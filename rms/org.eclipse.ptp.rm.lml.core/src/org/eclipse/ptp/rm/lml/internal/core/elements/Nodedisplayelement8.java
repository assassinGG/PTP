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
 * <p>Java class for nodedisplayelement8 complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="nodedisplayelement8">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.llview.de}nodedisplayelement">
 *       &lt;sequence>
 *         &lt;element name="el9" type="{http://www.llview.de}nodedisplayelement9" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "nodedisplayelement8", propOrder = {
    "el9"
})
public class Nodedisplayelement8
    extends Nodedisplayelement
 implements Serializable {

    protected List<Nodedisplayelement9> el9;

    /**
     * Gets the value of the el9 property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the el9 property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEl9().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Nodedisplayelement9 }
     * 
     * 
     */
    public List<Nodedisplayelement9> getEl9() {
        if (el9 == null) {
            el9 = new ArrayList<Nodedisplayelement9>();
        }
        return this.el9;
    }

}
