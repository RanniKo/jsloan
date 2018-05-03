package com.jsloan.repayment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.jsloan.common.constant.Constants;

import lombok.Data;

/**
 * @Date : 2018. 2. 5
 * @author Kim jongseong
 * @Descrption : 대출변제 (Total 상환관련 정보)
 */
@Data
public class LoanReimburse {
        
    //기준일자
    private String baseDate;
    
    //상환대상 총금액
    private BigDecimal totAmountForPay;

    //상환대상 총이자
    private BigDecimal totInterest;

    //상환대상 총원금    
    private BigDecimal totPrincipal;

    //상환대상 총연체료    
    private BigDecimal totOverdueFee;

    //연체상태    
    private Constants.OverdueStatus overDueStatus;        
    
    //대출상환계획(스케줄)
    private List<LoanRepayPlan> loanRepayPlans;
    
    //대출상환내역(수납내역의 처리결과)    
    private List<LoanRepayment> loanRepayments;

    public void addRepayPlan(LoanRepayPlan repayPlan) {
        loanRepayPlans.add(repayPlan);
    }
    
    public void addAllRepayPlans(List<LoanRepayPlan> repayPlans) {
        loanRepayPlans.addAll(repayPlans);
    }    
    
    public void addRepayment(LoanRepayment repayment) {
        loanRepayments.add(repayment);        
    }
    
    public void addAllRepayments(List<LoanRepayment> repayments) {
        loanRepayments.addAll(repayments);        
    } 
}
