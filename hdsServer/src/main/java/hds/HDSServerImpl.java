package hds;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;


@WebService(endpointInterface = "hds.HDSServer")
@HandlerChain(file="handler-chain.xml")
public class HDSServerImpl implements HDSServer{
	
	// Variables
	@Resource
	private WebServiceContext webServiceContext; 
	private Map<PublicKey, Account> accounts = new HashMap<PublicKey, Account>();
	private ArrayList<Transfer> transfers = new ArrayList<Transfer>();
	private int transferCounter = 0;
	private Log log;
	private String serverName = "HDS_SERVER";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_RED = "\u001B[31m";
	
	// Main Constructor
	public HDSServerImpl() throws InvalidInputException, FailToLogRequestException{
		this.log = new Log("log.txt");
		update();
	}
	
	// Test Constructor that uses different Log File
	public HDSServerImpl(String tests) throws InvalidInputException, FailToLogRequestException{
		this.log = new Log("trashLogFromTests.txt");
	}
	
	@Override
	public void register(String keyS, String username) throws InvalidInputException, FailToLogRequestException {
		
		if(webServiceContext != null)
			setupResponse();
		
		// Verify username and PublicKey
		if(username == null || !verifyPublicKey(keyS))
			throw new InvalidInputException("Username or PublicKey Invalid!");
		
		// Verify alphanumeric username
		if(!username.matches("[A-Za-z0-9]+"))
			throw new InvalidInputException("Username needs to be alphanumeric!");

		// Create account with username and PublicKey
		PublicKey key = StringToPubliKey(keyS);
		Account createdAccount = new Account(key, username);
		if(accountExists(createdAccount.getKey()) || usernameExists(username))
			throw new InvalidInputException("Account with same PublicKey and/or same Username already exists! Run a new shell to generate a new Public Key and/or try a different Username!");
		else{
			accounts.put(key, createdAccount);
			log.register(keyS, username);
			System.out.println(ANSI_GREEN + "  • Successfully registered account with username " + username + " and PublicKey "+ keyS + ANSI_RESET);
		}
	}

	@Override
	public void send_amount(String sourceS, String destinationS, float amount) throws InvalidInputException, FailToLogRequestException {
		
		if(!verifyPublicKey(sourceS) || !verifyPublicKey(destinationS) || amount == 0.0f)
			throw new InvalidInputException("Username, PublicKey or Amount Invalid!");
		
		if(sourceS.equals(destinationS)){
			throw new InvalidInputException("Same source and destination!!");
		}
		
		if(webServiceContext != null)
			setupResponse();
		if(amount>0){
			PublicKey source = StringToPubliKey(sourceS);
			PublicKey destination = StringToPubliKey(destinationS);
			if(accountExists(source) && accountExists(destination)){
				Account sender = accounts.get(source);
				if(sender.getBalance()-amount >= 0){
					Account destinatary = accounts.get(destination);
					Transfer transfer = new Transfer(sender, destinatary, amount, transferCounter);
					transferCounter++;
					sender.sendCoins(transfer);
					destinatary.notifyTransfer(transfer);
					transfers.add(transfer);
					log.sendAmount(sourceS, destinationS, amount);
					System.out.println(ANSI_GREEN + "  • Successfully sent " + amount + " from " + sender.getUsername() + " to " + destinatary.getUsername() + "!" + ANSI_RESET);
				}
				else
					throw new InvalidInputException("Insufficient Funds!");
			}
			else
				throw new InvalidInputException("Sender or Destinatary not found!");
		}
		else
			throw new InvalidInputException("Invalid amount!");	
	}

	@Override
	public void receive_amount(String keyS, int id) throws InvalidInputException, FailToLogRequestException {
		if(!verifyPublicKey(keyS))
			throw new InvalidInputException("PublicKey Invalid!");
		if(webServiceContext != null)
			setupResponse();
		//System.out.println(keyS + "\tACCEPT: " + id);
		PublicKey key = StringToPubliKey(keyS);
		if(id>=0 && id < transferCounter && !transfers.isEmpty()){
			Transfer transfer = this.getTransfers().get(id);
			if(accountExists(key)){
				Account destinatary = transfer.getDestinatary();
				if(destinatary.getKey().equals(key)){
					Account sender = transfer.getSender();
					if(transfer.getStatus().name().equals("PENDING")){
						destinatary.acceptTransfer(transfer);
						sender.acceptedTransfer(transfer);
						log.receiveAmount(keyS, id);
						System.out.println(ANSI_GREEN + "  • User " + destinatary.getUsername() + " successfully received " + transfer.getValue() + " from " + sender.getUsername() + ANSI_RESET);
					}
					else
						throw new InvalidInputException("Transfer was already accepted!"); //Lança excepção que o cliente apanha e imprime mensagem
				}
				else
					throw new InvalidInputException("No permission to accept transfer!"); //Lança excepção que o cliente apanha e imprime mensagem
			}
			else
				throw new InvalidInputException("Account not registered yet!"); //Lança excepção que o cliente apanha e imprime mensagem	
		}
		else
			throw new InvalidInputException("Unknown Transfer ID!"); //Lança excepção que o cliente apanha e imprime mensagem	
	}

	@Override
	public CheckResult check_account(String keyS) throws InvalidInputException {
		if(!verifyPublicKey(keyS))
			throw new InvalidInputException("Public Key Invalid!");
		if(webServiceContext != null)
			setupResponse();
		//System.out.println("CHECK ACCOUNT " + keyS);
		PublicKey key = StringToPubliKey(keyS);
		if(accountExists(key)){
			Account checkerAccount = accounts.get(key);
			return new CheckResult(checkerAccount.getBalance(), checkerAccount.getTransfersIn());
		}
		else
			throw new InvalidInputException("Unknown Account!"); //Lança excepção que o cliente apanha e imprime mensagem	
	}

	@Override
	public AuditResult audit(String keyS) throws InvalidInputException {
		if(!verifyPublicKey(keyS))
			throw new InvalidInputException("Public Key Invalid!");
		if(webServiceContext != null)
			setupResponse();
		//System.out.println("AUDIT ACCOUNT " + keyS);
		PublicKey key = StringToPubliKey(keyS);
		if(accountExists(key)){
			Account checkerAccount = accounts.get(key);
			return new AuditResult(checkerAccount.getTransfersHistory());
		}
		else
			throw new InvalidInputException("Unknown Account!"); //Lança excepção que o cliente apanha e imprime mensagem	
	}

	public boolean accountExists(PublicKey key){
		if(accounts.get(key) == null)
			return false;
		else
			return true;
	}
	
	public Map<PublicKey, Account> getAccounts() {
		return accounts;
	}

	public ArrayList<Transfer> getTransfers() {
		return transfers;
	}
	
	private boolean usernameExists(String username) {
		boolean found = false;
		for (Map.Entry<PublicKey, Account> entry : accounts.entrySet()){
			if(entry.getValue().getUsername().equals(username)){
				found = true;
			}
		}
		return found;
	}
	
	public static byte[] convertStringToByteArray(String string) throws Exception {
		return parseBase64Binary(string);
	}
	
	public static PublicKey convertByteArrayToPubKey(byte publicKeyBytes[], String algorithm) throws Exception {
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes));
	}
	
	PublicKey StringToPubliKey(String keyS) {
		try{
			byte[] publicBytes = convertStringToByteArray(keyS);
			return convertByteArrayToPubKey(publicBytes, "RSA");
		}catch(Exception e){
			return null;
		}
	}
	
	private boolean verifyPublicKey(String keyS){
		System.out.println("#####");
		System.out.println(keyS);
		System.out.println("#####");
		if(keyS == null)
			return false;
		if(StringToPubliKey(keyS)==null)
			return false;
		return true;
	}
	
	public void updateReceive_amount(String keyS, int id) throws InvalidInputException, FailToLogRequestException {
		System.out.println(keyS + "\tACCEPT: " + id);
		PublicKey key = StringToPubliKey(keyS);
		if(id < transferCounter && !transfers.isEmpty()){
			Transfer transfer = this.getTransfers().get(id);
			if(accountExists(key)){
				Account destinatary = transfer.getDestinatary();
				if(destinatary.getKey().equals(key)){
					Account sender = transfer.getSender();
					if(transfer.getStatus().name().equals("PENDING")){
						destinatary.acceptTransfer(transfer);
						sender.acceptedTransfer(transfer);
					}
					else
						throw new InvalidInputException("TRANSFER WAS ALREADY ACCEPTED"); //Lança excepção que o cliente apanha e imprime mensagem
				}
				else
					throw new InvalidInputException("NO PERMISSION TO ACCEPT TRANSFER"); //Lança excepção que o cliente apanha e imprime mensagem
			}
			else
				throw new InvalidInputException("ACCOUNT NOT REGISTERED YET"); //Lança excepção que o cliente apanha e imprime mensagem	
		}
		else
			throw new InvalidInputException("UNKOWN TRANSFER ID"); //Lança excepção que o cliente apanha e imprime mensagem	
	}
	
	public void updateSend_amount(String sourceS, String destinationS, float amount) throws InvalidInputException, FailToLogRequestException {
		System.out.println("SEND: " + amount + " \tFROM: " + sourceS + " \tTO" + destinationS);
		if(amount>0){
			PublicKey source = StringToPubliKey(sourceS);
			PublicKey destination = StringToPubliKey(destinationS);
			if(accountExists(source) && accountExists(destination)){
				Account sender = accounts.get(source);
				if(sender.getBalance()-amount >= 0){
					Account destinatary = accounts.get(destination);
					Transfer transfer = new Transfer(sender, destinatary, amount, transferCounter);
					transferCounter++;
					sender.sendCoins(transfer);
					destinatary.notifyTransfer(transfer);
					transfers.add(transfer);
				}
				else
					throw new InvalidInputException("INSUFFICIENT FUNDS");
			}
			else
				throw new InvalidInputException("SENDER OR DESTINATARY NOT REGISTERED YET");
		}
		else
			throw new InvalidInputException("INVALID AMOUNT");	
	}
	
	public void updateRegister(String keyS, String username) throws InvalidInputException, FailToLogRequestException {
		System.out.println("REGISTER: " + username + " \tPUBLICKEY: " + keyS);
		
		if(username == null || !verifyPublicKey(keyS))
			throw new InvalidInputException("USERNAME OR PUBLICKEY NOT VALID");
		
		if(!username.matches("[A-Za-z0-9]+"))
			throw new InvalidInputException("USERNAME NEEDS TO BE ALPHANUMERIC");

		PublicKey key = StringToPubliKey(keyS);
		Account createdAccount = new Account(key, username);
		if(accountExists(createdAccount.getKey()) || usernameExists(username))
			throw new InvalidInputException("ACCOUNT ALREADY EXISTS"); //Lança excepção que o cliente apanha e imprime mensagem
		else{
			accounts.put(key, createdAccount);
		}
	}
	
	private void update() throws InvalidInputException, FailToLogRequestException{
		Charset charset = Charset.forName("UTF-8");
		try (BufferedReader reader = Files.newBufferedReader(log.getLog(), charset)) {
		    String line = null;
		    while ((line = reader.readLine()) != null) {
		    	String[] args = line.split(" ");
		    	switch (args[0]) {
		        case "REGISTER":
		        	updateRegister(args[1], args[2]);
		        	break;
		        case "SEND":
		        	updateSend_amount(args[1], args[2], Float.parseFloat(args[3]));
		        	break;
		        case "RECEIVE":
		        	updateReceive_amount(args[1], Integer.parseInt(args[2]));
		        	break;
		        default:
		        	break;
		        }
		    }
		}catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	}
	
	// Put Entity and Nonce in SOAP variables
	public void setupResponse(){
		MessageContext messageContext = webServiceContext.getMessageContext();
		messageContext.put(SoapHandler.ENTITY_PROPERTY, serverName);
		messageContext.put(SoapHandler.NONCE_CONTENT, Nonce.getInstance().generateNonce());
	}

	public Account getAccount(PublicKey key1) {
		return accounts.get(key1);
	}

}
