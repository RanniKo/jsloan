package com.jsloan.common.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.Locale;

import com.jsloan.common.constant.Constants;

public class CalcUtil {
    
    public static final BigDecimal bMinusOne = new BigDecimal("-1");
    
    public static final BigDecimal b1 = BigDecimal.ONE;
    
    public final static MathContext DEF_CONTEXT = new MathContext(13, RoundingMode.HALF_UP);   
    
    
    /**
     * 원리금(PMT)을 산출한다.
     */
    public static BigDecimal calcPmt(BigDecimal rate, int nper, BigDecimal pv, BigDecimal fv, Constants.RepayPoint repayPoint) {
        
        BigDecimal pmt = null;
        BigDecimal bType = new BigDecimal(repayPoint == Constants.RepayPoint.POST ? "1" : "0" );        
    
        if (rate.compareTo(BigDecimal.ZERO) == 0) {
            
            pmt = divide(getMinus(pv).subtract(fv), BigDecimal.valueOf(nper));
            
        } else {
            
            if (pv.add(fv).compareTo(BigDecimal.ZERO) == 0) {                

                pmt = multiply(getMinus(pv), rate);
                
            } else {
                BigDecimal bRate = rate;
                BigDecimal bTemp1 = pow(BigDecimal.ONE.add(bRate), nper); //1+(이율/12)^기간
                BigDecimal bTemp2 = multiply(getMinus(fv), bRate).subtract(multiply(pv, bRate, bTemp1));
                BigDecimal bTemp3 = multiply(b1.add(multiply(bRate, bType)), bTemp1.subtract(b1));                
                pmt = divide(bTemp2, bTemp3);
            }
        }
        
        return sigularM(pmt);
    }    
    
    
    /**
     * 부호변환
     */    
    public static BigDecimal getMinus(BigDecimal decimal) {
        return multiply(decimal, bMinusOne);
    }

    
    /**
     * 곱셈
     */    
    public static BigDecimal multiply(BigDecimal... decimals) {
        
        BigDecimal returnDecimal = BigDecimal.ONE;
        
        for(BigDecimal decimal : decimals) {
            returnDecimal = returnDecimal.multiply(decimal, DEF_CONTEXT);
        }
        
        return returnDecimal;
    }
    
    
    /**
     * 곱셈 Scale
     */    
    public static BigDecimal multiplyM(BigDecimal... decimals) {        
        return sigularM(multiply(decimals));
    }
    
    
    /**
     * 나누기
     */    
    public static BigDecimal divide(BigDecimal decimalA, BigDecimal decimalB) {
        return decimalA.divide(decimalB, DEF_CONTEXT);
    }
    
    
    /**
     * 나누기 Scale
     */    
    public static BigDecimal divideM(BigDecimal decimalA, BigDecimal decimalB) {        
        return sigularM(divide(decimalA, decimalB));
    }
        
        
    /**
     * 제곱 
     */    
    public static BigDecimal pow(BigDecimal decimal, int arg0) {
        return decimal.pow(arg0, DEF_CONTEXT);
    }
    
    
    /**
     * 단수처리(금액)
     */    
    public static BigDecimal sigularM(BigDecimal decimal) {        
        Constants.SingularType scaleConst = Constants.SingularType.NORM_MONEY;        
        return decimal.setScale(scaleConst.getScale(), scaleConst.getRoundMode());
    }
    
    
    /**
     * 단수처리(이율)
     */    
    public static BigDecimal sigularR(BigDecimal rate) {        
        Constants.SingularType scaleConst = Constants.SingularType.RATE;        
        return rate.setScale(scaleConst.getScale(), scaleConst.getRoundMode());                
    }    
}

