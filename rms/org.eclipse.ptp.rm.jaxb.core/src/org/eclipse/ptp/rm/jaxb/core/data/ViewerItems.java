//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.03.29 at 08:42:38 PM CDT 
//

package org.eclipse.ptp.rm.jaxb.core.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for viewer-items complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="viewer-items">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ref" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="allDiscovered" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="allPredefined" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "viewer-items", propOrder = { "ref" })
public class ViewerItems {

	protected List<String> ref;
	@XmlAttribute
	protected Boolean allDiscovered;
	@XmlAttribute
	protected Boolean allPredefined;

	/**
	 * Gets the value of the ref property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the ref property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getRef().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 * 
	 * 
	 */
	public List<String> getRef() {
		if (ref == null) {
			ref = new ArrayList<String>();
		}
		return this.ref;
	}

	/**
	 * Gets the value of the allDiscovered property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isAllDiscovered() {
		if (allDiscovered == null) {
			return false;
		} else {
			return allDiscovered;
		}
	}

	/**
	 * Gets the value of the allPredefined property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isAllPredefined() {
		if (allPredefined == null) {
			return false;
		} else {
			return allPredefined;
		}
	}

	/**
	 * Sets the value of the allDiscovered property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setAllDiscovered(Boolean value) {
		this.allDiscovered = value;
	}

	/**
	 * Sets the value of the allPredefined property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setAllPredefined(Boolean value) {
		this.allPredefined = value;
	}

}
