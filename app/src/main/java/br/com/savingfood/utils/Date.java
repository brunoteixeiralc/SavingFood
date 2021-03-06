package br.com.savingfood.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by brunolemgruber on 14/12/16.
 */

public class Date {

    private static SimpleDateFormat simpleDateFormat;

    public static String formatDate(java.util.Date date, int dateFormat){

//        Dateformat.SHORT // 03/04/10
//        Dateformat.MEDIUM // 03/04/2010
//        Dateformat.LONG //3 de Abril de 2010
//        Dateformat.FULL //Sábado, 3 de Abril de 2010

        Locale brasil = new Locale("pt","br");

        Calendar c = Calendar.getInstance();
        c.setTime(date);

        DateFormat df = DateFormat.getDateInstance(dateFormat,brasil);
        return df.format(c.getTime());
    }

    public static String formatToString(String format, java.util.Date date){
        simpleDateFormat = new SimpleDateFormat(format);
        String strDate = simpleDateFormat.format(date);
        return strDate;
    }
}
