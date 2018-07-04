package com.jsloan.repayment.calc.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.jsloan.Loan;
import static com.jsloan.common.constant.Constants.*;

import com.jsloan.common.constant.Constants.OverdueStatus;
import com.jsloan.common.constant.Constants.RecvAmtDivision;
import com.jsloan.common.constant.Constants.RepayStatus;
import com.jsloan.common.util.CalcUtil;
import com.jsloan.common.util.CommUtil;
import com.jsloan.repayment.LoanReceipt;
import com.jsloan.repayment.LoanReimbursePart;
import com.jsloan.repayment.LoanRepayPlan;
import com.jsloan.repayment.LoanRepayment;
import com.jsloan.repayment.calc.LoanReceiveAndOverdueCalc;

import lombok.Getter;

/**
 * @Date : 2018. 2. 5
 * @author Kim jongseong
 * @Descrption : [Normal Case] 수납/연체 계산 
 */
public class ReceiveAndOverdueNormal implements LoanReceiveAndOverdueCalc {    
    
    /**
     * 수납내역과 상환계획을 이용하여 부분변제(상환내역 생성, 상환계획 갱신)을 반환한다. 
     */ 
    @Override
    public LoanReimbursePart dealingLoanReceipt(Loan loan, LoanReceipt receipt, List<LoanRepayPlan> loanRepayPlans, int startIndex, String lastRepaymentDate) {
        
        LoanReimbursePart returnLoanReimbursePart = new LoanReimbursePart();        
        
        List<LoanRepayPlan> createdRepayPlans = new ArrayList<>();
        List<LoanRepayment> createdRepayments = new ArrayList<>();
                    
        BigDecimal remainAmt = receipt.getReceiptAmount();

        int currTerm = 0;

        for(int i = startIndex; i< loanRepayPlans.size(); i++) {
            
            LoanRepayPlan repayPlan = new LoanRepayPlan(loanRepayPlans.get(i));
            
            currTerm = repayPlan.getTermNo();
            
            if(receipt.getBaseDate().compareTo(repayPlan.getPlanDate()) > 0){ 
                //연체이자 계산
                BigDecimal overDueFee = overdueFeeCalc(loan, repayPlan, lastRepaymentDate, receipt.getBaseDate());
                repayPlan.setOverdueFee( repayPlan.getOverdueFee().add(overDueFee) );
                repayPlan.setAmountForPay( repayPlan.getAmountForPay().add(overDueFee) );
            }
            
            BigDecimal interestForPay = repayPlan.getInterest().subtract(repayPlan.getRecvInterest());
            BigDecimal principalForPay = repayPlan.getPrincipal().subtract(repayPlan.getRecvPrincipal());
            BigDecimal overdueFeeForPay = repayPlan.getOverdueFee().subtract(repayPlan.getRecvOverdueFee());
                            
            BigDecimal amtOrder1 = remainAmt.subtract(overdueFeeForPay);
            BigDecimal amtOrder2 = amtOrder1.subtract(interestForPay);
            BigDecimal amtOrder3 = amtOrder2.subtract(principalForPay);
            
            BigDecimal repayOverDueFee =  getRecvAmtForRepayment(amtOrder1, overdueFeeForPay);
            BigDecimal repayInterest = getRecvAmtForRepayment(amtOrder2, interestForPay);
            BigDecimal repayPrincipal = getRecvAmtForRepayment(amtOrder3, principalForPay);
            
            createdRepayments.add( LoanRepayment.createRepayment(receipt, repayPlan, repayOverDueFee, repayInterest, repayPrincipal) );
            
            repayPlan.setRecvOverdueFee( getRecvAmtForPlan(amtOrder1, overdueFeeForPay, repayPlan, RecvAmtDivision.OVERDUE_FEE) );                
            repayPlan.setRecvInterest( getRecvAmtForPlan(amtOrder2, interestForPay, repayPlan, RecvAmtDivision.INTEREST) );                
            repayPlan.setRecvPrincipal( getRecvAmtForPlan(amtOrder3, principalForPay, repayPlan, RecvAmtDivision.PRINCIPAL) );                        
            
            remainAmt = amtOrder3;
            
            if(remainAmt.compareTo(BigDecimal.ZERO) >= 0) {
                repayPlan.setRepayStatus(RepayStatus.COMPLETE);
            }else {
                repayPlan.setRepayStatus(RepayStatus.UNPAID_YET);
            }                
            
            createdRepayPlans.add(repayPlan);                        
            
            if(remainAmt.compareTo(BigDecimal.ZERO) < 0) {
                break;
            }
            else if (remainAmt.compareTo(BigDecimal.ZERO) == 0) {
                currTerm++;
                break;
            }
            
        }
        
        returnLoanReimbursePart.setLastTermNo(currTerm);
        
        returnLoanReimbursePart.setLastRepaymentDate(createdRepayments.get(createdRepayments.size()-1).getBaseDate());
        
        returnLoanReimbursePart.setResultRepayPlans(createdRepayPlans);
        
        returnLoanReimbursePart.setResultRepayments(createdRepayments);        
        
        return returnLoanReimbursePart;
        
    }
    
    /**
     * 해당 회차의 수납금액계산 (for Repayment) 
     */
    private BigDecimal getRecvAmtForRepayment(BigDecimal amtAfter, BigDecimal amtForPay) {

        if(amtAfter.compareTo(BigDecimal.ZERO) > 0) {
            return amtForPay;
        }

        return minusToZero(amtForPay.add(amtAfter));               
    }

    
    /**
     * 해당 회차의 수납금액계산 (for Plan) 
     */
    private BigDecimal getRecvAmtForPlan(BigDecimal amtAfter, BigDecimal amtForPay, LoanRepayPlan repayPlan, RecvAmtDivision recvAmtDiv) {

        if(amtAfter.compareTo(BigDecimal.ZERO) > 0) {
            switch (recvAmtDiv) {
                case OVERDUE_FEE : return repayPlan.getOverdueFee();
                    
                case INTEREST : return repayPlan.getInterest();
                    
                case PRINCIPAL : return repayPlan.getPrincipal();
            }
        }
        else {
            switch (recvAmtDiv) {
                case OVERDUE_FEE : return repayPlan.getRecvOverdueFee().add( minusToZero(amtForPay.add(amtAfter)) ); 
                    
                case INTEREST : return repayPlan.getRecvInterest().add( minusToZero(amtForPay.add(amtAfter)) );
                    
                case PRINCIPAL : return repayPlan.getRecvPrincipal().add( minusToZero(amtForPay.add(amtAfter)) );
            }     
        }        
        
        return BigDecimal.ZERO;
    }    
    

    
    /**
     * 상환내역 반영 후 연체분에 대한 연체료를 계산, 상환계획에 반영한다.
     */
    @Override
    public void handleOverdue(String baseDate, Loan loan, String lastRepaymentDate, List<LoanRepayPlan> loanRepayPlans) {
        
        for(LoanRepayPlan repayPlan :loanRepayPlans) {
            if(repayPlan.getRepayStatus() == RepayStatus.COMPLETE) continue;            
            if(baseDate.compareTo(repayPlan.getPlanDate()) <= 0) break;
            
            BigDecimal overDueFee = overdueFeeCalc(loan, repayPlan, lastRepaymentDate, baseDate);
            repayPlan.setOverdueFee( repayPlan.getOverdueFee().add(overDueFee) );
            repayPlan.setAmountForPay( repayPlan.getAmountForPay().add(overDueFee) );
            repayPlan.setRepayStatus(RepayStatus.UNPAID_OVER);            
        }        
        
    }
    
    
    /**
     * 입력된 일자(baseDate)를 기준으로 해당 상환계획건의 연체료를 계산한다.
     */
    private BigDecimal overdueFeeCalc(Loan loan, LoanRepayPlan repayPlan, String lastRepaymentDate, String baseDate) {       
        
        BigDecimal currOverdueRate = loan.getApplyRate(repayPlan.getTermNo()).getOverdueRate();                
        
        if(CommUtil.isEmpty(lastRepaymentDate)) lastRepaymentDate = repayPlan.getPlanDate();
        
        String overdueStartDt = lastRepaymentDate.compareTo(repayPlan.getPlanDate()) < 0 ? repayPlan.getPlanDate() : lastRepaymentDate;        
        
        BigDecimal overdueDays = new BigDecimal(CommUtil.getDaysBetween(overdueStartDt, baseDate));
        
        BigDecimal overdueAmt = repayPlan.getAmountForPay()
                               .subtract(repayPlan.getRecvInterest())
                               .subtract(repayPlan.getRecvPrincipal());
                
        return CalcUtil.divideM(CalcUtil.multiply(overdueAmt, currOverdueRate, overdueDays), new BigDecimal("365"));
    }


    private BigDecimal minusToZero(BigDecimal decimal) {
        if(decimal.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        return decimal;
    }
    
    /**
     * 입력된 상환계획을 기준으로 연체여부를 반환한다.
     */
    @Override
    public OverdueStatus getOverdueStatus(List<LoanRepayPlan> fixedLoanRepayPlans) {
        
        for(LoanRepayPlan repayPlan :fixedLoanRepayPlans) {
            
            if(repayPlan.getRepayStatus() == RepayStatus.COMPLETE) {
                continue;
            }
            else if(repayPlan.getRepayStatus() == RepayStatus.UNPAID_OVER) {
                return OverdueStatus.OVERDUE;
            }
            else if(repayPlan.getRepayStatus() == RepayStatus.UNPAID_YET) {
                break;
            }
            
        }
        
        return OverdueStatus.NORMAL;
        
    }


}
