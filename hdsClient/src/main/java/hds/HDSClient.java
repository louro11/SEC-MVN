package hds;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.Handler;

public class HDSClient {
	
	private HDSServerImplService hdsService = new HDSServerImplService();
	private HDSServer hds = hdsService.getPort(HDSServer.class);
	private String clientUsername = null;
	private PublicKey publicKey;
	private byte[] publicKeyByteArray;
	private byte[] privateKeyByteArray;
	private String publicKeyString;
	private String publicKeyStorePath;
	private String privateKeyStorePath;

	
	public HDSClient(String privateKeyStorePath, String publicKeyStorePath) {
		//Generate private and public keys
		try{
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
	        keyGen.initialize(1024);
	        KeyPair keypair = keyGen.genKeyPair();
	        this.publicKey = keypair.getPublic();
	        System.out.println(publicKey);
	        PrivateKey privateKey = keypair.getPrivate();
	        this.publicKeyByteArray = publicKey.getEncoded();
	        this.privateKeyByteArray = privateKey.getEncoded();
	        this.publicKeyString = (Base64.getEncoder().encodeToString(publicKeyByteArray));
	        this.publicKeyStorePath = publicKeyStorePath;
	        this.privateKeyStorePath = privateKeyStorePath;
			//Setup handler
			setupHandler();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public HDSClient(PrivateKey priv, PublicKey pub, String privateKeyStorePath, String publicKeyStorePath) {
		try{
			this.publicKey = pub;
	        PrivateKey privateKey = priv;
	        this.publicKeyByteArray = publicKey.getEncoded();
	        this.privateKeyByteArray = privateKey.getEncoded();
	        this.publicKeyString = (Base64.getEncoder().encodeToString(publicKeyByteArray));
	        this.publicKeyStorePath = publicKeyStorePath;
	        this.privateKeyStorePath = privateKeyStorePath;
			//Setup handler
			setupHandler();
		}catch(Exception e){
			e.printStackTrace();
		}	
	}

	public void register() throws FailToLogRequestException_Exception, InvalidInputException_Exception{
        //Put keys in keystores
        putKeyInKeyStore(privateKeyByteArray, privateKeyStorePath, "_PrivateKey");
		putKeyInKeyStore(publicKeyByteArray, publicKeyStorePath, "_PublicKey");
		putEntityAndNonce();
		hds.register(getPublicKeyString(), getClientUsername());
	}
	
	
	public void sendAmount(String username, float amount) throws FailToLogRequestException_Exception, InvalidInputException_Exception{
		String keyD = getKeyFromUsername(username);
		putEntityAndNonce();
		System.out.println("#############");System.out.println("#############");System.out.println("#############");
		System.out.println("DEST:" + getKeyFromUsername(username));
		System.out.println("SEND:" + getKeyFromUsername(this.clientUsername));
		System.out.println("#############");System.out.println("#############");System.out.println("#############");
		try{
			hds.sendAmount(getPublicKeyString(), keyD, amount);
		}catch(WebServiceException e){
			throw new RuntimeException("YOU SHALL NOT PASS");
		}
	}
	
	public CheckResult checkAccount() throws InvalidInputException_Exception{
		putEntityAndNonce();
		try{
			return hds.checkAccount(getPublicKeyString());
		}catch(WebServiceException e){
			throw new RuntimeException("YOU SHALL NOT PASS");
		}
	}
	
	public void receiveAmount(int id) throws FailToLogRequestException_Exception, InvalidInputException_Exception{
		putEntityAndNonce();
		try{
			hds.receiveAmount(getPublicKeyString(), id);
		}catch(WebServiceException e){
			throw new RuntimeException("YOU SHALL NOT PASS");
		}
	}
	
	public AuditResult audit(String username) throws InvalidInputException_Exception{
		String key = getKeyFromUsername(username);
		putEntityAndNonce();
		try{
			return hds.audit(key);
		}catch(WebServiceException e){
			throw new RuntimeException("YOU SHALL NOT PASS");
		}
	}
	
	public HDSServer getHDS() {
		return hds;
	}
	
	public void putEntityAndNonce(){
		BindingProvider bindingProvider = (BindingProvider) hds;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(SoapHandler.ENTITY_PROPERTY, getClientUsername());
		requestContext.put(SoapHandler.NONCE_CONTENT, Nonce.getInstance().generateNonce());
		//requestContext.put(SoapHandler.NONCE_CONTENT, 123456789L);
		requestContext.put(SoapHandler.relativePathToPrivateKey, privateKeyStorePath);
		String copy = privateKeyStorePath;
		requestContext.put(SoapHandler.relativePathToServerPublicKey, copy.replace("PrivKeys/", ""));
	}
	
	public void setupHandler(){
		Binding binding = ((BindingProvider)hds).getBinding();
	    List<Handler> handlerList = binding.getHandlerChain();
	    handlerList.add(new SoapHandler());
	    
	    //CHANGE AMOUNT
	    //handlerList.add(new ManInTheMiddle());
	    
	    //HEADERS
	    //handlerList.add(new ManInTheMiddleNullSenderName());
	    //handlerList.add(new ManInTheMiddleNullNonce());
	    //handlerList.add(new ManInTheMiddleNullSignature());
	    //handlerList.add(new AddSOAPSenderName());
	    //handlerList.add(new RemoveSOAPNonce());
	    //handlerList.add(new RemoveSOAPDigest());
	    //handlerList.add(new RemoveSOAPHeaders());
	    
	    //BODY
	    //handlerList.add(new RemoveSOAPArgs());
	    //handlerList.add(new RemoveSOAPArgsNULL());
	    //handlerList.add(new AddSOAPArgs());

	    binding.setHandlerChain(handlerList);
	}


	public String getClientUsername() {
		return clientUsername;
	}

	public void setClientUsername(String clientUsername) {
		this.clientUsername = clientUsername;
	}
	
	public void putKeyInKeyStore(byte[] publicBytes, String pathToKeystore, String type) {
		try {
			SavePemPublicKey(Base64.getEncoder().encodeToString(publicBytes), pathToKeystore + getClientUsername() + type);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public  void  SavePemPublicKey(String key, String filename) throws Exception {
        File f = new File(filename);
        FileOutputStream fos = new FileOutputStream(f);
        DataOutputStream dos = new DataOutputStream(fos);
        dos.writeBytes(key);
        dos.flush();
        dos.close();
    }
	
	public String getKeyFromUsername(String username) {
		try {
			byte[] encoded = Files.readAllBytes(Paths.get(publicKeyStorePath + username + "_PublicKey"));
			return new String(encoded, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
			return null; 
		} 
	}

	public String getPublicKeyString() {
		return publicKeyString;
	}
	
	public void setPublicKeyString(String pubkey) {
		if(pubkey != null){
			try{
				this.publicKeyString = pubkey;
				this.publicKeyByteArray = convertStringToByteArray(pubkey);
				this.publicKey = StringToPubliKey(pubkey);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		else
			this.publicKeyString = pubkey;
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
	
	public void permissionsTest(String anotherPublicKey, String anotherUsername) throws FailToLogRequestException_Exception, InvalidInputException_Exception{
		putEntityAndNonce();
		try{
			hds.sendAmount(anotherPublicKey, this.getPublicKeyString(), 100);
		}catch(WebServiceException e){
			throw new RuntimeException("YOU SHALL NOT PASS");
		}
	}
}

