package com.jsloan.repayment;

import java.math.BigDecimal;

import com.jsloan.common.constant.Constants;

import lombok.Data;

/**
 * @Date : 2018. 2. 5
 * @author Kim jongseong
 * @Descrption : 대출상환계획 
 */
@Data
public class LoanRepayPlan {
    
    //상환계획 회차
    private int termNo;
    
    //상환계획 일자
    private String planDate;

    //원금 
    private BigDecimal principal;
    
    //이자 
    private BigDecimal interest;
    
    //연체료
    private BigDecimal overdueFee;
    
    //상환대상총액
    private BigDecimal amountForPay;

    //원금 (상환처리된)    
    private BigDecimal recvPrincipal;

    //이자 (상환처리된)
    private BigDecimal recvInterest;

    //연체료 (상환처리된)    
    private BigDecimal recvOverdueFee;    

    //대출잔액 (회차전)    
    private BigDecimal balance;
    
    //대출잔액 (회차후)
    private BigDecimal afterBalance;
    
    //상환상태
    private Constants.RepayStatus repayStatus;
    
    //상환종류
    private Constants.RepayType repayType;

    //중도상환수수료
    private BigDecimal earlyRedemptionFee;
    
    public LoanRepayPlan() {
        
    }    
    
    public LoanRepayPlan(LoanRepayPlan loanRepayPlan) {
        this.termNo         = loanRepayPlan.termNo        ;
        this.planDate       = loanRepayPlan.planDate      ;
        this.principal      = loanRepayPlan.principal     ;
        this.interest       = loanRepayPlan.interest      ; 
        this.overdueFee     = loanRepayPlan.overdueFee    ;
        this.amountForPay   = loanRepayPlan.amountForPay  ;
        this.recvPrincipal  = loanRepayPlan.recvPrincipal ;
        this.recvInterest   = loanRepayPlan.recvInterest  ;
        this.recvOverdueFee = loanRepayPlan.recvOverdueFee;
        this.balance        = loanRepayPlan.balance       ;
        this.afterBalance   = loanRepayPlan.afterBalance  ;
        this.repayStatus    = loanRepayPlan.repayStatus   ;
        this.repayType      = loanRepayPlan.repayType     ;
        this.earlyRedemptionFee = loanRepayPlan.earlyRedemptionFee;
    }
    
}