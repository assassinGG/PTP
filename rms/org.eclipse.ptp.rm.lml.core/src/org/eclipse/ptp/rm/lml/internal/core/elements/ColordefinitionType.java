//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.04.24 at 09:34:46 AM CEST 
//


package org.eclipse.ptp.rm.lml.internal.core.elements;

import java.io.Serializable;


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Collects a list of colorconstants.
 * 
 * <p>Java class for colordefinition_type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="colordefinition_type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="colorname" type="{http://www.llview.de}colorconstant_type" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "colordefinition_type", propOrder = {
    "colorname"
})
public class ColordefinitionType  implements Serializable {

    @XmlElement(namespace = "http://www.llview.de")
    protected List<ColorconstantType> colorname;

    /**
     * Gets the value of the colorname property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the colorname property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getColorname().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ColorconstantType }
     * 
     * 
     */
    public List<ColorconstantType> getColorname() {
        if (colorname == null) {
            colorname = new ArrayList<ColorconstantType>();
        }
        return this.colorname;
    }

}
