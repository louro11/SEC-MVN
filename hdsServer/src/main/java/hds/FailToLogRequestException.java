package hds;

public class FailToLogRequestException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public FailToLogRequestException(String message){
		super(message);
	}
}
