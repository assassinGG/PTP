//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.08.30 at 10:02:47 AM EDT 
//


package org.eclipse.ptp.rm.jaxb.core.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for button-group-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="button-group-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="layout-data" type="{http://org.eclipse.ptp/rm}layout-data-type" minOccurs="0"/>
 *         &lt;element name="layout" type="{http://org.eclipse.ptp/rm}layout-type" minOccurs="0"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="tooltip" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="button" type="{http://org.eclipse.ptp/rm}widget-type" maxOccurs="unbounded"/>
 *         &lt;element name="control-state" type="{http://org.eclipse.ptp/rm}control-state-type" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="group" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="saveValueTo" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="style" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="background" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "button-group-type", propOrder = {
    "layoutData",
    "layout",
    "title",
    "tooltip",
    "button",
    "controlState"
})
public class ButtonGroupType {

    @XmlElement(name = "layout-data")
    protected LayoutDataType layoutData;
    protected LayoutType layout;
    protected String title;
    protected String tooltip;
    @XmlElement(required = true)
    protected List<WidgetType> button;
    @XmlElement(name = "control-state")
    protected ControlStateType controlState;
    @XmlAttribute
    protected Boolean group;
    @XmlAttribute
    protected String saveValueTo;
    @XmlAttribute
    protected String style;
    @XmlAttribute
    protected String background;

    /**
     * Gets the value of the layoutData property.
     * 
     * @return
     *     possible object is
     *     {@link LayoutDataType }
     *     
     */
    public LayoutDataType getLayoutData() {
        return layoutData;
    }

    /**
     * Sets the value of the layoutData property.
     * 
     * @param value
     *     allowed object is
     *     {@link LayoutDataType }
     *     
     */
    public void setLayoutData(LayoutDataType value) {
        this.layoutData = value;
    }

    /**
     * Gets the value of the layout property.
     * 
     * @return
     *     possible object is
     *     {@link LayoutType }
     *     
     */
    public LayoutType getLayout() {
        return layout;
    }

    /**
     * Sets the value of the layout property.
     * 
     * @param value
     *     allowed object is
     *     {@link LayoutType }
     *     
     */
    public void setLayout(LayoutType value) {
        this.layout = value;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the tooltip property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTooltip() {
        return tooltip;
    }

    /**
     * Sets the value of the tooltip property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTooltip(String value) {
        this.tooltip = value;
    }

    /**
     * Gets the value of the button property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the button property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getButton().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WidgetType }
     * 
     * 
     */
    public List<WidgetType> getButton() {
        if (button == null) {
            button = new ArrayList<WidgetType>();
        }
        return this.button;
    }

    /**
     * Gets the value of the controlState property.
     * 
     * @return
     *     possible object is
     *     {@link ControlStateType }
     *     
     */
    public ControlStateType getControlState() {
        return controlState;
    }

    /**
     * Sets the value of the controlState property.
     * 
     * @param value
     *     allowed object is
     *     {@link ControlStateType }
     *     
     */
    public void setControlState(ControlStateType value) {
        this.controlState = value;
    }

    /**
     * Gets the value of the group property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isGroup() {
        if (group == null) {
            return false;
        } else {
            return group;
        }
    }

    /**
     * Sets the value of the group property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setGroup(Boolean value) {
        this.group = value;
    }

    /**
     * Gets the value of the saveValueTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSaveValueTo() {
        return saveValueTo;
    }

    /**
     * Sets the value of the saveValueTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSaveValueTo(String value) {
        this.saveValueTo = value;
    }

    /**
     * Gets the value of the style property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStyle() {
        return style;
    }

    /**
     * Sets the value of the style property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStyle(String value) {
        this.style = value;
    }

    /**
     * Gets the value of the background property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBackground() {
        return background;
    }

    /**
     * Sets the value of the background property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBackground(String value) {
        this.background = value;
    }

}
