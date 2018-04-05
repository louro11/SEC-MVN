package hds;

import org.junit.*;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReceiveAmountTest {
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
	HDSClient client4 = new HDSClient("./PrivKeys/", "../keys/");

	String keyS1 = client1.getPublicKeyString();
	String keyS2 = client2.getPublicKeyString();
	String keyS3 = client3.getPublicKeyString();
	String keyS4 = client4.getPublicKeyString();
	

    // initialization and clean-up for each test
    @Before
    public void setUp() throws InvalidInputException_Exception, FailToLogRequestException_Exception{
    	client1.setClientUsername("client1test123456789REGISTER");
    	client2.setClientUsername("client2test123456789REGISTER");
    	client3.setClientUsername("client3test123456789REGISTER");
    	client4.setClientUsername("client4test123456789REGISTER");
    	client1.setPublicKeyString(keyS1);
    	client2.setPublicKeyString(keyS2);
    	client3.setPublicKeyString(keyS3);
    	client4.setPublicKeyString(keyS4);
    	try{
    		client1.register();
    		client2.register();
    	}catch(InvalidInputException_Exception e){}
    }

    @After
    public void tearDown() {
    	
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
    
    @Test(expected = InvalidInputException_Exception.class) 
    public void  receiveTwiceSameTransfer() throws InvalidInputException_Exception, FailToLogRequestException_Exception {
    	List<Transfer> transfers = new ArrayList<Transfer>();
    	CheckResult cr;
    	int transferid;
    	client1.sendAmount("client2test123456789REGISTER", 100);
    	
    	cr = client2.checkAccount();
    	transfers = cr.getTransfersIn();
    	Transfer t = transfers.get(transfers.size()-1);	
    	transferid = t.getId(); 
    	
    	client2.receiveAmount(transferid);
    	client2.receiveAmount(transferid);
    }
    
    @Test(expected = RuntimeException.class) 
    public void  receiveWrongDestinationTransfer() throws InvalidInputException_Exception, FailToLogRequestException_Exception {
    	List<Transfer> transfers = new ArrayList<Transfer>();
    	CheckResult cr;
    	int transferid;
		
		client2.sendAmount("client4test123456789REGISTER", 100f);
		
		cr = client4.checkAccount();
		transfers=cr.getTransfersIn();
		Transfer t = transfers.get(transfers.size()-1);	
    	transferid = t.getId(); 
		client1.receiveAmount(transferid);

    }
    
    @Test(expected = InvalidInputException_Exception.class) 
    public void receiveInvalidIDTransfer() throws InvalidInputException_Exception, FailToLogRequestException_Exception {
    	client2.sendAmount("client1test123456789REGISTER", 100);
    	client1.receiveAmount(0);
    }
    
    @Test(expected = InvalidInputException_Exception.class) 
    public void receiveNegativeIDTransfer() throws InvalidInputException_Exception, FailToLogRequestException_Exception {
    	client2.sendAmount("client1test123456789REGISTER", 100);
    	client1.receiveAmount(-1);
    }
    
}
