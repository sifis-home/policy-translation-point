//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.2 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2022.01.14 at 03:59:01 PM CET 
//


package it.polito.elite.sifis.entities.xacml;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ConditionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ConditionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{urn:oasis:names:tc:xacml:3.0:core:schema:wd-17}Expression"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ConditionType", propOrder = {
    "expression"
})
public class ConditionType {

    @XmlAttribute(name = "DecisionTime", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String decisionTime;
	
    @XmlElementRef(name = "Expression", namespace = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17", type = JAXBElement.class)
	public JAXBElement<?> expression;

    /**
     * Gets the value of the expression property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link FunctionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AttributeSelectorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AttributeValueType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AttributeDesignatorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link VariableReferenceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ApplyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExpressionType }{@code >}
     *     
     */
    public JAXBElement<?> getExpression() {
        return expression;
    }

    /**
     * Sets the value of the expression property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link FunctionType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AttributeSelectorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AttributeValueType }{@code >}
     *     {@link JAXBElement }{@code <}{@link AttributeDesignatorType }{@code >}
     *     {@link JAXBElement }{@code <}{@link VariableReferenceType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ApplyType }{@code >}
     *     {@link JAXBElement }{@code <}{@link ExpressionType }{@code >}
     *     
     */
    public void setExpression(JAXBElement<?> value) {
        this.expression = value;
    }
    
    /**
     * Gets the value of the DecisionTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDecisionTime() {
        return decisionTime;
    }

    /**
     * Sets the value of the DecisionTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDecisionTime(String value) {
        this.decisionTime = value;
    }

}
