package hds;

import org.junit.*;



public class RegisterTest {

    // static members


    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
    }

    @AfterClass
    public static void oneTimeTearDown() {
    }
    
    // members
    HDSClient client1 = new HDSClient("./PrivKeys/", "../keys/");
	HDSClient client2 = new HDSClient("./PrivKeys/", "../keys/");

	String keyS1 = client1.getPublicKeyString();
	String keyS2 = client2.getPublicKeyString();

    // initialization and clean-up for each test
    @Before
    public void setUp() throws InvalidInputException_Exception, FailToLogRequestException_Exception{
    	client1.setClientUsername("client1test123456789REGISTER");
    	client2.setClientUsername("client2test123456789REGISTER");
    	try{
    		client1.register();
    	}catch(InvalidInputException_Exception e){}
    }

    @After
    public void tearDown() {
       	client1.setPublicKeyString(keyS1);
    	client2.setPublicKeyString(keyS2);
    }


    // tests
   
    
    @Test(expected = InvalidInputException_Exception.class) 
    public void  keyAndUsernameExist() throws InvalidInputException_Exception, FailToLogRequestException_Exception {
    	client1.register();
    }
    
    @Test(expected = InvalidInputException_Exception.class) 
    public void  usernameExists() throws InvalidInputException_Exception, FailToLogRequestException_Exception{
    	System.out.println("usernameExists");
    	client2.setClientUsername("client1test123456789REGISTER");
    	client2.register();
    }
    
    @Test(expected = InvalidInputException_Exception.class) 
    public void  nonAlphaNumericUsername2() throws InvalidInputException_Exception, FailToLogRequestException_Exception {
    	System.out.println("nonAlphaNumericUsername2");
    	client2.setClientUsername("-1");
    	client2.register();
    }

    
   
//     @Test(expected = InvalidInputException_Exception.class) 
//    public void  nullKey() throws InvalidInputException_Exception, FailToLogRequestException_Exception {
//    		System.out.println("nullKey");
//    		client2.setPublicKeyString(null);
//    		client2.register();
//    }
    
//    @Test(expected = RuntimeException.class) 
//    public void  permissionTest() throws InvalidInputException_Exception, FailToLogRequestException_Exception {
//    	System.out.println("permissionTest");
//    	client2.register();
//    	client2.permissionsTest(keyS1, "client1test123456789REGISTER");
//    }
//    
//    @Test(expected = RuntimeException.class) 
//    public void  anotherKey() throws InvalidInputException_Exception, FailToLogRequestException_Exception {
//    	System.out.println("anotherKey");
//    	client2.setPublicKeyString(keyS1);
//    	client2.register();
//    }
    
   
    	//Handlers are dealing with the null cases 
    
//    @Test(expected = RuntimeException.class) 
//    public void  nullUsername() throws InvalidInputException_Exception, FailToLogRequestException_Exception {
//    	System.out.println("nullUsername");
//    	client2.setClientUsername(null);
//    	client2.register();
//    }
    
//    @Test(expected = RuntimeException.class) 
//    public void  nullKeyAndUsername() throws InvalidInputException_Exception, FailToLogRequestException_Exception {
//    	System.out.println("nullKeyAndUsername");
//    	client2.setClientUsername(null);
//    	client2.setPublicKeyString(null);
//    	client2.register();
//    }

    
//    @Test(expected = RuntimeException.class) 
//    public void  falseKey() throws InvalidInputException_Exception, FailToLogRequestException_Exception {
//    	System.out.println("falseKey");
//    	client2.setPublicKeyString("ThisKeyIsFake");
//    	client2.register();
//    }
    
}
