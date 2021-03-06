//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.09.07 at 09:57:43 PM EDT 
//


package org.eclipse.ptp.rm.jaxb.core.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tab-item-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tab-item-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="layout" type="{http://org.eclipse.ptp/rm}layout-type" minOccurs="0"/>
 *         &lt;element name="layout-data" type="{http://org.eclipse.ptp/rm}layout-data-type" minOccurs="0"/>
 *         &lt;element name="title" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tooltip" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="font" type="{http://org.eclipse.ptp/rm}font-type" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded" minOccurs="0">
 *           &lt;element name="composite" type="{http://org.eclipse.ptp/rm}composite-type"/>
 *           &lt;element name="tab-folder" type="{http://org.eclipse.ptp/rm}tab-folder-type"/>
 *           &lt;element name="widget" type="{http://org.eclipse.ptp/rm}widget-type"/>
 *           &lt;element name="browse" type="{http://org.eclipse.ptp/rm}browse-type"/>
 *           &lt;element name="action" type="{http://org.eclipse.ptp/rm}push-button-type"/>
 *           &lt;element name="button-group" type="{http://org.eclipse.ptp/rm}button-group-type"/>
 *           &lt;element name="viewer" type="{http://org.eclipse.ptp/rm}attribute-viewer-type"/>
 *         &lt;/choice>
 *         &lt;element name="control-state" type="{http://org.eclipse.ptp/rm}control-state-type" minOccurs="0"/>
 *       &lt;/sequence>
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
@XmlType(name = "tab-item-type", propOrder = {
    "layout",
    "layoutData",
    "title",
    "tooltip",
    "font",
    "compositeOrTabFolderOrWidget",
    "controlState"
})
public class TabItemType {

    protected LayoutType layout;
    @XmlElement(name = "layout-data")
    protected LayoutDataType layoutData;
    @XmlElement(required = true)
    protected String title;
    protected String tooltip;
    protected FontType font;
    @XmlElements({
        @XmlElement(name = "viewer", type = AttributeViewerType.class),
        @XmlElement(name = "action", type = PushButtonType.class),
        @XmlElement(name = "button-group", type = ButtonGroupType.class),
        @XmlElement(name = "widget", type = WidgetType.class),
        @XmlElement(name = "browse", type = BrowseType.class),
        @XmlElement(name = "tab-folder", type = TabFolderType.class),
        @XmlElement(name = "composite", type = CompositeType.class)
    })
    protected List<Object> compositeOrTabFolderOrWidget;
    @XmlElement(name = "control-state")
    protected ControlStateType controlState;
    @XmlAttribute
    protected String style;
    @XmlAttribute
    protected String background;

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
     * Gets the value of the font property.
     * 
     * @return
     *     possible object is
     *     {@link FontType }
     *     
     */
    public FontType getFont() {
        return font;
    }

    /**
     * Sets the value of the font property.
     * 
     * @param value
     *     allowed object is
     *     {@link FontType }
     *     
     */
    public void setFont(FontType value) {
        this.font = value;
    }

    /**
     * Gets the value of the compositeOrTabFolderOrWidget property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the compositeOrTabFolderOrWidget property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCompositeOrTabFolderOrWidget().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AttributeViewerType }
     * {@link PushButtonType }
     * {@link ButtonGroupType }
     * {@link WidgetType }
     * {@link BrowseType }
     * {@link TabFolderType }
     * {@link CompositeType }
     * 
     * 
     */
    public List<Object> getCompositeOrTabFolderOrWidget() {
        if (compositeOrTabFolderOrWidget == null) {
            compositeOrTabFolderOrWidget = new ArrayList<Object>();
        }
        return this.compositeOrTabFolderOrWidget;
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
