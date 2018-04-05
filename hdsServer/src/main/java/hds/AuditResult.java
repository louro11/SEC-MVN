package hds;

import java.util.ArrayList;

public class AuditResult {
	
	public ArrayList<Transfer> transfersHistory;

	public AuditResult(ArrayList<Transfer> transfersHistory) {
		this.transfersHistory = transfersHistory;
	}

	public ArrayList<Transfer> getTransfersHistory(){
		return transfersHistory;
	}

}
