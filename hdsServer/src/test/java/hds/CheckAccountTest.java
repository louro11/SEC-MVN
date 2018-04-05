package hds;

import org.junit.Assert;

import static org.junit.Assert.*;

import java.security.PublicKey;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
public class CheckAccountTest {

		@BeforeClass
	    public static void oneTimeSetUp() {
	    }

	    @AfterClass
	    public static void oneTimeTearDown() {
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
	    	ArrayList<Transfer> transfersIn = new ArrayList<Transfer>();
	    	float balance=0, newbalance=0;
	    	int transferscount, id;
	    	CheckResult cr;
	    	ArrayList<Transfer> transfers = new ArrayList<Transfer>();
	    
	    	cr = server.check_account(keyS1);   	
	    	balance = cr.getBalance();
	    	transfersIn= cr.getTransfersIn();
	    	transferscount=transfersIn.size();
	    	
	    	server.send_amount(keyS2, keyS1, 100);	    	
	    	cr = server.check_account(keyS1);
	    	
	    	transferscount++;
	    	assertEquals(transfersIn.size(), transferscount);
	    	assertEquals(balance, cr.getBalance(),0);
	    	    	
	    	transfers = server.getTransfers();	    	
	    	Transfer t = transfers.get(transfers.size()-1);	
	    	id = t.getId(); 	
	    	server.receive_amount(keyS1, id); 
	    	cr = server.check_account(keyS1);
	    	transferscount--;
	    	assertEquals(transfersIn.size(), transferscount);
	    	newbalance = cr.getBalance();
	    	assertEquals(balance, newbalance-100,0);
	    	
	    }
		
		@Test
		public void transferListResultOK()throws InvalidInputException, FailToLogRequestException{
			ArrayList<Transfer> transfersIn = new ArrayList<Transfer>();
			CheckResult cr;
			int transfercount;
			
			cr=server.check_account(keyS1);
			transfersIn = cr.getTransfersIn();	
			transfercount=transfersIn.size();
			server.send_amount(keyS2, keyS1, 100);
			
			cr = server.check_account(keyS1); 
			assertTrue(!cr.getTransfersIn().isEmpty());
			transfercount++;
			assertEquals(cr.getTransfersIn().size(), transfercount,0);
			
		}
		
		@Test
		public void balanceResultOK()throws InvalidInputException, FailToLogRequestException{
			ArrayList<Transfer> transfers = new ArrayList<Transfer>();
			CheckResult cr;
			float balance;
			int id;
			
			cr=server.check_account(keyS1);
			balance = cr.getBalance();
			server.send_amount(keyS2, keyS1, 100);
			
			transfers = server.getTransfers();	    	
	    	Transfer t = transfers.get(transfers.size()-1);	
	    	id = t.getId(); 	
			
			server.receive_amount(keyS1, id);
			cr = server.check_account(keyS1); 
			assertTrue(cr.getBalance()==balance + 100);
			
		}
		   
	    @Test(expected = InvalidInputException.class) 
	    public void  checkUnregisteredAccount() throws InvalidInputException, FailToLogRequestException {
	    	server.check_account(keyS3);
	    }
	    
	    
	    
}