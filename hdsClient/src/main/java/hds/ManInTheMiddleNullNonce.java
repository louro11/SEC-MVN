package hds;

import java.io.IOException;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.NodeList;

public class ManInTheMiddleNullNonce implements SOAPHandler<SOAPMessageContext> {

	public boolean handleMessage(SOAPMessageContext smc)  {
		// Check message direction
    	Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		if (outbound){
			System.out.println("\n\n Man in the middle -.-  : ");
			
			try {
				
				smc.getMessage().getSOAPHeader().getFirstChild().getNextSibling().setTextContent(null);
				//smc.getMessage().getSOAPHeader().getFirstChild().getNextSibling().setTextContent("ThisIsAString");
				//smc.getMessage().getSOAPHeader().getFirstChild().getNextSibling().setTextContent("92387490812374012364872316498732164897231647986231879462389746329817463287946832764823719468321796428137462381746238746238974623874623874682376428374613287462387463127894623874632874623897463218476238476328746238476324873264");
				//smc.getMessage().getSOAPHeader().getFirstChild().getNextSibling().setTextContent("0");
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

}
