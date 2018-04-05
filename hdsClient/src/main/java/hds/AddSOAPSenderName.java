package hds;

import java.io.IOException;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public class AddSOAPSenderName implements SOAPHandler<SOAPMessageContext> {

	public boolean handleMessage(SOAPMessageContext smc)  {
		// Check message direction
    	Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (outbound){
			System.out.println("\n\n Man in the middle -.-  : ");
			try {
				
				putServerNameHeader(smc, "UnexpectedHeader");
				smc.getMessage().saveChanges();
				smc.getMessage().writeTo(System.out);

			} catch (SOAPException | IOException e) {
				e.printStackTrace();
			}
		}
		

		return true;
	}
	
	@Override
	public void close(MessageContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String putServerNameHeader(SOAPMessageContext smc, String propertyValue){

		System.out.println("smc");
		System.out.println(smc);
		System.out.println("");
		
    	// Get String with identification from request context
		System.out.println("propertyValue");
		System.out.println(propertyValue);
		System.out.println("");
		// Put String in request SOAP header
		try {
			// Get SOAP envelope
			SOAPMessage msg = smc.getMessage();
			SOAPPart sp = msg.getSOAPPart();
			SOAPEnvelope se = sp.getEnvelope();
			System.out.println("msg");
			System.out.println(msg);
			System.out.println("");
			System.out.println("sp");
			System.out.println(sp);
			System.out.println("");
			System.out.println("se");
			System.out.println(se);
			System.out.println("");

			// Add header
			SOAPHeader sh = se.getHeader();
			if (sh == null)
				sh = se.addHeader();

			// Add header element (name, namespace prefix, namespace)
			Name name = se.createName("ManInTheMiddle", "H", "ManInTheMiddleNS");
			SOAPHeaderElement element = sh.addHeaderElement(name);
			System.out.println("element");
			System.out.println(element);
			System.out.println("");
			// Add header element value
			String newValue = propertyValue;
			element.addTextNode(newValue);

			// Print header
			System.out.println("  • Added Header with my username: "+ newValue);
			
			return newValue;

		} catch (SOAPException e) {
			System.out.printf("Outbound message - Failed to add SOAP header because of %s%n", e);
			System.err.println((e.getMessage()));
			return null;
		}

    }

}
