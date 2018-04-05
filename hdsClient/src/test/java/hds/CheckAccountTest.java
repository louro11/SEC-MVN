package hds;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

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
    	client3.setClientUsername("client2test123456789REGISTER");
    	client1.setPublicKeyString(keyS1);
    	client2.setPublicKeyString(keyS2);
    	
    	try{
    		client1.register();
    		client2.register();
    	}catch(InvalidInputException_Exception e){}
    }

    @After
    public void tearDown() {
//       	client1.setPublicKeyString(keyS1);
//    		client2.setPublicKeyString(keyS2);
    }
    
    //tests
    
    @Test
    public void ok() throws FailToLogRequestException_Exception, InvalidInputException_Exception{
    	List<Transfer> transfers = new ArrayList<Transfer>();
    	CheckResult cr;
    	int transferid;
    	
    	cr = client1.checkAccount();
    	assertEquals(cr.getBalance(), 500f, 0);
    	assertEquals(true, cr.getTransfersIn().isEmpty());
    	
    	cr = client2.checkAccount();
    	assertEquals(cr.getBalance(), 500f, 0);
    	assertEquals(true, cr.getTransfersIn().isEmpty());
    	
    	client1.sendAmount("client2test123456789REGISTER", 500.0f);
    	
    	cr = client1.checkAccount();
    	assertEquals(cr.getBalance(), 0.0f, 0);
    	assertEquals(true, cr.getTransfersIn().isEmpty());
    	
    	cr = client2.checkAccount();
    	assertEquals(cr.getBalance(), 500f, 0);
    	assertEquals(false, cr.getTransfersIn().isEmpty());
    	
    	transfers = cr.getTransfersIn();
    	Transfer t = transfers.get(transfers.size()-1);	
    	transferid = t.getId(); 
    	
    	client2.receiveAmount(transferid);
    	
    	cr = client2.checkAccount();
    	assertEquals(cr.getBalance(), 1000f, 0);
    	assertEquals(true, cr.getTransfersIn().isEmpty());
    	
    }
    
    @Test(expected = RuntimeException.class) 
    public void  checkUnregisteredAccount() throws InvalidInputException_Exception, FailToLogRequestException_Exception {
    	client3.checkAccount();
    }
    
}
