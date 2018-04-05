
package hds;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for transfer complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transfer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sender" type="{http://hds/}account" minOccurs="0"/>
 *         &lt;element name="destinatary" type="{http://hds/}account" minOccurs="0"/>
 *         &lt;element name="usernameSender" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="usernameDestinatary" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="status" type="{http://hds/}statusEnum" minOccurs="0"/>
 *         &lt;element name="requestTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="responseTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="id" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "transfer", propOrder = {
    "sender",
    "destinatary",
    "usernameSender",
    "usernameDestinatary",
    "value",
    "status",
    "requestTime",
    "responseTime",
    "id"
})
public class Transfer {

    protected Account sender;
    protected Account destinatary;
    protected String usernameSender;
    protected String usernameDestinatary;
    protected float value;
    @XmlSchemaType(name = "string")
    protected StatusEnum status;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar requestTime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar responseTime;
    protected int id;

    /**
     * Gets the value of the sender property.
     * 
     * @return
     *     possible object is
     *     {@link Account }
     *     
     */
    public Account getSender() {
        return sender;
    }

    /**
     * Sets the value of the sender property.
     * 
     * @param value
     *     allowed object is
     *     {@link Account }
     *     
     */
    public void setSender(Account value) {
        this.sender = value;
    }

    /**
     * Gets the value of the destinatary property.
     * 
     * @return
     *     possible object is
     *     {@link Account }
     *     
     */
    public Account getDestinatary() {
        return destinatary;
    }

    /**
     * Sets the value of the destinatary property.
     * 
     * @param value
     *     allowed object is
     *     {@link Account }
     *     
     */
    public void setDestinatary(Account value) {
        this.destinatary = value;
    }

    /**
     * Gets the value of the usernameSender property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsernameSender() {
        return usernameSender;
    }

    /**
     * Sets the value of the usernameSender property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsernameSender(String value) {
        this.usernameSender = value;
    }

    /**
     * Gets the value of the usernameDestinatary property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUsernameDestinatary() {
        return usernameDestinatary;
    }

    /**
     * Sets the value of the usernameDestinatary property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUsernameDestinatary(String value) {
        this.usernameDestinatary = value;
    }

    /**
     * Gets the value of the value property.
     * 
     */
    public float getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     */
    public void setValue(float value) {
        this.value = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link StatusEnum }
     *     
     */
    public StatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusEnum }
     *     
     */
    public void setStatus(StatusEnum value) {
        this.status = value;
    }

    /**
     * Gets the value of the requestTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRequestTime() {
        return requestTime;
    }

    /**
     * Sets the value of the requestTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRequestTime(XMLGregorianCalendar value) {
        this.requestTime = value;
    }

    /**
     * Gets the value of the responseTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getResponseTime() {
        return responseTime;
    }

    /**
     * Sets the value of the responseTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setResponseTime(XMLGregorianCalendar value) {
        this.responseTime = value;
    }

    /**
     * Gets the value of the id property.
     * 
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(int value) {
        this.id = value;
    }

}
