package com.jsloan.common.constant;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * @Date : 2018. 4. 12. 
 * @author Kim jongseong
 * @Descrption : 상수모음
 */
import lombok.Getter;

/**
 * @Date : 2018. 2. 5
 * @author Kim jongseong
 * @Descrption : 상수모음 
 */
public class Constants {

    /**
     * 대출상품
     */
    @Getter
    public enum LoanProduct {
          CREDIT("신용대출")
        , MORTGAGE("담보대출");

        private String productName;

        LoanProduct(String productName) {
            this.productName = productName;
        }
    }

    /**
     * 상환방식
     */
    @Getter
    public enum RepayMethod {
          PAYMT_MTRT("만기일시")
        , EQUAL_PRCP("원금균등")
        , EQUAL_PRCP_INTR("원리균등");

        private String methodName;

        RepayMethod(String methodName) {
            this.methodName = methodName;
        }
    }

    /**
     * 선불/후불
     */
    @Getter
    public enum RepayPoint {
          POST("후불")
        , PRE("선불"); 

        private String repayPointName;

        RepayPoint(String repayPointName) {
            this.repayPointName = repayPointName;
        }
    }

    /**
     * 상환종류
     */
    @Getter
    public enum RepayType {
          TERM_PAY("회차상환")
        , EARL_REDEM("중도상환");

        private String repayTypeName;

        RepayType(String repayTypeName) {
            this.repayTypeName = repayTypeName;
        }
    }

    /**
     * 상환상태
     */
    @Getter
    public enum RepayStatus {
          COMPLETE("완료")
        , UNPAID_OVER("연체미상환")
        , UNPAID_YET("미도래");

        private String repayStatusName;

        RepayStatus(String repayStatusName) {
            this.repayStatusName = repayStatusName;
        }
    }
    
    
    /**
     * 단수처리종류
     */
    @Getter
    public enum SingularType {        
          NORM_MONEY(0, BigDecimal.ROUND_FLOOR)
        , RATE(3, BigDecimal.ROUND_FLOOR);
        
        private int scale;
        private int roundMode;

        SingularType(int scale, int roundMode) {
            this.scale = scale;
            this.roundMode = roundMode;
        }
    }
    
    /**
     * 연체상태
     */
    @Getter
    public enum OverdueStatus {        
          NORMAL("정상")
        , OVERDUE("연체");
        
        private String desc;

        OverdueStatus(String desc) {
            this.desc = desc;
        }
    }
    
    /**
     * 수납금액구분
     */
    @Getter
    public enum RecvAmtDivision {        
          INTEREST("이자")
        , OVERDUE_FEE("연체료")
        , PRINCIPAL("원금");
        
        private String desc;

        RecvAmtDivision(String desc) {
            this.desc = desc;
        }
    }
    
}
