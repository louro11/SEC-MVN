package hds;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Iterator;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class SoapHandler implements SOAPHandler<SOAPMessageContext> {
	
	public static final String CLASS_NAME = SoapHandler.class.getSimpleName();
	public static final String ENTITY_PROPERTY = "senderToPut";
	public static final String SENDER_HEADER = "senderName";
	public static final String SENDER_NS = "urn:senderNameNS";
	public static final String SIGNATURE_HEADER = "signature";
	public static final String SIGNATURE_NS = "urn:signatureNSA";
	public static final String NONCE_CONTENT = "nonceToPut";
	public static final String NONCE_HEADER = "nonce";
	public static final String NONCE_NS = "urn:nonceNS";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";


    public boolean handleMessage(SOAPMessageContext smc) {
    	
    	// Check message direction
    	Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		
    	if (outbound) {
				System.out.println(  "\n\n############################");
				System.out.println(  "#          Outbound        #");
				System.out.println(  "#        SOAP  Message     #");
				System.out.println(  "############################");
				System.out.println("  • Message number: "+ Nonce.getInstance().getCount());
					
				try {
					// Add header with username 
					putServerNameHeader(smc); //ATENCAO HEADER PODE SER NULL (RETURN DO CATCH NO putCompanyHeader(smc), PENSAR E ARRANJAR)!	
					smc.getMessage().saveChanges();
					
					// Add header with Nonce
					putNonceHeader(smc);
					smc.getMessage().saveChanges();
					
					// Get my Private Key
					PrivateKey privateKey = getServerPrivateKey();

					// Prepare content to be signed (Body + Username Header + Nonce Header)
					String message = getSoapString(smc);
					
					// Get the signature of the message = (PK[Digest])
					byte[] signature = makeDigitalSignature(message.getBytes(), privateKey);
					
					// Add header with signature
					putSignatureHeader(smc, signature);
					smc.getMessage().saveChanges();	
					
					// Print message
					System.out.println("  • Final SOAP message to be sent: ");
		            smc.getMessage().writeTo(System.out);
					System.out.println("\n");
					
				} catch (Exception e) {
					System.out.println(ANSI_RED + "Something went wrong at the Oubound SOAP Handler and message will be discarded!" + ANSI_RESET);
					return false;
				}
		}	

		else{
			System.out.println(  "\n############################");
			System.out.println(  "#           Inbound        #");
			System.out.println(  "#        SOAP  Message     #");
			System.out.println(  "############################");
			System.out.println("  • Message number: "+ Nonce.getInstance().getCount());
			
			try {
				System.out.println("  • Received SOAP message:  ");
				smc.getMessage().writeTo(System.out);
				
				// Get name of the received message sender from Header
				String headerValue = getSenderUsernameHeader(smc); //ATENCAO HEADER PODE SER NULL (RETURN DO CATCH NO putCompanyHeader(smc), PENSAR E ARRANJAR)!
					if(headerValue == null)
						return false;
					if(headerValue.contains("/"))
			    			return false;
				
				// Get Nonce of the received message from Header
				long nonce = getNonceHeader(smc);
				if(nonce == 0)
					return false;
				
				// Prepare content of message to verify (Body + Header + Nonce) 
				boolean v;
				byte[] digest = getDigest(smc);
				if(digest == null)
					return false;
				
				SOAPEnvelope se = getSoapEnvelope(smc);
				Name digestName = se.createName(SIGNATURE_HEADER, "H", SIGNATURE_NS);	
				Node nodeToRemove = null;
				NodeList nl = smc.getMessage().getSOAPHeader().getChildNodes();
				for(int i = 0; i < nl.getLength(); i++){
					if(nl.item(i).getNodeName().equals(digestName.getQualifiedName())){
						nodeToRemove = nl.item(i);
					}
				}
				
				if(nodeToRemove != null ){
					smc.getMessage().getSOAPHeader().removeChild(nodeToRemove);
					smc.getMessage().saveChanges();
					String message = getSoapString(smc);			
					System.out.println("  • Message to be verified with " + headerValue + "'s PublicKey: ");
					smc.getMessage().writeTo(System.out);
	
		            // Verify signature
					PublicKey pubKey = null;
					try{
						pubKey = getPublicKeyUsername(headerValue, "../keys/");
					}catch(Exception e){
						return false;
					}
					
					if(pubKey == null) {
						return false;
					}
					try{
						v = verifyDigitalSignature(digest, message.getBytes(), pubKey);
					}catch(InvalidKeyException e){
						return false;
					}
					
					if(!v)
						return false;
					
					if(Nonce.getInstance().checkinboundNonce(headerValue, nonce))
						return false;
					
					SOAPBody sb = smc.getMessage().getSOAPBody();
					if(sb == null)
						return false;
					
					Node methodNode = sb.getFirstChild();
					if(methodNode == null)
						return false;
					
					String method = methodNode.getLocalName();
					
					switch(method){
					
						case "register":
							if(!validPublicKeyFromArgument(smc, pubKey))
								return false;
							if(!validRegisterArguments(smc))
								return false;
							break;
							
						case "send_amount":
							if(!validPublicKeyFromArgument(smc, pubKey))
								return false;
							if(!validSendAmountArguments(smc))
								return false;
							break;
							
						case "check_account":
							if(!validPublicKeyFromArgument(smc, pubKey))
								return false;
							break;
							
						case "receive_amount":
							if(!validPublicKeyFromArgument(smc, pubKey))
								return false;
							if(!validReceiveAmountArguments(smc))
								return false;
							break;
							
						default:
							break;
					}
				}else{
					 
					return false;
				}
				return true;	

			} catch (SOAPException e) {
				System.out.printf("Inbound message Failed to get SOAP header because of %s%n", e);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("Something went wrong at the Inbound SOAP Handler!");
				e.printStackTrace();
			}
		}
    	return true;
    }

    private boolean validReceiveAmountArguments(SOAPMessageContext smc) throws Exception {
	    	Node arg1 = smc.getMessage().getSOAPBody().getFirstChild().getFirstChild().getNextSibling();
	    	if(arg1 == null)
	    		return false;
	    	String arg1String = arg1.getTextContent();
	    	//System.out.println("arg1String: " + arg1String);
	    	if(arg1String == null || "".equals(arg1String) || !validArgToInt(arg1String))
	    		return false;
			return true;
	}

	private boolean validArgToInt(String argString) {
		try{
			Integer.parseInt(argString);
			return true;
		}catch(Exception e){
			return false;
		}
	}

	private boolean validRegisterArguments(SOAPMessageContext smc) throws Exception {
	    	Node arg1 = smc.getMessage().getSOAPBody().getFirstChild().getFirstChild().getNextSibling();
	    	if(arg1 == null)
	    		return false;
	    	String arg1String = arg1.getTextContent();
	    	if(arg1String == null || "".equals(arg1String))
	    		return false;
			return true;
	}
    
    private boolean validSendAmountArguments(SOAPMessageContext smc) throws Exception {

	    	Node arg1 = smc.getMessage().getSOAPBody().getFirstChild().getFirstChild().getNextSibling();
	    	if(arg1 == null)
	    		return false;
	    	String arg1String = arg1.getTextContent();
	    	if(arg1String == null || "".equals(arg1String))
	    		return false;
	    	
	    	Node arg2 = arg1.getNextSibling();
	    	if(arg2 == null)
	    		return false;
	    	String arg2String = arg2.getTextContent();
	    	if(arg2String == null || "".equals(arg2String) || !validArgToFloat(arg2String))
	    		return false;
			return true;
	}

	private boolean validArgToFloat(String argString) {
		try{
			Float.parseFloat(argString);
			return true;
		}catch(Exception e){
			return false;
		}
	}

	private boolean validPublicKeyFromArgument(SOAPMessageContext smc, PublicKey pubKey) throws Exception{
		System.out.println("  • User permissions successfully verified!");
	    	Node argNode = smc.getMessage().getSOAPBody().getFirstChild();
	    	if(argNode == null)
	    		return false;
	    	Node argNode2 = argNode.getFirstChild();
	    	if(argNode2 == null)
	    		return false;
			String argKeyRA = argNode2.getTextContent();
	
			PublicKey argPubKeyRA = StringToPubliKey(argKeyRA);
			if(argPubKeyRA == null){
				return false;
			}
			if(!argPubKeyRA.equals(pubKey)){
				return false;
			}
			else
				System.out.println(ANSI_GREEN + "  • Signature successfully verified!" + ANSI_RESET);
				return true;
    }

    
    
    // Adds header with unique nonce to the message    
	private void putNonceHeader(SOAPMessageContext smc) throws Exception {
		
		// Get String from request context
		long propertyValue = (long) smc.get(NONCE_CONTENT);

		// Put String in SOAP header
			// Get SOAP envelope
			SOAPMessage msg = smc.getMessage();
			SOAPPart sp = msg.getSOAPPart();
			SOAPEnvelope se = sp.getEnvelope();

			// Add header
			SOAPHeader sh = se.getHeader();
			if (sh == null)
				sh = se.addHeader();

			// Add header element (name, namespace prefix, namespace)
			Name name = se.createName(NONCE_HEADER, "H", NONCE_NS);
			SOAPHeaderElement element = sh.addHeaderElement(name);

			// Add header element value
			long newValue = propertyValue;
			element.addTextNode(Long.toString(newValue));

			// Print header
			System.out.println("  • Added Header with Nonce: "+ newValue);
		
	}

	// Returns String of the whole message
	private String getSoapString(SOAPMessageContext smc) throws Exception{

    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	smc.getMessage().writeTo(stream);
    	return new String(stream.toByteArray(),"utf-8");
    }
		
    // Create signature and returns it
	private static byte[] makeDigitalSignature(byte[] bytes, PrivateKey privateKey) throws Exception {

		// get a signature object using the SHA-1 and RSA combo
		// and sign the plain-text with the private key
		Signature sig = Signature.getInstance("SHA1WithRSA");
		sig.initSign(privateKey);
		sig.update(bytes);
		byte[] signature = sig.sign();
		
		return signature;
	}

    // Adds header with signature to the message
	private void putSignatureHeader(SOAPMessageContext smc, byte[] signature ) throws Exception{
    	
		// put token in request SOAP header
		try {
			
			// get SOAP envelope
			SOAPMessage msg = smc.getMessage();
			SOAPPart sp = msg.getSOAPPart();
			SOAPEnvelope se = sp.getEnvelope();

			// add header
			SOAPHeader sh = se.getHeader();
			
			if (sh == null)
				sh = se.addHeader();

			// add header element (name, namespace prefix, namespace)
			Name digestName = se.createName(SIGNATURE_HEADER, "H", SIGNATURE_NS);
			SOAPHeaderElement element = sh.addHeaderElement(digestName);
			
			// add header element value
			element.addTextNode(printBase64Binary(signature));
			
			// print header
			System.out.println("  • Added Header with signature and length in byte[]: " + signature.length);
			
		} catch (SOAPException e) {
			System.out.printf("Outbound message Transporter-WS-Client Failed to add SOAP header because of %s%n", e);
		}
		
    }

	private String getSenderUsernameHeader(SOAPMessageContext smc) throws SOAPException {
		
		SOAPEnvelope se = getSoapEnvelope(smc);
		SOAPHeader sh = getSoapHeader(smc);

		// check header
		if (se == null) {
			System.out.println("Envelope is null!");
			return null;
		}
		
		// check header
		if (sh == null) {
			System.out.println("SOAPHeader is null!");
			return null;
		}

		// get first header element
		Name name = se.createName(SENDER_HEADER, "H", SENDER_NS);
		Iterator<?> it = sh.getChildElements(name);
		
		// check header element
		if (!it.hasNext()) {
			System.out.printf("Inbound message Header element %s not found.%n", SENDER_HEADER);
			return null;
		}
		SOAPElement element = (SOAPElement) it.next();

		// check header
		if (element == null) {
			System.out.println("Inbound message Header not found.");
			return null;
		}
		// get header element value
		String headerValue = element.getValue();
		System.out.println("\n  • Message from: " + headerValue);

		return headerValue;
	}

	private long getNonceHeader(SOAPMessageContext smc) throws SOAPException {
		SOAPEnvelope se = getSoapEnvelope(smc);
		SOAPHeader sh = getSoapHeader(smc);

		// check header
		if (se == null) {
			System.out.println("Inbound message Header not found.");
			return 0;
		}
				
		// check header
		if (sh == null) {
			System.out.println("Inbound message Header not found.");
			return 0;
		}

		// get first header element
		Name name = se.createName(NONCE_HEADER, "H", NONCE_NS);
		Iterator<?> it = sh.getChildElements(name);
		
		// check header element
		if (!it.hasNext()) {
			System.out.printf("Inbound message Transporter-WS-Client Header element %s not found.%n", SENDER_HEADER);
			return 0;
		}
		
		
		SOAPElement element = (SOAPElement) it.next();

		
		// check header
		if (element == null) {
			System.out.println("Inbound message Header not found.");
			return 0;
		}
		// get header element value
		String headerValue = element.getValue();
		if (headerValue == null) {
			System.out.println("Inbound message Header not found.");
			return 0;
		}
		try{
			long nonce = Long.valueOf(headerValue).longValue();
			System.out.println("  • Got Nonce: " + headerValue);
			return nonce;
		}catch(Exception e){
			return 0;
		}
	}

	private byte[] getDigest(SOAPMessageContext smc) throws SOAPException{
		SOAPEnvelope se = getSoapEnvelope(smc);
		SOAPHeader sh = getSoapHeader(smc);

		// check header
		if (se == null) {
			System.out.println("Inbound message: Header not found.");
			return null;
		}
		// check header
		if (sh == null) {
			System.out.println("Inbound message: Header not found.");
			return null;
		}

		// get first header element
		Name name = se.createName(SIGNATURE_HEADER, "H", SIGNATURE_NS);
		Iterator<?> it = sh.getChildElements(name);
		
		// check header element
		if (!it.hasNext()) {
			System.out.printf("Inbound message Header element %s not found.%n", SENDER_HEADER);
			return null;
		}
		SOAPElement element = (SOAPElement) it.next();

		// check header
		if (element == null) {
			System.out.println("Inbound message: Header not found.");
			return null;
		}
		
		// get header element value
		String headerValue = element.getValue();
				
		// check header
		if (headerValue == null) {
			System.out.println("Inbound message: Header not found.");
			return null;
		}
		// print header
		System.out.println("  • Got Header with signature and length in byte[]: " + 		parseBase64Binary(headerValue).length
);
		return parseBase64Binary(headerValue);

	}
    
	private SOAPEnvelope getSoapEnvelope(SOAPMessageContext smc) throws SOAPException {
    	SOAPMessage msg = smc.getMessage();
		SOAPPart sp = msg.getSOAPPart();
		return sp.getEnvelope();
	}
	
	private SOAPHeader getSoapHeader(SOAPMessageContext smc) throws SOAPException {
		return getSoapEnvelope(smc).getHeader();
	}
	
	private static boolean verifyDigitalSignature(byte[] cipherDigest, byte[] bytes, PublicKey publicKey)
			throws Exception {

		boolean verification = false;
		// verify the signature with the public key
		System.out.println("\n  • Verifying signature...");

		Signature sig = Signature.getInstance("SHA1WithRSA");
		sig.initVerify(publicKey);
		sig.update(bytes);
		try {

			verification = sig.verify(cipherDigest);
			if (verification) System.out.println(ANSI_GREEN + "  • Signature successfully verified!" + ANSI_RESET);
			if (!verification) System.out.println(ANSI_RED + "  • Signature doesn't match! Be careful, there is a Man in the Middle around..." +  ANSI_RESET);
			return verification;
		} catch (SignatureException se) {
			System.err.println("Caught exception while verifying signature " + se);
			return false;
		}
	}
	
	// Interface method
	public boolean handleFault(SOAPMessageContext smc) {
		return handleMessage(smc);
    }
	
	// Interface method
	@Override
    public void close(MessageContext messageContext) {
    }
    
	// Interface method
	@Override
	public Set<QName> getHeaders() {
		return null;
	}
	
	// Gets Public Key from Username
	private static PublicKey getPublicKeyUsername(String username, String pathToKeystore) {
		try {
			File privKeyFile = new File(pathToKeystore + username + "_PublicKey");
			Charset charset = Charset.forName("UTF-8");
			BufferedReader reader = Files.newBufferedReader(privKeyFile.toPath(), charset);
			String key = reader.readLine();
			return StringToPubliKey(key);
		} catch (Exception e) {
			return null; 
		} 
	}
	
	// Adds header with sender identification to the message
    private String putServerNameHeader(SOAPMessageContext smc) throws Exception{

    	// Get String with identification from request context
		String propertyValue = (String) smc.get(ENTITY_PROPERTY);

		// Put String in request SOAP header
		
			// Get SOAP envelope
			SOAPMessage msg = smc.getMessage();
			SOAPPart sp = msg.getSOAPPart();
			SOAPEnvelope se = sp.getEnvelope();

			// Add header
			SOAPHeader sh = se.getHeader();
			if (sh == null)
				sh = se.addHeader();

			// Add header element (name, namespace prefix, namespace)
			Name name = se.createName(SENDER_HEADER, "H", SENDER_NS);
			SOAPHeaderElement element = sh.addHeaderElement(name);

			// Add header element value
			String newValue = propertyValue;
			element.addTextNode(newValue);

			// Print header
			System.out.println("  • Added Header with my username: "+ newValue);
			
			return newValue;
    }
    
    // Gets Private Key
 	private static PrivateKey getServerPrivateKey() throws Exception {
 			File privKeyFile = new File("./HDS_SERVER_PrivateKey");
 			Charset charset = Charset.forName("UTF-8");
 			BufferedReader reader = Files.newBufferedReader(privKeyFile.toPath(), charset);
 			String key = reader.readLine();
 			byte[] encoded = parseBase64Binary(key);
 			KeyFactory kf = KeyFactory.getInstance("RSA");
 			return kf.generatePrivate(new PKCS8EncodedKeySpec(encoded));
 	}
 	
	private static PublicKey StringToPubliKey(String keyS) {
		try{
			byte[] publicBytes = convertStringToByteArray(keyS);
			return convertByteArrayToPubKey(publicBytes, "RSA");
		}catch(Exception e){
			return null;
		}
	}
	
	public static byte[] convertStringToByteArray(String string) throws Exception {
		return parseBase64Binary(string);
	}
	
	public static PublicKey convertByteArrayToPubKey(byte publicKeyBytes[], String algorithm) throws Exception {
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
	}
}
