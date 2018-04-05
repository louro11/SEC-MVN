package hds;

import java.security.PublicKey;
import java.util.ArrayList;

public class Account {
	
	private PublicKey key;
	private float balance;
	private float initialWalletValue = 500;
	private String username;
	private ArrayList<Transfer> transfersIn = new ArrayList<Transfer>();
	private ArrayList<Transfer> transfersOut = new ArrayList<Transfer>();
	private ArrayList<Transfer> transfersHistory = new ArrayList<Transfer>();

	public Account(PublicKey key, String username){
		this.key = key;
		this.balance = initialWalletValue;
		this.username = username;
	}
	
	public float getBalance() {
		return balance;
	}

	public float getInitialWalletValue() {
		return initialWalletValue;
	}

	public PublicKey getKey() {
		return key;
	}

	public ArrayList<Transfer> getTransfersIn() {
		return transfersIn;
	}

	public ArrayList<Transfer> getTransfersOut() {
		return transfersOut;
	}
	
	public void sendCoins(Transfer transfer){
		transfersOut.add(transfer);
		transfersHistory.add(transfer);
		this.balance = this.balance - transfer.getValue();
	}
	
	public void notifyTransfer(Transfer transfer){
		transfersIn.add(transfer);
	}
	
	public void acceptedTransfer(Transfer transfer) throws InvalidInputException{
		Account sender = transfer.getSender();
		Account destinatary = transfer.getDestinatary();
		float value = transfer.getValue();
		int outSize = transfersOut.size()-1;
		for(int i = 0; i <= outSize; i++){
		    Transfer t = transfersOut.get(i);
		    if(t.getDestinatary() == destinatary && t.getSender() == sender && t.getValue() == value){
		    	transfersOut.remove(i);
		    	break;
		    }
		}
	}
	
	public void acceptTransfer(Transfer transfer) throws InvalidInputException{
		Account sender = transfer.getSender();
		Account destinatary = transfer.getDestinatary();
		float value = transfer.getValue();
		int inSize = transfersIn.size()-1;
		for(int i = 0; i <= inSize; i++){
		    Transfer t = transfersIn.get(i);
		    if(t.getDestinatary() == destinatary && t.getSender() == sender && t.getValue() == value){
		    	transfersIn.remove(i);
		    	this.balance = this.balance + transfer.getValue();
		    	transfer.accept();
		    	transfersHistory.add(transfer);
		    	break;
		    }
		}
	}
	

	public ArrayList<Transfer> getTransfersHistory() {
		return transfersHistory;
	}

	public String getUsername() {
		return username;
	}

}
