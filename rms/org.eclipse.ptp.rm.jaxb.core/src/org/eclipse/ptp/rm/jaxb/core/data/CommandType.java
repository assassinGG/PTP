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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for command-type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="command-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="arg" type="{http://org.eclipse.ptp/rm}arg-type" maxOccurs="unbounded"/>
 *         &lt;element name="input" type="{http://org.eclipse.ptp/rm}arg-type" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="environment" type="{http://org.eclipse.ptp/rm}name-value-pair-type" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="stdout-parser" type="{http://org.eclipse.ptp/rm}tokenizer-type" minOccurs="0"/>
 *         &lt;element name="stderr-parser" type="{http://org.eclipse.ptp/rm}tokenizer-type" minOccurs="0"/>
 *         &lt;element name="redirect-parser" type="{http://org.eclipse.ptp/rm}tokenizer-type" minOccurs="0"/>
 *         &lt;element name="pre-launch-cmd" type="{http://org.eclipse.ptp/rm}simple-command-type" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="post-launch-cmd" type="{http://org.eclipse.ptp/rm}simple-command-type" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="directory" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="redirectStderr" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="streamBufferLimit" type="{http://www.w3.org/2001/XMLSchema}int" default="-1" />
 *       &lt;attribute name="replaceEnvironment" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="waitForId" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="ignoreExitStatus" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="keepOpen" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="flags" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "command-type", propOrder = {
    "arg",
    "input",
    "environment",
    "stdoutParser",
    "stderrParser",
    "redirectParser",
    "preLaunchCmd",
    "postLaunchCmd"
})
public class CommandType {

    @XmlElement(required = true)
    protected List<ArgType> arg;
    protected List<ArgType> input;
    protected List<NameValuePairType> environment;
    @XmlElement(name = "stdout-parser")
    protected TokenizerType stdoutParser;
    @XmlElement(name = "stderr-parser")
    protected TokenizerType stderrParser;
    @XmlElement(name = "redirect-parser")
    protected TokenizerType redirectParser;
    @XmlElement(name = "pre-launch-cmd")
    protected List<SimpleCommandType> preLaunchCmd;
    @XmlElement(name = "post-launch-cmd")
    protected List<SimpleCommandType> postLaunchCmd;
    @XmlAttribute(required = true)
    protected String name;
    @XmlAttribute
    protected String directory;
    @XmlAttribute
    protected Boolean redirectStderr;
    @XmlAttribute
    protected Integer streamBufferLimit;
    @XmlAttribute
    protected Boolean replaceEnvironment;
    @XmlAttribute
    protected Boolean waitForId;
    @XmlAttribute
    protected Boolean ignoreExitStatus;
    @XmlAttribute
    protected Boolean keepOpen;
    @XmlAttribute
    protected String flags;

    /**
     * Gets the value of the arg property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the arg property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getArg().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArgType }
     * 
     * 
     */
    public List<ArgType> getArg() {
        if (arg == null) {
            arg = new ArrayList<ArgType>();
        }
        return this.arg;
    }

    /**
     * Gets the value of the input property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the input property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInput().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ArgType }
     * 
     * 
     */
    public List<ArgType> getInput() {
        if (input == null) {
            input = new ArrayList<ArgType>();
        }
        return this.input;
    }

    /**
     * Gets the value of the environment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the environment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEnvironment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NameValuePairType }
     * 
     * 
     */
    public List<NameValuePairType> getEnvironment() {
        if (environment == null) {
            environment = new ArrayList<NameValuePairType>();
        }
        return this.environment;
    }

    /**
     * Gets the value of the stdoutParser property.
     * 
     * @return
     *     possible object is
     *     {@link TokenizerType }
     *     
     */
    public TokenizerType getStdoutParser() {
        return stdoutParser;
    }

    /**
     * Sets the value of the stdoutParser property.
     * 
     * @param value
     *     allowed object is
     *     {@link TokenizerType }
     *     
     */
    public void setStdoutParser(TokenizerType value) {
        this.stdoutParser = value;
    }

    /**
     * Gets the value of the stderrParser property.
     * 
     * @return
     *     possible object is
     *     {@link TokenizerType }
     *     
     */
    public TokenizerType getStderrParser() {
        return stderrParser;
    }

    /**
     * Sets the value of the stderrParser property.
     * 
     * @param value
     *     allowed object is
     *     {@link TokenizerType }
     *     
     */
    public void setStderrParser(TokenizerType value) {
        this.stderrParser = value;
    }

    /**
     * Gets the value of the redirectParser property.
     * 
     * @return
     *     possible object is
     *     {@link TokenizerType }
     *     
     */
    public TokenizerType getRedirectParser() {
        return redirectParser;
    }

    /**
     * Sets the value of the redirectParser property.
     * 
     * @param value
     *     allowed object is
     *     {@link TokenizerType }
     *     
     */
    public void setRedirectParser(TokenizerType value) {
        this.redirectParser = value;
    }

    /**
     * Gets the value of the preLaunchCmd property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the preLaunchCmd property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPreLaunchCmd().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SimpleCommandType }
     * 
     * 
     */
    public List<SimpleCommandType> getPreLaunchCmd() {
        if (preLaunchCmd == null) {
            preLaunchCmd = new ArrayList<SimpleCommandType>();
        }
        return this.preLaunchCmd;
    }

    /**
     * Gets the value of the postLaunchCmd property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the postLaunchCmd property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPostLaunchCmd().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SimpleCommandType }
     * 
     * 
     */
    public List<SimpleCommandType> getPostLaunchCmd() {
        if (postLaunchCmd == null) {
            postLaunchCmd = new ArrayList<SimpleCommandType>();
        }
        return this.postLaunchCmd;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the directory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * Sets the value of the directory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirectory(String value) {
        this.directory = value;
    }

    /**
     * Gets the value of the redirectStderr property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isRedirectStderr() {
        if (redirectStderr == null) {
            return false;
        } else {
            return redirectStderr;
        }
    }

    /**
     * Sets the value of the redirectStderr property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRedirectStderr(Boolean value) {
        this.redirectStderr = value;
    }

    /**
     * Gets the value of the streamBufferLimit property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getStreamBufferLimit() {
        if (streamBufferLimit == null) {
            return -1;
        } else {
            return streamBufferLimit;
        }
    }

    /**
     * Sets the value of the streamBufferLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setStreamBufferLimit(Integer value) {
        this.streamBufferLimit = value;
    }

    /**
     * Gets the value of the replaceEnvironment property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isReplaceEnvironment() {
        if (replaceEnvironment == null) {
            return false;
        } else {
            return replaceEnvironment;
        }
    }

    /**
     * Sets the value of the replaceEnvironment property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setReplaceEnvironment(Boolean value) {
        this.replaceEnvironment = value;
    }

    /**
     * Gets the value of the waitForId property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isWaitForId() {
        if (waitForId == null) {
            return false;
        } else {
            return waitForId;
        }
    }

    /**
     * Sets the value of the waitForId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setWaitForId(Boolean value) {
        this.waitForId = value;
    }

    /**
     * Gets the value of the ignoreExitStatus property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIgnoreExitStatus() {
        if (ignoreExitStatus == null) {
            return false;
        } else {
            return ignoreExitStatus;
        }
    }

    /**
     * Sets the value of the ignoreExitStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIgnoreExitStatus(Boolean value) {
        this.ignoreExitStatus = value;
    }

    /**
     * Gets the value of the keepOpen property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isKeepOpen() {
        if (keepOpen == null) {
            return false;
        } else {
            return keepOpen;
        }
    }

    /**
     * Sets the value of the keepOpen property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setKeepOpen(Boolean value) {
        this.keepOpen = value;
    }

    /**
     * Gets the value of the flags property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlags() {
        return flags;
    }

    /**
     * Sets the value of the flags property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlags(String value) {
        this.flags = value;
    }

}
