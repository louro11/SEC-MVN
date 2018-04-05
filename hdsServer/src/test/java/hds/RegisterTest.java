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
    HDSServerImpl server;
    String keyS1 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJZTyXjVttDI2gbqdAWShLpndoD/Wng68xR2nMvZ6H2Qor8ZDNnsAbXNCBpFZFUhp0ka0dt8M/L/uH/cFbDw8kn8U4d8gDJTLxwMtF6bQVkNDyoWixj5Ir+Kj5J7XSkX8sDzlTOahZ549URPX/8uaq6SfsQx2hTOJjnfuMp8r2EQIDAQAB";
    String keyS2 = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDiCHFJagc67ZQZtCEXzZ19rE5kLW4n5geDB/0pDAx+2ZYlOfDsx91J2xE6UANJHmmPG4W9dhPN4J1R12ncFUZ2e6Qd3swA4vswwGbL9Sq+Fo/H3wOzwp15K1M6QjsEhx/JpcpToN35GTLjlgrE2t5loWFE3gL0pL6IYq0PQQLuaQIDAQAB";

    // initialization and clean-up for each test
    @Before
    public void setUp() throws InvalidInputException, FailToLogRequestException{
    	server = new HDSServerImpl("tests"); 
    }

    @After
    public void tearDown() {
    	server = null;
    }


    // tests
    @Test
    public void  ok() throws InvalidInputException, FailToLogRequestException {
    	server.register(keyS1, "sec1");
    	server.register(keyS2, "sec2");
    }
    
    @Test(expected = InvalidInputException.class) 
    public void  usernameExists() throws InvalidInputException, FailToLogRequestException {
    	server.register(keyS1, "sec");
    	server.register(keyS2, "sec");
    }
    
    @Test(expected = InvalidInputException.class) 
    public void  keyExists() throws InvalidInputException, FailToLogRequestException {
    	server.register(keyS1, "sec");
    	server.register(keyS1, "sec2");
    }
    
    @Test(expected = InvalidInputException.class) 
    public void  keyAndUsernameExist() throws InvalidInputException, FailToLogRequestException {
    	server.register(keyS1, "sec");
    	server.register(keyS1, "sec");
    }
    
    @Test(expected = InvalidInputException.class) 
    public void  nullKey() throws InvalidInputException, FailToLogRequestException {
    	server.register(null, "sec");
    }

    @Test(expected = InvalidInputException.class) 
    public void  nullUsername() throws InvalidInputException, FailToLogRequestException {
    	server.register(keyS1, null);
    }
    
    @Test(expected = InvalidInputException.class) 
    public void  nullKeyAndUsername() throws InvalidInputException, FailToLogRequestException {
    	server.register(null, null);
    }

    @Test(expected = InvalidInputException.class) 
    public void  nonAlphaNumericUsername() throws InvalidInputException, FailToLogRequestException {
    	server.register(keyS1, "12/dd$%");
    }
    
    @Test(expected = InvalidInputException.class) 
    public void  nonAlphaNumericUsername2() throws InvalidInputException, FailToLogRequestException {
    	server.register(keyS1, "-1");
    }
    
    @Test(expected = InvalidInputException.class) 
    public void  falseKey() throws InvalidInputException, FailToLogRequestException {
    	server.register("ThisKeyIsFake", "sec1");
    }

}
