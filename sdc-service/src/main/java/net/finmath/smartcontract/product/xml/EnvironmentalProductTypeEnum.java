//
// This file was generated by the Eclipse Implementation of JAXB, v3.0.0 
// See https://eclipse-ee4j.github.io/jaxb-ri 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2023.03.27 at 10:23:13 AM CEST 
//


package net.finmath.smartcontract.product.xml;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EnvironmentalProductTypeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <pre>
 * &lt;simpleType name="EnvironmentalProductTypeEnum"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token"&gt;
 *     &lt;enumeration value="EUAllowance"/&gt;
 *     &lt;enumeration value="EUCredit"/&gt;
 *     &lt;enumeration value="AlternativeAllowance"/&gt;
 *     &lt;enumeration value="NOXEmissionsProduct"/&gt;
 *     &lt;enumeration value="RegionalEmissionsProduct"/&gt;
 *     &lt;enumeration value="RGGIEmissionsProduct"/&gt;
 *     &lt;enumeration value="SO2EmissionsProduct"/&gt;
 *     &lt;enumeration value="StateEmissionProduct"/&gt;
 *     &lt;enumeration value="VoluntaryEmissionProduct"/&gt;
 *     &lt;enumeration value="RenewableEnergyCertificate"/&gt;
 *     &lt;enumeration value="AUSCarbonCreditUnit"/&gt;
 *     &lt;enumeration value="AUSCarbonUnit"/&gt;
 *     &lt;enumeration value="AUSEnergySavingCertificate"/&gt;
 *     &lt;enumeration value="AUSLargeScaleGenerationCertificate"/&gt;
 *     &lt;enumeration value="AUSSmallScaleTechnologyCertificate"/&gt;
 *     &lt;enumeration value="AUSVictorianEnergyEfficiencyCertificate"/&gt;
 *     &lt;enumeration value="MXCCFECRTINTLODS"/&gt;
 *     &lt;enumeration value="NZEmissionsUnits"/&gt;
 *     &lt;enumeration value="UKRenewableObligationCertificate"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "EnvironmentalProductTypeEnum")
@XmlEnum
public enum EnvironmentalProductTypeEnum {


    /**
     * For EU Emissions Allowance Transactions: An EU Emissions Allowance.
     * 
     */
    @XmlEnumValue("EUAllowance")
    EU_ALLOWANCE("EUAllowance"),

    /**
     * For EU Emissions Allowance Transactions: An EU Emissions Credit.
     * 
     */
    @XmlEnumValue("EUCredit")
    EU_CREDIT("EUCredit"),

    /**
     * For EU Emissions Allowance Transactions: An EU Alternative Allowance.
     * 
     */
    @XmlEnumValue("AlternativeAllowance")
    ALTERNATIVE_ALLOWANCE("AlternativeAllowance"),

    /**
     * For US Emissions Allowance Transactions: A limited authorization issued by the state permitting authority or the US Environmental Protection Agency (EPA) to emit certain amounts of nitrogen oxides (Nox).
     * 
     */
    @XmlEnumValue("NOXEmissionsProduct")
    NOX_EMISSIONS_PRODUCT("NOXEmissionsProduct"),

    /**
     * For US Emissions Allowance Transactions: A emissions allowance or emissions reduction credit issued under a mult-state or provincial or other cap-and-trade Scheme.
     * 
     */
    @XmlEnumValue("RegionalEmissionsProduct")
    REGIONAL_EMISSIONS_PRODUCT("RegionalEmissionsProduct"),

    /**
     * For US Emissions Allowance Transactions: A limited authorization issued by the RGGI scheme permitting the emission of certain amounts of carbon dioxide.
     * 
     */
    @XmlEnumValue("RGGIEmissionsProduct")
    RGGI_EMISSIONS_PRODUCT("RGGIEmissionsProduct"),

    /**
     * For US Emissions Allowance Transactions: A limited authorization issued by the U.S. Environmental Protection Agency (EPA) to emit a certain amount of sulfur dioxide.
     * 
     */
    @XmlEnumValue("SO2EmissionsProduct")
    SO_2_EMISSIONS_PRODUCT("SO2EmissionsProduct"),

    /**
     * For US Emissions Allowance Transactions: Any emissions allowance or emission reduction credit created and promulgated under a U.S. state Scheme.
     * 
     */
    @XmlEnumValue("StateEmissionProduct")
    STATE_EMISSION_PRODUCT("StateEmissionProduct"),

    /**
     * For US Emissions Allowance Transactions: Any emissions allowance, emission credit, emissions offset or emissions reduction credit created or issued under a voluntary Scheme.
     * 
     */
    @XmlEnumValue("VoluntaryEmissionProduct")
    VOLUNTARY_EMISSION_PRODUCT("VoluntaryEmissionProduct"),

    /**
     * For US Renewable Energy Certificate Transactions: A Renewable Energy Certificate (“REC”) represents the environmental, social and other non-power qualities of generation of 1 megawatt-hour (MWh) of electricity from an eligible renewable energy resource.
     * 
     */
    @XmlEnumValue("RenewableEnergyCertificate")
    RENEWABLE_ENERGY_CERTIFICATE("RenewableEnergyCertificate"),

    /**
     * Australia Green Energy: Australian Carbon Credit Unit.
     * 
     */
    @XmlEnumValue("AUSCarbonCreditUnit")
    AUS_CARBON_CREDIT_UNIT("AUSCarbonCreditUnit"),

    /**
     * Australia Green Energy: Australian Carbon Unit.
     * 
     */
    @XmlEnumValue("AUSCarbonUnit")
    AUS_CARBON_UNIT("AUSCarbonUnit"),

    /**
     * Australia Green Energy: Energy Savings Certificate.
     * 
     */
    @XmlEnumValue("AUSEnergySavingCertificate")
    AUS_ENERGY_SAVING_CERTIFICATE("AUSEnergySavingCertificate"),

    /**
     * Australia Green Energy: Large-scale Generation Certificate.
     * 
     */
    @XmlEnumValue("AUSLargeScaleGenerationCertificate")
    AUS_LARGE_SCALE_GENERATION_CERTIFICATE("AUSLargeScaleGenerationCertificate"),

    /**
     * Australia Green Energy: Small-scale Technology Certificate.
     * 
     */
    @XmlEnumValue("AUSSmallScaleTechnologyCertificate")
    AUS_SMALL_SCALE_TECHNOLOGY_CERTIFICATE("AUSSmallScaleTechnologyCertificate"),

    /**
     * Australia Green Energy: Victorian Energy Efficiency Certificate.
     * 
     */
    @XmlEnumValue("AUSVictorianEnergyEfficiencyCertificate")
    AUS_VICTORIAN_ENERGY_EFFICIENCY_CERTIFICATE("AUSVictorianEnergyEfficiencyCertificate"),

    /**
     * Mexico CCFE CRT INTLODS Physical Certificates.
     * 
     */
    MXCCFECRTINTLODS("MXCCFECRTINTLODS"),

    /**
     * New Zealand Emissions Units.
     * 
     */
    @XmlEnumValue("NZEmissionsUnits")
    NZ_EMISSIONS_UNITS("NZEmissionsUnits"),

    /**
     * United Kingdom Ofgem Renewable Obligation Certificate.
     * 
     */
    @XmlEnumValue("UKRenewableObligationCertificate")
    UK_RENEWABLE_OBLIGATION_CERTIFICATE("UKRenewableObligationCertificate");
    private final String value;

    EnvironmentalProductTypeEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EnvironmentalProductTypeEnum fromValue(String v) {
        for (EnvironmentalProductTypeEnum c: EnvironmentalProductTypeEnum.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
