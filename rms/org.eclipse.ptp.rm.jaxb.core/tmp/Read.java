//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.03.05 at 01:15:27 PM CST 
//

package org.eclipse.ptp.rm.jaxb.core.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element ref="{}match" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *       &lt;attribute name="all" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="delim" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="includeDelim" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="maxMatchLen" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
 *       &lt;attribute name="mode" default="or">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="or"/>
 *             &lt;enumeration value="and"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="save" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "match" })
@XmlRootElement(name = "read")
public class Read {

	protected List<Match> match;
	@XmlAttribute
	protected Boolean all;
	@XmlAttribute
	protected String delim;
	@XmlAttribute
	protected Boolean includeDelim;
	@XmlAttribute
	protected Integer maxMatchLen;
	@XmlAttribute
	protected String mode;
	@XmlAttribute
	protected Integer save;

	/**
	 * Gets the value of the delim property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getDelim() {
		return delim;
	}

	/**
	 * Gets the value of the match property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a
	 * snapshot. Therefore any modification you make to the returned list will
	 * be present inside the JAXB object. This is why there is not a
	 * <CODE>set</CODE> method for the match property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getMatch().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link Match }
	 * 
	 * 
	 */
	public List<Match> getMatch() {
		if (match == null) {
			match = new ArrayList<Match>();
		}
		return this.match;
	}

	/**
	 * Gets the value of the maxMatchLen property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public int getMaxMatchLen() {
		if (maxMatchLen == null) {
			return 0;
		} else {
			return maxMatchLen;
		}
	}

	/**
	 * Gets the value of the mode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMode() {
		if (mode == null) {
			return "or"; //$NON-NLS-1$
		} else {
			return mode;
		}
	}

	/**
	 * Gets the value of the save property.
	 * 
	 * @return possible object is {@link Integer }
	 * 
	 */
	public int getSave() {
		if (save == null) {
			return 0;
		} else {
			return save;
		}
	}

	/**
	 * Gets the value of the all property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isAll() {
		if (all == null) {
			return false;
		} else {
			return all;
		}
	}

	/**
	 * Gets the value of the includeDelim property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isIncludeDelim() {
		if (includeDelim == null) {
			return false;
		} else {
			return includeDelim;
		}
	}

	/**
	 * Sets the value of the all property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setAll(Boolean value) {
		this.all = value;
	}

	/**
	 * Sets the value of the delim property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setDelim(String value) {
		this.delim = value;
	}

	/**
	 * Sets the value of the includeDelim property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setIncludeDelim(Boolean value) {
		this.includeDelim = value;
	}

	/**
	 * Sets the value of the maxMatchLen property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setMaxMatchLen(Integer value) {
		this.maxMatchLen = value;
	}

	/**
	 * Sets the value of the mode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setMode(String value) {
		this.mode = value;
	}

	/**
	 * Sets the value of the save property.
	 * 
	 * @param value
	 *            allowed object is {@link Integer }
	 * 
	 */
	public void setSave(Integer value) {
		this.save = value;
	}

}
