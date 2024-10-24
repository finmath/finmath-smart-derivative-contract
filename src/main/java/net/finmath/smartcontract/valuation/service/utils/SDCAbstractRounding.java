/**
        * SDC Project
        *
        * @author Dietmar Schnabel
        *
        * This class implements some number rounding according to business conventions.
        *
        */
        package net.finmath.smartcontract.valuation.service.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * The Class  SDCAbstractRounding
 * <p>
 * Contains general rounding methods, as well as converting to strings.
 */
public abstract class SDCAbstractRounding {

    protected int scale;
    protected RoundingMode roundingMode;

    private BigDecimal round(double variable) {
        String s = Double.toString(variable);
		return new BigDecimal(s).setScale(scale, roundingMode);
    }

    /**
     * Round double.
     *
     * @param variable the double var
     * @return the rounded double
     */
    public double roundDouble(double variable) {
        return round(variable).doubleValue();
    }

    private String getAsIntString(BigDecimal variable) {
        String margin = variable.toString();
		return margin.replace(".","");
    }

    /**
     * Returns an integer value, the double left shifted
     *
     * @param variable the double var
     * @return the double as integer string
     */
    public String getRoundedValueAsIntegerString(double variable) {
        BigDecimal x = round(variable);
        return getAsIntString(x);
    }

    /**
     * Gets the double from the left shifted integer String
     *
     * @param s the integer string
     * @return the double
     */
    public double getDoubleFromIntegerString(String s) {
        String sf = null;

        if (s.length()==1) {
            sf = "0.0" + s; //TODO introduce scale
        } else if (s.length()==2) {
            sf = "0." + s;
        } else {
            int i1 = s.length() - scale -1;
            int i2 = s.length()  -1;
            sf = s.substring(0,i1+1) + "." + s.substring(i1+1,i2+1);
        }

        double x = Double.parseDouble(sf);
        return roundDouble(x);
    }
}
