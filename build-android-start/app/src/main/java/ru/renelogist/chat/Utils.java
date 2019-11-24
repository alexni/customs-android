package ru.renelogist.chat;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static Date stringToDate(String aDate, String aFormat) {

        try {
            if(aDate==null) return null;
            ParsePosition pos = new ParsePosition(0);
            SimpleDateFormat simpledateformat = new SimpleDateFormat(aFormat);
            Date stringDate = simpledateformat.parse(aDate, pos);
            return stringDate;
        } catch (Exception e) {
            return null;
        }
    }
}
