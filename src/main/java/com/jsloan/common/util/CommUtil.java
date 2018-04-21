package com.jsloan.common.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.jsloan.common.exception.InvalidLoanException;
import com.jsloan.common.exception.LoanError;

public class CommUtil {
        
    public static final Locale CURRENT_LOCALE = Locale.KOREA;                
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd", CURRENT_LOCALE);
    
    /**
     * 빈문자 체크
     */
    public static boolean isEmpty(String val) {
        if (val == null || val.trim().length() == 0)
            return true;
        else
            return false;
    }
    
    
    /**
     * 입력값이 null 이면 초기값을 반환 한다.
     */
    public static String nvl(String str, String initValue) {
        if( (str == null) || (str.trim().equals("")) || (str.trim().equalsIgnoreCase("NULL")) ) {
            return (initValue == null) ? "" : initValue;
        } 
        return str;
    }

    
    /**
     * 입력값이 null 이면 Empty String[""] 을 반환 한다.
     */
    public static String nvl(String str) {
        return nvl(str, null);
    }

    
    /**
     * 입력된 날짜(String[yyyyMMdd])가 유효한 날짜인지 Check한다.
     */    
    public static boolean dateCheck(String s) {
        boolean isValid = true;
        Date date = null;
    
        try {
        
            date = DATE_FORMAT.parse(s);
    
            if (!DATE_FORMAT.format(date).equals(s)) {
                isValid = false;
            }
        } catch (ParseException e) {
            isValid = false;
        }
        return isValid;
    }

    
    /**
     * 입력된 연도와 월을 이용해 마지막일자를 리턴한다.
     */    
    public static int lastDay(int year, int month) {
        int day = 0;
        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                day = 31;
                break;
            case 2:
                if ((year % 4) == 0) {
                    if ((year % 100) == 0 && (year % 400) != 0) {
                        day = 28;
                    } else {
                        day = 29;
                    }
                } else {
                    day = 28;
                }
                break;
            default:
                day = 30;
        }
        return day;
    }           
    
    /**
     * 입력된 날짜(String[yyyyMMdd])의 월 수를 offset한다. 매월마지막일자를 가져가는 것도 가능한다.
     */
    public static String addMonth(String ctymd, int addMonth, boolean strIsEndDayOfMonth) {

        int year = Integer.parseInt(ctymd.substring(0, 4));
        int month = Integer.parseInt(ctymd.substring(4, 6));
        int day = Integer.parseInt(ctymd.substring(6, 8));
    
        int subtractYear = (int) (addMonth / 12);
        int subtractMonth = addMonth % 12;
        year += subtractYear;
        month += subtractMonth;
        
        if (month <= 0) {
            year--;
            month = 12 + month;
        } else if (month > 12) {
            year++;
            month = month % 12;
        }
    
        //  말일에 대해서 다시 말일로 계산하도록 처리
        if(strIsEndDayOfMonth) {
            day = lastDay(year, month);
        }
    
        DecimalFormat fourDf = new java.text.DecimalFormat("0000");
        DecimalFormat twoDf = new java.text.DecimalFormat("00");
        String tempDate = fourDf.format(year) + twoDf.format(month) + twoDf.format(day);
    
        if (!dateCheck(tempDate)) {
            day = lastDay(year, month);
            tempDate = fourDf.format(year) + twoDf.format(month) + twoDf.format(day);
        }

        return tempDate;
    }

    
    /**
     * 입력된 날짜(String[yyyyMMdd]) 사이의 일수를 반환한다.
     */
    public static int getDaysBetween(String from, String to) {
        long duration = 0;
        try {
            if (from.equals(to)) {
                return 0;
            }
        
            Date d1 = DATE_FORMAT.parse(from);
            Date d2 = DATE_FORMAT.parse(to);
            duration = d2.getTime() - d1.getTime();
        }catch(ParseException e) {
            throw new InvalidLoanException(LoanError.DATE_PARSE_EXCEPTION);
        }
        return (int) (duration / (1000 * 60 * 60 * 24));
    }
    

 
    /**
     * 현재 날짜 (String[yyyyMMdd])를 반환한다. 
     */
    public static String getToday() {
        Date date = Calendar.getInstance().getTime();
        
        return DATE_FORMAT.format(date);
    }
  
}
