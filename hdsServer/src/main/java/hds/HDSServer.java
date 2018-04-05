//Interface Server
package hds;


import javax.jws.WebService;

import javax.jws.WebMethod;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;


@WebService
@SOAPBinding(style = Style.DOCUMENT)
public interface HDSServer {
	@WebMethod void register(String key, String username) throws InvalidInputException, FailToLogRequestException;
	@WebMethod void send_amount(String source, String destination, float amount) throws InvalidInputException, FailToLogRequestException;
	@WebMethod void receive_amount(String key, int id) throws InvalidInputException, FailToLogRequestException;
	@WebMethod CheckResult check_account(String key) throws InvalidInputException;
	@WebMethod AuditResult audit(String key) throws InvalidInputException;
}