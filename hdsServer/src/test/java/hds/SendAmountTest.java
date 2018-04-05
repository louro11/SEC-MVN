package hds;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

public class SendAmountTest {
	// static members


    // one-time initialization and clean-up

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
    String unregisteredKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCO5VRsFzoW8+8ipuTsQbCGraO2fDXjv9EoK+qENQE8kp5+Ef7tChpiaTNsM1M6mOOADq8IPYo6V4xT87eiP/y6I82bBKz2e2G+/RnvEAp+iFSEKQqD92JkY4/BjjmSk6qmAcf5tNhYhnWfoXQdD9erri9ZKXAUEYKLIQ7Kb1MyOQIDAQAB";
    // initialization and clean-up for each test
    @Before
    public void setUp() throws InvalidInputException, FailToLogRequestException{
	    server = new HDSServerImpl("tests"); 
	    server.register(keyS1, "sec1");
	    server.register(keyS2, "sec2");
	    server.register(keyS3, "sec3");
    }

    @After
    public void tearDown() {
    	server = null;
    }


    // tests
    @Test
    public void  coinsFlowOK() throws InvalidInputException, FailToLogRequestException {
    	ArrayList<Transfer> transfers = new ArrayList<Transfer>();
    	int id;
    	
    	server.send_amount(keyS1, keyS2, 2);
    	
    	Account sender = server.getTransfers().get(0).getSender();
    	Account destinatary = server.getTransfers().get(0).getDestinatary();
    	
    	assertEquals(498.0, sender.getBalance(), 0);
    	assertEquals(500.0, destinatary.getBalance(), 0);
    	
    	transfers = server.getTransfers();	
    	Transfer t = transfers.get(transfers.size()-1);	
    	id = t.getId();
    	
    	server.receive_amount(keyS2, id);
    	
    	assertEquals(502.0, destinatary.getBalance(), 0);
    }
    
    @Test
    public void  decimalCoinsFlowOK() throws InvalidInputException, FailToLogRequestException  {
    	ArrayList<Transfer> transfers = new ArrayList<Transfer>();
    	int id;
    	server.send_amount(keyS1, keyS2, (float) 2.5);
    	Account sender = server.getTransfers().get(0).getSender();
    	Account destinatary = server.getTransfers().get(0).getDestinatary();
    	
    	assertEquals(497.5, sender.getBalance(), 0);
    	assertEquals(500.0, destinatary.getBalance(), 0);
    	
    	transfers = server.getTransfers();	
    	Transfer t = transfers.get(transfers.size()-1);	
    	id = t.getId();
    	
    	server.receive_amount(keyS2, id);
    	
    	assertEquals(502.5, destinatary.getBalance(), 0);
    }
    
    @Test
    public void  transferInListFlowOK() throws InvalidInputException, FailToLogRequestException  {
    	server.send_amount(keyS1, keyS2, 2);
    	Account sender = server.getTransfers().get(0).getSender();
    	Account destinatary = server.getTransfers().get(0).getDestinatary();
    	
    	assertEquals(true, sender.getTransfersIn().isEmpty());
    	assertEquals(false, destinatary.getTransfersIn().isEmpty());
    }
    
    @Test
    public void  transferOutListFlowOK() throws InvalidInputException, FailToLogRequestException  {
    	server.send_amount(keyS1, keyS2, 2);
    	Account sender = server.getTransfers().get(0).getSender();
    	Account destinatary = server.getTransfers().get(0).getDestinatary();
    	
    	assertEquals(false, sender.getTransfersOut().isEmpty());
    	assertEquals(true, destinatary.getTransfersOut().isEmpty());
    	
    }
    
    @Test
    public void  transferHistoryListFlowOK() throws InvalidInputException, FailToLogRequestException  {
    	server.send_amount(keyS1, keyS2, 2);
    	Account sender = server.getTransfers().get(0).getSender();
    	Account destinatary = server.getTransfers().get(0).getDestinatary();

    	assertEquals(false, sender.getTransfersHistory().isEmpty());
    	assertEquals(true, destinatary.getTransfersHistory().isEmpty());
    }
    
    @Test(expected = InvalidInputException.class) 
    public void  sendAmountToUnregisteredUser() throws InvalidInputException, FailToLogRequestException {
    	server.send_amount(keyS1, unregisteredKey, 2);
    }
    
    @Test(expected = InvalidInputException.class) 
    public void  sendAmountFromUnregisteredUser() throws InvalidInputException, FailToLogRequestException  {
    	server.send_amount(unregisteredKey, keyS1, 2);
    }
    
    @Test(expected = InvalidInputException.class) 
    public void  insuficientFunds() throws InvalidInputException, FailToLogRequestException  {
    	server.send_amount(keyS1, keyS2, 999999999);
    }
    
    @Test(expected = InvalidInputException.class) 
    public void  negativeAmount() throws InvalidInputException, FailToLogRequestException  {
    	server.send_amount(keyS1, keyS2, -2);
    }
    
    @Test(expected = InvalidInputException.class) 
    public void  zeroAmount() throws InvalidInputException, FailToLogRequestException  {
    	server.send_amount(keyS1, keyS2, 0);
    }
    
    @Test(expected = InvalidInputException.class) 
    public void  decimalZeroAmount() throws InvalidInputException, FailToLogRequestException  {
    	server.send_amount(keyS1, keyS2, (float) 0.0);
    }
    
    @Test(expected = InvalidInputException.class) 
    public void  nullAmount() throws InvalidInputException, FailToLogRequestException  {
    	server.send_amount(keyS1, keyS2, 0.0f);
    }
    
    @Test(expected = InvalidInputException.class) 
    public void  nullSender() throws InvalidInputException, FailToLogRequestException  {
    	server.send_amount(null, keyS2, 2);
    }
    
    @Test(expected = InvalidInputException.class) 
    public void  nullDestinatary() throws InvalidInputException, FailToLogRequestException  {
    	server.send_amount(keyS1, null, 2);
    }
    
    
}