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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for scheme_element5 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="scheme_element5">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.llview.de}scheme_element">
 *       &lt;sequence>
 *         &lt;element name="el6" type="{http://www.llview.de}scheme_element6" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "scheme_element5", propOrder = {
    "el6"
})
public class SchemeElement5
    extends SchemeElement
 implements Serializable {

    protected List<SchemeElement6> el6;

    /**
     * Gets the value of the el6 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the el6 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEl6().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SchemeElement6 }
     * 
     * 
     */
    public List<SchemeElement6> getEl6() {
        if (el6 == null) {
            el6 = new ArrayList<SchemeElement6>();
        }
        return this.el6;
    }

}
