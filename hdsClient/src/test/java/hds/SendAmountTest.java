package hds;

import org.junit.*;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class SendAmountTest {
	
		@BeforeClass
	    public static void oneTimeSetUp() {
	    }
	
	    @AfterClass
	    public static void oneTimeTearDown() {
	    }
    
	    // members
		
	    HDSClient client1 = new HDSClient("./PrivKeys/", "../keys/");
		HDSClient client2 = new HDSClient("./PrivKeys/", "../keys/");
		HDSClient client3 = new HDSClient("./PrivKeys/", "../keys/");
	
		String keyS1 = client1.getPublicKeyString();
		String keyS2 = client2.getPublicKeyString();
		String keyS3 = client3.getPublicKeyString();
	
	    // initialization and clean-up for each test
	    @Before
	    public void setUp() throws InvalidInputException_Exception, FailToLogRequestException_Exception{
	    	client1.setClientUsername("client1test123456789REGISTER");
	    	client2.setClientUsername("client2test123456789REGISTER");
	    	client3.setClientUsername("client3test123456789REGISTER");
	    	client1.setPublicKeyString(keyS1);
	    	client2.setPublicKeyString(keyS2);
	    	try{
	    		client1.register();
	    		client2.register();
	    	}catch(InvalidInputException_Exception e){}
	    }
	
	    @After
	    public void tearDown() {
//	       	client1.setPublicKeyString(keyS1);
//	    	client2.setPublicKeyString(keyS2);
	    }
	    
	    //tests
	    
	    @Test
	    public void ok() throws FailToLogRequestException_Exception, InvalidInputException_Exception{
	    	List<Transfer> transfers = new ArrayList<Transfer>();
	    	CheckResult cr;
	    	int transferid;
	    	
	    	client1.sendAmount("client2test123456789REGISTER", 500.0f);
	    	cr = client1.checkAccount();
	    	assertEquals(cr.getBalance(), 0.0f, 0);
	    	
	    	cr = client2.checkAccount();
	    	assertEquals(cr.getBalance(), 500f, 0);
	    	transfers = cr.getTransfersIn();
	    	Transfer t = transfers.get(transfers.size()-1);	
	    	transferid = t.getId(); 
	    	client2.receiveAmount(transferid);
	    	assertEquals(cr.getBalance(), 1000f, 0);
	    }
	    
	    @Test
	    public void decimalFlowOk()throws FailToLogRequestException_Exception, InvalidInputException_Exception{
	    	List<Transfer> transfers = new ArrayList<Transfer>();
	    	CheckResult cr;
	    	int transferid;
	    	client2.sendAmount("client1test123456789REGISTER", 0.5f);
	    	cr = client2.checkAccount();
	    	assertEquals(cr.getBalance(), 999.5f, 0);
	    	
	    	cr = client1.checkAccount();
	    	assertEquals(cr.getBalance(), 0.0f, 0);
	    	transfers = cr.getTransfersIn();
	    	Transfer t = transfers.get(transfers.size()-1);	
	    	transferid = t.getId(); 
	    	client1.receiveAmount(transferid);
	    	assertEquals(cr.getBalance(), 0.5f, 0);
	    }
	    
	    @Test
	    public void  transferInListOK() throws InvalidInputException_Exception, FailToLogRequestException_Exception  {
	    	List<Transfer> transfers = new ArrayList<Transfer>();
	    	CheckResult cr;
	    	client2.sendAmount("client1test123456789REGISTER", 2);
	    	
	    	cr = client2.checkAccount();
	    	transfers = cr.getTransfersIn();
	    	assertEquals(true, transfers.isEmpty());
	    	
	    	cr = client1.checkAccount();
	    	transfers=cr.getTransfersIn();
	    	assertEquals(false, transfers.isEmpty());
	    	
	    }
	    
	    
	    @Test (expected = InvalidInputException_Exception.class) 
	    public void sendZeroAmount()throws InvalidInputException_Exception, FailToLogRequestException_Exception{
	    	client1.sendAmount("client2test123456789REGISTER", 0);
	    }
	    
	    @Test (expected = InvalidInputException_Exception.class) 
	    public void sendDecimalZeroAmount()throws InvalidInputException_Exception, FailToLogRequestException_Exception{
	    	client1.sendAmount("client2test123456789REGISTER", 0.0f);
	    }
	    
	    @Test(expected = InvalidInputException_Exception.class) 
	    public void  insuficientFunds() throws InvalidInputException_Exception, FailToLogRequestException_Exception  {
	    	client1.sendAmount("client2test123456789REGISTER", 999999999);
	    }
	    
	    @Test(expected = InvalidInputException_Exception.class) 
	    public void  negativeAmount() throws InvalidInputException_Exception, FailToLogRequestException_Exception  {
	    	client1.sendAmount("client2test123456789REGISTER", -10);
	    }
	    
	    @Test(expected = InvalidInputException_Exception.class) 
	    public void sendFromUnregisteredUser() throws InvalidInputException_Exception, FailToLogRequestException_Exception  {
	    	client3.sendAmount("client1test123456789REGISTER", 100);
	    }
	    
	    @Test(expected = InvalidInputException_Exception.class) 
	    public void sendToUnregisteredUser() throws InvalidInputException_Exception, FailToLogRequestException_Exception  {
	    	client1.sendAmount("client3test123456789REGISTER", 100);
	    }
}







