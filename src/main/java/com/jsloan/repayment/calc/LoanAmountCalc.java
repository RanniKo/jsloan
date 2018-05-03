package com.jsloan.repayment.calc;

import java.math.BigDecimal;
import java.util.List;

import com.jsloan.Loan;
import com.jsloan.common.constant.Constants;
import com.jsloan.common.exception.InvalidLoanException;
import com.jsloan.common.exception.LoanError;
import com.jsloan.common.util.CommUtil;
import com.jsloan.repayment.LoanReceipt;
import com.jsloan.repayment.LoanReimburse;
import com.jsloan.repayment.LoanRepayPlan;

/**
 * @Date : 2018. 2. 5
 * @author Kim jongseong
 * @Descrption : 상환을 위한 금액계산 Main 
 */

public abstract class LoanAmountCalc {
    
    private static final String TOT_BASE_DATE = "99991231";
    
    /**
     * 수납 / 연체처리를 위한 참조
     */
    protected LoanReceiveAndOverdueCalc loanReceiveAndOverdueCalc;
    
    /**
     * 대출정보를 이용해 기본상환계획을 산출한다.
     */
    abstract protected List<LoanRepayPlan> getPlans(Loan loan, int startTerm, BigDecimal balance);        
    
    public LoanAmountCalc(LoanReceiveAndOverdueCalc loanReceiveAndOverdueCalc) {
        this.loanReceiveAndOverdueCalc = loanReceiveAndOverdueCalc;
    }      
    
    
    /**
     * 대출정보를 이용해 최초 기본상환계획을 산출한다. (수납 및 연체 반영전)
     */    
    private List<LoanRepayPlan> getPlans(Loan loan) {
        return getPlans(loan, 1, loan.getLoanAmt());
    }    
    
    /**
     * 기본상환계획 및 총금액정보를 반환한다. (수납 및 연체 반영전)
     */
    public LoanReimburse getRepayPlan(Loan loan) {
        LoanReimburse returnReim = new LoanReimburse();
        
        returnReim.setLoanRepayPlans(getPlans(loan));
                
        returnReim.setTotInterest( getSumInPlans(returnReim.getLoanRepayPlans(), TOT_BASE_DATE, Constants.RecvAmtDivision.INTEREST) );
        
        returnReim.setTotPrincipal( getSumInPlans(returnReim.getLoanRepayPlans(), TOT_BASE_DATE, Constants.RecvAmtDivision.PRINCIPAL) );
        
        returnReim.setTotAmountForPay(
                                      returnReim.getTotInterest()
                                     .add(returnReim.getTotPrincipal())
                                     );        
        
        return returnReim;

    };

    
    /**
     * 현재일자를 기준으로 하는 상환정보(LoanReimburse)를 반환한다. 
     * 수납내역을 고려하여 연체등을 산출한다.
     */
    public LoanReimburse getRepay(Loan loan) {
       return getRepay(loan, CommUtil.getToday());
    };

    
    /**
     * 입력된 일자를 기준으로 상환정보(LoanReimburse)를 반환한다. 
     * 수납내역을 고려하여 연체등을 산출한다.
     */        
    public LoanReimburse getRepay(Loan loan, String baseDate) {
        
        validateCondition(loan, baseDate);
        
        LoanReimburse returnReim = new LoanReimburse();
        
        returnReim.setBaseDate(baseDate);
        
        returnReim.setLoanRepayPlans(getPlans(loan));
        
        loanReceiveAndOverdueCalc.calcReceiveAndOverdue(baseDate, loan, returnReim.getLoanRepayPlans());
        
        returnReim.setLoanRepayPlans(loanReceiveAndOverdueCalc.getFixedLoanRepayPlans());
        
        returnReim.setLoanRepayments(loanReceiveAndOverdueCalc.getLoanRepayments());
                
        returnReim.setTotOverdueFee( getSumInPlans(returnReim.getLoanRepayPlans(), baseDate, Constants.RecvAmtDivision.OVERDUE_FEE) );
        
        returnReim.setTotInterest( getSumInPlans(returnReim.getLoanRepayPlans(), baseDate, Constants.RecvAmtDivision.INTEREST) );
        
        returnReim.setTotPrincipal( getSumInPlans(returnReim.getLoanRepayPlans(), baseDate, Constants.RecvAmtDivision.PRINCIPAL) );
        
        returnReim.setTotAmountForPay(
                                      returnReim.getTotOverdueFee()
                                     .add(returnReim.getTotInterest())
                                     .add(returnReim.getTotPrincipal())
                                     );
        
        returnReim.setOverDueStatus( loanReceiveAndOverdueCalc.getOverdueStatus(returnReim.getLoanRepayPlans()) );
        
        return returnReim; 
        
    }
    
    
    /**
     * 입력된 정보를 검증한다.
     */
    private void validateCondition(Loan loan, String baseDate) {
        
        List<LoanReceipt> receipts = loan.getReceipts();
        
        if(receipts == null || receipts.size() == 0) return;               
        
        for(LoanReceipt receipt : receipts) {
            
            if (receipt.getBaseDate().compareTo(baseDate) > 0)  throw new InvalidLoanException(LoanError.INVALID_BASE_DATE);            
                            
        }
    }
    
        
    /**
     * 상환계획 내역의 수납금액구분별 총액을 산출한다.
     */
    private BigDecimal getSumInPlans(List<LoanRepayPlan> repayPlans, String baseDate, Constants.RecvAmtDivision recvAmtDivision) {
        
        BigDecimal returnSum = BigDecimal.ZERO;
        
        for(LoanRepayPlan repayPlan:repayPlans) {
            
            if(repayPlan.getRepayStatus() == Constants.RepayStatus.COMPLETE) continue;
            if(baseDate.compareTo(repayPlan.getPlanDate()) < 0) break;
            
            if(recvAmtDivision == Constants.RecvAmtDivision.OVERDUE_FEE) {
                
                returnSum = returnSum.add( repayPlan.getOverdueFee().subtract(repayPlan.getRecvOverdueFee()) );
                
            }else if(recvAmtDivision == Constants.RecvAmtDivision.INTEREST) {
                
                returnSum = returnSum.add( repayPlan.getInterest().subtract(repayPlan.getRecvInterest()) );
                
            }else if(recvAmtDivision == Constants.RecvAmtDivision.PRINCIPAL) {
                
                returnSum = returnSum.add( repayPlan.getPrincipal().subtract(repayPlan.getRecvPrincipal()) );
                
            }
            
        }
        
        return returnSum;
    }     

}
