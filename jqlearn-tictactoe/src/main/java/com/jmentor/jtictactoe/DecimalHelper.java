package com.jmentor.jtictactoe;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by fv73ro on 6-2-2017.
 */
public class DecimalHelper {

    public static DecimalFormat defaultDecimalFormat(final String pattern) {
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator(',');
        decimalFormat.setDecimalFormatSymbols(symbols);
        return decimalFormat;
    }
}
