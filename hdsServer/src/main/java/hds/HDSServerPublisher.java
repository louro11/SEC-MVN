//Publicação do Server 
//Verificar WSDL em http://127.0.0.1:9879/hds?wsdl

package hds;


import javax.xml.ws.Endpoint;


public class HDSServerPublisher {

	public static void main(String[] args)
	{
		Endpoint endpoint=null;
		String url="http://127.0.0.1:9879/hds";
		System.out.println("\n💰💰💰 HDS Coin Server 💰💰💰");
		System.out.println("Publishing to 127.0.0.1:9879...");
		try {
			HDSServerImpl port = new HDSServerImpl();
			endpoint = Endpoint.create(port);
			endpoint.publish(url);
			System.out.println("WSDL available at: http://127.0.0.1:9879/hds?wsdl");
			System.out.println("Waiting requests. Press ENTER to end the Server.");
			System.in.read();
		} catch (Exception e) {
			System.out.printf("Caught exception: ", e);
			e.printStackTrace();
		} finally {
			if (endpoint != null) {
				endpoint.stop();
				System.out.printf("Stopped %s successfully! Bye!\n",  url);
			}
		}
	} 
}
