//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.05.23 at 09:56:07 AM EDT 
//


package org.eclipse.ptp.rm.jaxb.core.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for workflow-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="workflow-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="vardefs" type="{http://org.eclipse.ptp/rm}vardefs-type" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="managed-files" type="{http://org.eclipse.ptp/rm}managed-files-type" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="step" type="{http://org.eclipse.ptp/rm}step-type" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "workflow-type", propOrder = {
    "vardefs",
    "managedFiles",
    "step"
})
public class WorkflowType {

    protected List<VardefsType> vardefs;
    @XmlElement(name = "managed-files")
    protected List<ManagedFilesType> managedFiles;
    protected List<StepType> step;

    /**
     * Gets the value of the vardefs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vardefs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVardefs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VardefsType }
     * 
     * 
     */
    public List<VardefsType> getVardefs() {
        if (vardefs == null) {
            vardefs = new ArrayList<VardefsType>();
        }
        return this.vardefs;
    }

    /**
     * Gets the value of the managedFiles property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the managedFiles property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getManagedFiles().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ManagedFilesType }
     * 
     * 
     */
    public List<ManagedFilesType> getManagedFiles() {
        if (managedFiles == null) {
            managedFiles = new ArrayList<ManagedFilesType>();
        }
        return this.managedFiles;
    }

    /**
     * Gets the value of the step property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the step property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStep().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StepType }
     * 
     * 
     */
    public List<StepType> getStep() {
        if (step == null) {
            step = new ArrayList<StepType>();
        }
        return this.step;
    }

}
