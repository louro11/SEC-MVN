package hds;


import java.util.Date;

public class Transfer {
	public Account sender;
	public Account destinatary;
	public String usernameSender;
	public String usernameDestinatary;
	public float value;
	public StatusEnum status;
	public Date requestTime;
	public Date responseTime;
	public int id;
	
	public enum StatusEnum {
		PENDING,
		ACCEPTED;
	}
	
	public Transfer(Account source, Account destination, float value, int id){
		this.sender = source;
		this.destinatary = destination;
		this.usernameSender = source.getUsername();
		this.usernameDestinatary = destination.getUsername();
		this.value = value;
		this.status = StatusEnum.PENDING;
		this.requestTime = new Date();
		this.responseTime = null;
		this.id = id;
	}
	
	public Account getSender() {
		return sender;
	}

	public Account getDestinatary() {
		return destinatary;
	}
	
	public String getUsernameSender() {
		return usernameSender;
	}

	public String getUsernameDestinatary() {
		return usernameDestinatary;
	}

	public float getValue() {
		return value;
	}

	public Date getRequestTime() {
		return requestTime;
	}

	public void accept() {
		this.responseTime = new Date();
		this.status = StatusEnum.ACCEPTED;
	}

	public StatusEnum getStatus() {
		return status;
	}

	public Date getResponseTime() {
		return responseTime;
	}

	public int getId() {
		return id;
	}
}
