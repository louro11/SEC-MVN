package hds;


import java.util.ArrayList;

public class CheckResult {

	public ArrayList<Transfer> transfersIn;
	public float balance;

	public CheckResult(float balance, ArrayList<Transfer> transfersIn) {
		this.balance = balance;
		this.transfersIn = transfersIn;
	}

	public ArrayList<Transfer> getTransfersIn() {
		return transfersIn;
	}

	public float getBalance() {
		return balance;
	}

}
