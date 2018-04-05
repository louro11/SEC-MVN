package hds;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

import java.io.BufferedReader;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Scanner;



public class HDSClientApplication {
	final static String ANSI_GREEN = "\u001B[32m";
	final static String ANSI_RESET = "\u001B[0m";
	final static String ANSI_RED = "\u001B[31m";
	public static void main(String[] args){

		String privateKeyStorePath = "PrivKeys/";
		String publicKeyStorePath = "../keys/";
		HDSClient client;
		String myUsername = null;
		boolean registered = false;
		
		if(args.length == 0){
			client = new HDSClient(privateKeyStorePath, publicKeyStorePath);
		}
		else{
			client = logIn(args[0], privateKeyStorePath, publicKeyStorePath);
			if(client == null)
				System.out.println("Something went wrong :(");
			myUsername = client.getClientUsername();
			registered = true;
		}
	
		String key = client.getPublicKeyString();
		
			try{
				Scanner scanner = new Scanner(System.in);
				boolean quit = false;
				while(!quit){
					printMenu();
					
					//Escolher opção
					int choice = -1;
				    String choiceS = scanner.next();
				    
				    try{
				    	choice = Integer.parseInt(choiceS);
			    	}catch(NumberFormatException e){
			    		System.out.println("Ups. Invalid option...");
			    		System.out.println("Please try again (1, 2, 3 , 4, 5 or 0)");
			    		continue;
			    	}
				    
				    /* EXECUTAR OPÇÂO ESCOLHIDA
			         * 1 - register
					 * 2 - send_amount
					 * 3 - check_account
					 * 4 - receive_amount
					 * 5 - audit
					 * 0 - SAIR
			         * */
				    switch (choice) {
				        case 1:	// REGISTER
				        	
				        	// 1º Pede username
				        	System.out.println("SELECT A USERNAME: (you can't use '/') *type 'BACK' to navigate to the previous menu.");
				        	boolean usernameGiven = false;
						    while(!usernameGiven){
						    	myUsername = scanner.next();
						    	if(myUsername.contains("/")){
						    		System.out.println("INVALID  USERNAME: YOU CANNOT USE '/'");
						    		break;
						    	}
						    	if(myUsername.equals("BACK") || myUsername.equals("back")
						    			|| myUsername.equals("client1test123456789") 
						    			|| myUsername.equals("client2test123456789")) //client*test123456789 são utilizadores reservados p/ testes
						    		break;//print de cenas!!!
						    	else
						    		usernameGiven = true;
						    }
				        	if(!usernameGiven)
				        		break;
				        	try{
				        		// 2º Guarda as keys na "CA" e regista-se através da instancia de HDSClient
				        		client.setClientUsername(myUsername);
				        		try{
					        		client.register();
					        		registered = true;
					        		break;
				        		}catch(RuntimeException e){
				        			System.out.println("Message Failed :(");
				        		}
				        	}catch(InvalidInputException_Exception e){
				        		// A Chave publica e/ou username passados ao servidor são invalidos
				        		printException(e);
				    	    	break;	
			    	    	}catch(FailToLogRequestException_Exception e){
			    	    		// Registo do pedido no log do servidor falhou
			    	    		printException(e);
				    	    	break;	
			    	    	}
				            
				        case 2: // SEND AMOUNT
				        	
				        	if(!registered || client.getClientUsername() == null){
				        		System.out.println("YOU NEED TO REGISTER YOUR ACCOUNT FIRST!");
				        		break;
				        	}
				        		
				    		System.out.println("SELECT DESTINATARY: *type 'BACK' to navigate to the previous menu.");
				    		String username2 = scanner.next();
				    		if(username2.equals("BACK") || username2.equals("back"))
				    			break;
				    		System.out.println("SELECT AMOUNT: *type 'BACK' to navigate to the previous menu.");
				    		int amount = -1;
				    		boolean amountGiven = false;
				    	    while(!amountGiven){
				    	    	String amountS = scanner.next();
				    	    	if(amountS.equals("BACK") || amountS.equals("back"))
				    		    	break;
				    	    	try{
				    	    		amount = Integer.parseInt(amountS);
				    	    		amountGiven = true;
				    	    	}catch(NumberFormatException e){
				    	    		System.out.println("Ups. Invalid amount...");
				    	    		System.out.println("Please try again (amount is an integer number)");
				    	    		continue;
				    	    	}
				    	    }
				    	    try{
				    	    	client.sendAmount(username2, amount);
				    	    	break;
				    	    }catch(InvalidInputException_Exception e){
				    	    	// A Chave publica e/ou username e/ou amount passados ao servidor são invalidos
				    	    	printException(e);
				    	    	break;	
				    	    }catch(FailToLogRequestException_Exception e){
				    	    	// Registo do pedido no log do servidor falhou
				    	    	printException(e);
				    	    	break;	
			    	    	}
				            
				        case 3: // CHECK ACCOUNT
				        	
				        	if(!registered || client.getClientUsername() == null){
				        		System.out.println("YOU NEED TO REGISTER YOUR ACCOUNT FIRST!");
				        		break;
				        	}
				        	try{
				        		CheckResult response = client.checkAccount();
				        		if(response == null){
				        			System.out.println("Message Failed");
				        			break;
				        		}
				        			
				        		printCheckResult(response);
					        	pressAnyKeyToContinue();
					            break;
				        	}catch(InvalidInputException_Exception e){
				        		// A Chave publica passada ao servidor é invalida
				    	    	printException(e);
				    	    	break;	
				    	    }
				            
				        case 4: // RECEIVE AMOUNT
				        	
				        	if(!registered || client.getClientUsername() == null){
				        		System.out.println("YOU NEED TO REGISTER YOUR ACCOUNT FIRST!");
				        		break;
				        	}
				        	System.out.println("SELECT TRANSFER ID: *type 'BACK' to navigate to the previous menu.");
				        	boolean idGiven = false;
				        	int id = -1;
						    while(!idGiven){
						    	String idS = scanner.next();
						    	if(idS.equals("BACK") || idS.equals("back"))
							    	break;
						    	try{
						    		id = Integer.parseInt(idS);
						    		idGiven = true;
						    	}catch(NumberFormatException e){
						    		System.out.println("Ups. Invalid transfer ID...");
						    		System.out.println("Please try again (ID is an integer number)");
						    		continue;
						    	}
						    }
						    try{
							    if(id!=-1){
							    	client.receiveAmount(id);
							    	pressAnyKeyToContinue();
					        	}
					            break;
						    }catch(InvalidInputException_Exception e){
						    	// A Chave publica e/ou o ID passados ao servidor são invalidos
						    	printException(e);
				    	    	break;	
				    	    }catch(FailToLogRequestException_Exception e){
				    	    	// Registo do pedido no log do servidor falhou
				    	    	printException(e);
				    	    	break;	
			    	    	}
				            
				        case 5: // AUDIT
				        	
				        	if(!registered || client.getClientUsername() == null){
				        		System.out.println("YOU NEED TO REGISTER YOUR ACCOUNT FIRST!");
				        		break;
				        	}
				        	System.out.println("SELECT ACCOUNT TO AUDIT: *type 'BACK' to navigate to the previous menu.");
				    		String username5 = scanner.next();
				    		if(username5.equals("BACK") || username5.equals("back"))
				    			break;
				    		try{
					        	AuditResult auditResponse = client.audit(username5);
				        		if(auditResponse == null){
				        			System.out.println("Message Failed");
				        			break;
				        		}
					        	ArrayList<Transfer> history = (ArrayList<Transfer>) auditResponse.getTransfersHistory();
					        	if(history.isEmpty()){
					        		System.out.println("YOU HAVE NO TRANSFERS REGISTERED");
					        	}
					        	else{
					        		for (Transfer transfer : history) {
					        			printTransfer(transfer);
					        		}
					        	}
					        	pressAnyKeyToContinue();
					            break;
				            }catch(InvalidInputException_Exception e){
				            	// A Chave publica passada ao servidor é invalida
						    	printException(e);
				    	    	break;	
				    	    }
				            
				        case 0:
				        	scanner.close();
				        	quit = true;
				        	printGoodByeMessage();
				            break;
				            
				        default:
				        	System.out.println("PLEASE CHOOSE A VALID OPTION (1, 2, 3, 4, 5 or 0)"); 	
				    }
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
	}
	
	private static HDSClient logIn(String args, String privateKeyStorePath, String publicKeyStorePath) {
		Scanner scanner = new Scanner(System.in);
		boolean logged = false;
		String username = null;
		
		PublicKey pub = null;
		PrivateKey priv = null;
		
		while(!logged ){
			System.out.println("Login with your username: ");
			username = scanner.next();
			pub = getPublicKeyLogIn(username, publicKeyStorePath);
			priv = getPrivateKeyLogIn(username, privateKeyStorePath);
			if(pub != null && priv != null){
				HDSClient client = new HDSClient(priv, pub, privateKeyStorePath, publicKeyStorePath);
				client.setClientUsername(username);
				return client;
			}
		}
		return null;
	}
	
	// Returns Private Key from KeyStore
	private static PrivateKey getPrivateKeyLogIn(String username, String pathToKeystore) {
		try {
			
			File privKeyFile = new File(pathToKeystore + username + "_PrivateKey");
			Charset charset = Charset.forName("UTF-8");
			BufferedReader reader = Files.newBufferedReader(privKeyFile.toPath(), charset);
			String key = reader.readLine();
			byte[] encoded = parseBase64Binary(key);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePrivate(new PKCS8EncodedKeySpec(encoded));
		} catch (Exception e) {
			return null; 
		} 
	}
	
	// Returns Private Key from KeyStore
	private static PublicKey getPublicKeyLogIn(String username, String pathToKeystore) {
		try {
			File privKeyFile = new File(pathToKeystore + username + "_PublicKey");
			Charset charset = Charset.forName("UTF-8");
			BufferedReader reader = Files.newBufferedReader(privKeyFile.toPath(), charset);
			String key = reader.readLine();
			return StringToPubliKey(key);
		} catch (Exception e) {
			return null; 
		} 
	}
	
	private static PublicKey StringToPubliKey(String keyS) {
		try{
			byte[] publicBytes = convertStringToByteArray(keyS);
			return convertByteArrayToPubKey(publicBytes, "RSA");
		}catch(Exception e){
			return null;
		}
	}
	
	public static byte[] convertStringToByteArray(String string) throws Exception {
		return parseBase64Binary(string);
	}
	
	public static PublicKey convertByteArrayToPubKey(byte publicKeyBytes[], String algorithm) throws Exception {
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
	}
	
	private static void printMenu() {
		System.out.println("");
		System.out.println("");
		System.out.println("");
		System.out.println("################################");
		System.out.println("          HDS PROJECT");
		System.out.println("################################");
		System.out.println("");
		System.out.println("SELECT AN OPTION");
		System.out.println("1 - REGISTER ACCOUNT");
		System.out.println("2 - TRANSFER MONEY");
		System.out.println("3 - CHECK ACCOUNT");
		System.out.println("4 - ACCEPT TRANSFER");
		System.out.println("5 - SEE TRANSACTION HISTORY");
		System.out.println("0 - EXIT");
		
	}
	
	private static void printException(Exception e) {
		System.out.println("#############################");
    	System.out.println("");
    	System.out.println(ANSI_RED + e.getMessage() + ANSI_RESET);
    	System.out.println("");
    	System.out.println("#############################");
	}
	
	private static void printGoodByeMessage() {
		System.out.println("################");
    	System.out.println("      Bye!      ");
    	System.out.println("################");
	}
	
	public static void printCheckResult(CheckResult response) {
    	ArrayList<Transfer> transfersIn = (ArrayList<Transfer>) response.getTransfersIn();
    	float balance = response.getBalance();
    	if(transfersIn.isEmpty()){
    		System.out.println("YOU HAVE NO TRANSFERS TO ACCEPT");
    	}
    	else{
    		for (Transfer transfer : transfersIn) {
    			printTransfer(transfer);
    		}
    	}
    	System.out.println("CURRENT ACCOUNT BALANCE: " + balance);
	}
	
	public static void printTransfer(Transfer transfer) {
		System.out.println("#############################################################");
		System.out.println(" ");
		System.out.println("ID: " + transfer.getId());
		System.out.println("SENDER: " + transfer.getUsernameSender());
		System.out.println("DESTINATARY: " + transfer.getUsernameDestinatary());
		System.out.println("AMOUNT: " + transfer.getValue());
		System.out.println("STATUS: " + transfer.getStatus());
		System.out.println("REQUEST TIME: " + transfer.getRequestTime());
		System.out.println(" ");
		System.out.println("#############################################################");
	}
	
	public static void pressAnyKeyToContinue() { 
	        System.out.println("Press Enter key to continue...");
	        try
	        {
	            System.in.read();
	        }  
	        catch(Exception e){
	        	e.printStackTrace();
	        }  
	 }

}
