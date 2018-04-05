
package hds;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for auditResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="auditResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="transfersHistory" type="{http://hds/}transfer" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "auditResult", propOrder = {
    "transfersHistory"
})
public class AuditResult {

    @XmlElement(nillable = true)
    protected List<Transfer> transfersHistory;

    /**
     * Gets the value of the transfersHistory property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transfersHistory property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransfersHistory().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Transfer }
     * 
     * 
     */
    public List<Transfer> getTransfersHistory() {
        if (transfersHistory == null) {
            transfersHistory = new ArrayList<Transfer>();
        }
        return this.transfersHistory;
    }

}
