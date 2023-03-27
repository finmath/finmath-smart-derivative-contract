//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * A type defining the characteristics of the metal product being traded in a physically settled metal transaction.
 * 
 * <p>Java class for Metal complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Metal"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="material" type="{http://www.fpml.org/FpML-5/confirmation}Material"/&gt;
 *         &lt;sequence&gt;
 *           &lt;element name="shape" type="{http://www.fpml.org/FpML-5/confirmation}CommodityMetalShape" maxOccurs="unbounded"/&gt;
 *         &lt;/sequence&gt;
 *         &lt;sequence&gt;
 *           &lt;element name="brand" type="{http://www.fpml.org/FpML-5/confirmation}CommodityMetalBrand" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;/sequence&gt;
 *         &lt;sequence&gt;
 *           &lt;element name="grade" type="{http://www.fpml.org/FpML-5/confirmation}CommodityMetalGrade" maxOccurs="unbounded"/&gt;
 *         &lt;/sequence&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Metal", propOrder = {
    "material",
    "shape",
    "brand",
    "grade"
})
public class Metal {

    @XmlElement(required = true)
    protected Material material;
    @XmlElement(required = true)
    protected List<CommodityMetalShape> shape;
    protected List<CommodityMetalBrand> brand;
    @XmlElement(required = true)
    protected List<CommodityMetalGrade> grade;

    /**
     * Gets the value of the material property.
     * 
     * @return
     *     possible object is
     *     {@link Material }
     *     
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Sets the value of the material property.
     * 
     * @param value
     *     allowed object is
     *     {@link Material }
     *     
     */
    public void setMaterial(Material value) {
        this.material = value;
    }

    /**
     * Gets the value of the shape property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the shape property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShape().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CommodityMetalShape }
     * 
     * 
     */
    public List<CommodityMetalShape> getShape() {
        if (shape == null) {
            shape = new ArrayList<CommodityMetalShape>();
        }
        return this.shape;
    }

    /**
     * Gets the value of the brand property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the brand property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBrand().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CommodityMetalBrand }
     * 
     * 
     */
    public List<CommodityMetalBrand> getBrand() {
        if (brand == null) {
            brand = new ArrayList<CommodityMetalBrand>();
        }
        return this.brand;
    }

    /**
     * Gets the value of the grade property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a <CODE>set</CODE> method for the grade property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGrade().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CommodityMetalGrade }
     * 
     * 
     */
    public List<CommodityMetalGrade> getGrade() {
        if (grade == null) {
            grade = new ArrayList<CommodityMetalGrade>();
        }
        return this.grade;
    }

}