package hds;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;

public class Log {
	//float f = Float.parseFloat("25");
	private String registerMESSAGE;
	private String sendAmountMESSAGE;
	private String receiveAmountMESSAGE;
	private String pathToLogFile;
	private File log;

	public Log(String filename){
		this.registerMESSAGE = "REGISTER";
		this.sendAmountMESSAGE = "SEND";
		this.receiveAmountMESSAGE = "RECEIVE";
		this.pathToLogFile = filename;
		this.log = new File(pathToLogFile);
	}
	
	public void register(String key, String username) throws FailToLogRequestException {
		String l = registerMESSAGE + " " + key + " " + username + "\n";
		SaveTextInFile(l, log);
	}
	
	public void sendAmount(String senderKey, String destinataryKey, float amount) throws FailToLogRequestException {
		String l = sendAmountMESSAGE + " " + senderKey + " " + destinataryKey + " " +Float.toString(amount) + "\n";
		SaveTextInFile(l, log);
	}
	
	public void receiveAmount(String receiverKey, int id) throws FailToLogRequestException {
		String l = receiveAmountMESSAGE + " " + receiverKey + " " + id + "\n";
		SaveTextInFile(l, log);
	}
	
    public static  void  SaveTextInFile(String text, File log) throws FailToLogRequestException {
    	try{
    	    FileWriter writer = new FileWriter (log, true);
    	    writer.write(text);
    	    writer.flush();
    	    writer.close();
    	}catch(Exception e){
    		throw new FailToLogRequestException("FAIL TO LOG YOUR REQUEST\nPLEASE TRY AGAIN LATER...");
    	}
    }
    
    public Path getLog(){
    	return log.toPath();
    }
}
