
package hds;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the hds package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _InvalidInputException_QNAME = new QName("http://hds/", "InvalidInputException");
    private final static QName _SendAmount_QNAME = new QName("http://hds/", "send_amount");
    private final static QName _AuditResponse_QNAME = new QName("http://hds/", "auditResponse");
    private final static QName _CheckAccount_QNAME = new QName("http://hds/", "check_account");
    private final static QName _ReceiveAmountResponse_QNAME = new QName("http://hds/", "receive_amountResponse");
    private final static QName _Audit_QNAME = new QName("http://hds/", "audit");
    private final static QName _RegisterResponse_QNAME = new QName("http://hds/", "registerResponse");
    private final static QName _ReceiveAmount_QNAME = new QName("http://hds/", "receive_amount");
    private final static QName _CheckAccountResponse_QNAME = new QName("http://hds/", "check_accountResponse");
    private final static QName _FailToLogRequestException_QNAME = new QName("http://hds/", "FailToLogRequestException");
    private final static QName _SendAmountResponse_QNAME = new QName("http://hds/", "send_amountResponse");
    private final static QName _Register_QNAME = new QName("http://hds/", "register");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: hds
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link InvalidInputException }
     * 
     */
    public InvalidInputException createInvalidInputException() {
        return new InvalidInputException();
    }

    /**
     * Create an instance of {@link SendAmount }
     * 
     */
    public SendAmount createSendAmount() {
        return new SendAmount();
    }

    /**
     * Create an instance of {@link AuditResponse }
     * 
     */
    public AuditResponse createAuditResponse() {
        return new AuditResponse();
    }

    /**
     * Create an instance of {@link CheckAccount }
     * 
     */
    public CheckAccount createCheckAccount() {
        return new CheckAccount();
    }

    /**
     * Create an instance of {@link ReceiveAmountResponse }
     * 
     */
    public ReceiveAmountResponse createReceiveAmountResponse() {
        return new ReceiveAmountResponse();
    }

    /**
     * Create an instance of {@link Audit }
     * 
     */
    public Audit createAudit() {
        return new Audit();
    }

    /**
     * Create an instance of {@link RegisterResponse }
     * 
     */
    public RegisterResponse createRegisterResponse() {
        return new RegisterResponse();
    }

    /**
     * Create an instance of {@link ReceiveAmount }
     * 
     */
    public ReceiveAmount createReceiveAmount() {
        return new ReceiveAmount();
    }

    /**
     * Create an instance of {@link CheckAccountResponse }
     * 
     */
    public CheckAccountResponse createCheckAccountResponse() {
        return new CheckAccountResponse();
    }

    /**
     * Create an instance of {@link FailToLogRequestException }
     * 
     */
    public FailToLogRequestException createFailToLogRequestException() {
        return new FailToLogRequestException();
    }

    /**
     * Create an instance of {@link SendAmountResponse }
     * 
     */
    public SendAmountResponse createSendAmountResponse() {
        return new SendAmountResponse();
    }

    /**
     * Create an instance of {@link Register }
     * 
     */
    public Register createRegister() {
        return new Register();
    }

    /**
     * Create an instance of {@link AuditResult }
     * 
     */
    public AuditResult createAuditResult() {
        return new AuditResult();
    }

    /**
     * Create an instance of {@link CheckResult }
     * 
     */
    public CheckResult createCheckResult() {
        return new CheckResult();
    }

    /**
     * Create an instance of {@link Transfer }
     * 
     */
    public Transfer createTransfer() {
        return new Transfer();
    }

    /**
     * Create an instance of {@link Account }
     * 
     */
    public Account createAccount() {
        return new Account();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link InvalidInputException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://hds/", name = "InvalidInputException")
    public JAXBElement<InvalidInputException> createInvalidInputException(InvalidInputException value) {
        return new JAXBElement<InvalidInputException>(_InvalidInputException_QNAME, InvalidInputException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendAmount }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://hds/", name = "send_amount")
    public JAXBElement<SendAmount> createSendAmount(SendAmount value) {
        return new JAXBElement<SendAmount>(_SendAmount_QNAME, SendAmount.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuditResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://hds/", name = "auditResponse")
    public JAXBElement<AuditResponse> createAuditResponse(AuditResponse value) {
        return new JAXBElement<AuditResponse>(_AuditResponse_QNAME, AuditResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CheckAccount }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://hds/", name = "check_account")
    public JAXBElement<CheckAccount> createCheckAccount(CheckAccount value) {
        return new JAXBElement<CheckAccount>(_CheckAccount_QNAME, CheckAccount.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReceiveAmountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://hds/", name = "receive_amountResponse")
    public JAXBElement<ReceiveAmountResponse> createReceiveAmountResponse(ReceiveAmountResponse value) {
        return new JAXBElement<ReceiveAmountResponse>(_ReceiveAmountResponse_QNAME, ReceiveAmountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Audit }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://hds/", name = "audit")
    public JAXBElement<Audit> createAudit(Audit value) {
        return new JAXBElement<Audit>(_Audit_QNAME, Audit.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RegisterResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://hds/", name = "registerResponse")
    public JAXBElement<RegisterResponse> createRegisterResponse(RegisterResponse value) {
        return new JAXBElement<RegisterResponse>(_RegisterResponse_QNAME, RegisterResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReceiveAmount }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://hds/", name = "receive_amount")
    public JAXBElement<ReceiveAmount> createReceiveAmount(ReceiveAmount value) {
        return new JAXBElement<ReceiveAmount>(_ReceiveAmount_QNAME, ReceiveAmount.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CheckAccountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://hds/", name = "check_accountResponse")
    public JAXBElement<CheckAccountResponse> createCheckAccountResponse(CheckAccountResponse value) {
        return new JAXBElement<CheckAccountResponse>(_CheckAccountResponse_QNAME, CheckAccountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FailToLogRequestException }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://hds/", name = "FailToLogRequestException")
    public JAXBElement<FailToLogRequestException> createFailToLogRequestException(FailToLogRequestException value) {
        return new JAXBElement<FailToLogRequestException>(_FailToLogRequestException_QNAME, FailToLogRequestException.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SendAmountResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://hds/", name = "send_amountResponse")
    public JAXBElement<SendAmountResponse> createSendAmountResponse(SendAmountResponse value) {
        return new JAXBElement<SendAmountResponse>(_SendAmountResponse_QNAME, SendAmountResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Register }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://hds/", name = "register")
    public JAXBElement<Register> createRegister(Register value) {
        return new JAXBElement<Register>(_Register_QNAME, Register.class, null, value);
    }

}
