package hds;

import static org.junit.Assert.*;

import java.security.PublicKey;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReceiveAmountTest {

	
		@BeforeClass
	    public static void oneTimeSetUp() {
	    	System.out.println("hi");
	    }

	    @AfterClass
	    public static void oneTimeTearDown() {
	    	System.out.println("bye");
	    }

	    // members
	    HDSServerImpl server;
	    String keyS1 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJZTyXjVttDI2gbqdAWShLpndoD/Wng68xR2nMvZ6H2Qor8ZDNnsAbXNCBpFZFUhp0ka0dt8M/L/uH/cFbDw8kn8U4d8gDJTLxwMtF6bQVkNDyoWixj5Ir+Kj5J7XSkX8sDzlTOahZ549URPX/8uaq6SfsQx2hTOJjnfuMp8r2EQIDAQAB";
	    String keyS2 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDiCHFJagc67ZQZtCEXzZ19rE5kLW4n5geDB/0pDAx+2ZYlOfDsx91J2xE6UANJHmmPG4W9dhPN4J1R12ncFUZ2e6Qd3swA4vswwGbL9Sq+Fo/H3wOzwp15K1M6QjsEhx/JpcpToN35GTLjlgrE2t5loWFE3gL0pL6IYq0PQQLuaQIDAQAB";
	    String keyS3 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDUMkQhP5ProOyFgJRKAOT588ATl3nYJLNJw+PNX1dCtPBJbCgSuZ0jPku1H9K8a53wL7aIU8yCL+3FgDIY77KQF1QzkHWHQlI/a45SdYe5TIDYdYt2jBc5CNBphKB6M8JDQKna7TSjK4RrMJA73Ix+rFlq4kb/b90QIshgEO4NmQIDAQAB";

	    
	    // initialization and clean-up for each test
	    @Before
	    public void setUp() throws InvalidInputException, FailToLogRequestException{
	    	server = new HDSServerImpl("tests");
	    	server.register(keyS1, "carlos");
	    	server.register(keyS2, "miguel");
	    }

	    @After
	    public void tearDown() {
	    	server = null;
	    }
	
	    
		@Test
	    public void  ok() throws InvalidInputException, FailToLogRequestException {
			float balance=0, newbalance=0;
			int id;
			ArrayList<Transfer> transfers = new ArrayList<Transfer>();
	    	PublicKey key1 = server.StringToPubliKey(keyS1);
	    	Account acc1 = server.getAccount(key1);
	    	balance = acc1.getBalance();
	    	server.send_amount(keyS2, keyS1, 100);
	    	
	    	transfers = server.getTransfers();	    	
	    	Transfer t = transfers.get(transfers.size()-1);	
	    	id = t.getId(); 
	    	
	    	server.receive_amount(keyS1, id); //transfer id?
	    	newbalance = acc1.getBalance();
	    	assertTrue(balance == newbalance-100);
	    }
		
		
		
		//test, receive transfer with wrong destination
		
		@Test(expected = InvalidInputException.class) 
	    public void  receiveWrongDestinationTransfer() throws InvalidInputException, FailToLogRequestException {
			int id;
	    	server.send_amount(keyS2, keyS1, 100);
	    	
	    	ArrayList<Transfer> transfers = new ArrayList<Transfer>();
	    	transfers = server.getTransfers();
	    	
	    	Transfer t = transfers.get(transfers.size()-1);	
	    	id = t.getId();
	    	
	    	server.receive_amount(keyS3, id);
	    }
		
		
		@Test(expected = InvalidInputException.class) 
	    public void  receiveTwiceSameTransfer() throws InvalidInputException, FailToLogRequestException {
			int id;
	    	server.send_amount(keyS2, keyS1, 100);
	    	
	    	ArrayList<Transfer> transfers = new ArrayList<Transfer>();
	    	transfers = server.getTransfers();
	    	
	    	Transfer t = transfers.get(transfers.size()-1);	
	    	id = t.getId();
	    	server.receive_amount(keyS1, id);
	    	server.receive_amount(keyS1, id);
	    }
		
		
	    @Test(expected = InvalidInputException.class) 
	    public void  negativeIDTransfer() throws InvalidInputException, FailToLogRequestException {
	    	server.send_amount(keyS2, keyS1, 100);
	    	server.receive_amount(keyS1, -1);
	    }
	    
	    @Test(expected = InvalidInputException.class) 
	    public void  nullReceiver() throws InvalidInputException, FailToLogRequestException {
	    	server.send_amount(keyS2, keyS1, 100);
	    	server.receive_amount(null, 1);
	    }
	    
//	    @Test(expected = InvalidInputException.class) 
//	    public void  nullID() throws InvalidInputException, FailToLogRequestException {
//	    	server.send_amount(keyS2, keyS1, 100);
//	    	server.receive_amount(keyS1, (Integer) null);
//	    }
//	    
	    
}