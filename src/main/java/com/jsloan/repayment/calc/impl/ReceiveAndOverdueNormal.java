package com.jsloan.repayment.calc.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.jsloan.Loan;
import static com.jsloan.common.constant.Constants.*;
import com.jsloan.common.util.CalcUtil;
import com.jsloan.common.util.CommUtil;
import com.jsloan.repayment.LoanReceipt;
import com.jsloan.repayment.LoanRepayPlan;
import com.jsloan.repayment.LoanRepayment;
import com.jsloan.repayment.calc.LoanReceiveAndOverdueCalc;

import lombok.Getter;

/**
 * @Date : 2018. 2. 5
 * @author Kim jongseong
 * @Descrption : [Normal Case] 수납/연체 계산 
 */
@Getter
public class ReceiveAndOverdueNormal implements LoanReceiveAndOverdueCalc {    
    
    private List<LoanRepayment> loanRepayments;    
    private List<LoanRepayPlan> fixedLoanRepayPlans;            

    /**
     * 상환계획 및 수납내역을 이용하여 상환내역/연체를 계산한다.
     * void 현재 계산된 결과는 객체 내부 List에 저장한다.
     */
    public void calcReceiveAndOverdue(String baseDate, Loan loan, List<LoanRepayPlan> loanRepayPlans) {
        
        this.loanRepayments = new ArrayList<LoanRepayment>();
        this.fixedLoanRepayPlans = new ArrayList<LoanRepayPlan>(loanRepayPlans);        
        
        this.dealingLoanReceipt(loan);
        this.handleOverdue(baseDate, loan);
    }

    
    /**
     * 수납내역을 이용하여 상환내역/연체를 계산한다. 
     * 상환내역 생성 (loanRepayments)
     * 상환계획 수정 (fixedLoanRepayPlans)
     */
    private void dealingLoanReceipt(Loan loan) { 
        
        List<LoanReceipt> receipts = loan.getReceipts();
        
        receipts.sort(new Comparator<LoanReceipt>() {
            public int compare(LoanReceipt arr1, LoanReceipt arr2) {
                return arr1.getReceiveDate().compareTo(arr2.getReceiveDate());                
            }       
        });
        
        int currTerm = 1;
        
        for(LoanReceipt receipt : receipts) {
            
            BigDecimal remainAmt = receipt.getReceiptAmount();

            for(int i=currTerm; i<= fixedLoanRepayPlans.size();i++) {
                LoanRepayPlan repayPlan = fixedLoanRepayPlans.get(i-1);
                currTerm = repayPlan.getTermNo();
                
                LoanRepayment repayment = new LoanRepayment();
                repayment.setReceiveDate(receipt.getReceiveDate());
                repayment.setBaseDate(receipt.getBaseDate());
                repayment.setPlanDate(repayPlan.getPlanDate());
                repayment.setTermNo(repayPlan.getTermNo());
                repayment.setRepayType(receipt.getRepayType());
                
                if(receipt.getBaseDate().compareTo(repayPlan.getPlanDate()) > 0){
                    BigDecimal overDueFee = overdueFeeCalc(loan, repayPlan, loanRepayments, repayment.getBaseDate());
                    repayPlan.setOverdueFee( repayPlan.getOverdueFee().add(overDueFee) );
                    repayPlan.setAmountForPay( repayPlan.getAmountForPay().add(overDueFee) );
                }
                
                BigDecimal interestForPay = repayPlan.getInterest().subtract(repayPlan.getRecvInterest());
                BigDecimal principalForPay = repayPlan.getPrincipal().subtract(repayPlan.getRecvPrincipal());
                BigDecimal overdueFeeForPay = repayPlan.getOverdueFee().subtract(repayPlan.getRecvOverdueFee());
                                
                repayment.setBalance(repayPlan.getBalance().subtract(repayPlan.getRecvPrincipal()));
                
                BigDecimal amtOrder1 = remainAmt.subtract(overdueFeeForPay);
                BigDecimal amtOrder2 = amtOrder1.subtract(interestForPay);
                BigDecimal amtOrder3 = amtOrder2.subtract(principalForPay);
                
                repayPlan.setRecvOverdueFee( getRecvAmtForRepayment(amtOrder1, overdueFeeForPay, repayPlan, RecvAmtDivision.OVERDUE_FEE) );
                repayment.setOverdueFee( getRecvAmtForPlan(amtOrder1, overdueFeeForPay) );
                
                repayPlan.setRecvInterest( getRecvAmtForRepayment(amtOrder2, interestForPay, repayPlan, RecvAmtDivision.INTEREST) );
                repayment.setInterest( getRecvAmtForPlan(amtOrder2, interestForPay) );
                
                repayPlan.setRecvPrincipal( getRecvAmtForRepayment(amtOrder3, principalForPay, repayPlan, RecvAmtDivision.PRINCIPAL) );
                repayment.setPrincipal( getRecvAmtForPlan(amtOrder3, principalForPay) );        
                
                /*
                if(amtOrder1.compareTo(BigDecimal.ZERO) > 0) {
                    repayPlan.setRecvOverdueFee(repayPlan.getOverdueFee());
                    repayment.setOverdueFee(overdueFeeForPay);                    
                }else {
                    repayPlan.setRecvOverdueFee(repayPlan.getRecvOverdueFee().add( minusToZero(overdueFeeForPay.add(amtOrder1)) ));
                    repayment.setOverdueFee( minusToZero(overdueFeeForPay.add(amtOrder1)) );
                }
                
                if(amtOrder2.compareTo(BigDecimal.ZERO) > 0) {
                    repayPlan.setRecvInterest(repayPlan.getInterest());
                    repayment.setInterest(interestForPay);                    
                }else {
                    repayPlan.setRecvInterest(repayPlan.getRecvInterest().add( minusToZero(interestForPay.add(amtOrder2)) ));
                    repayment.setInterest( minusToZero(interestForPay.add(amtOrder2)) );                    
                }
                
                if(amtOrder3.compareTo(BigDecimal.ZERO) > 0) {
                    repayPlan.setRecvPrincipal(repayPlan.getPrincipal());
                    repayment.setPrincipal(principalForPay);                    
                }else {
                    repayPlan.setRecvPrincipal(repayPlan.getRecvPrincipal().add( minusToZero(principalForPay.add(amtOrder3)) ));
                    repayment.setPrincipal( minusToZero(principalForPay.add(amtOrder3)) );                    
                }
                */
                repayment.setRepayAmount(repayment.getOverdueFee()
                                        .add(repayment.getInterest())
                                        .add(repayment.getPrincipal()));
                
                repayment.setAfterBalance(repayment.getBalance().subtract(repayment.getPrincipal()));
                remainAmt = amtOrder3;
                
                if(remainAmt.compareTo(BigDecimal.ZERO) >= 0) {
                    repayPlan.setRepayStatus(RepayStatus.COMPLETE);
                }else {
                    repayPlan.setRepayStatus(RepayStatus.UNPAID_YET);
                }
                
                loanRepayments.add(repayment);
                
                if(remainAmt.compareTo(BigDecimal.ZERO) < 0) {
                    break;
                }
                else if (remainAmt.compareTo(BigDecimal.ZERO) == 0) {
                    currTerm++;
                    break;
                }
                
            }            
            
        }        
        
    }
    
    private BigDecimal getRecvAmtForPlan(BigDecimal amtAfter, BigDecimal amtForPay) {

        if(amtAfter.compareTo(BigDecimal.ZERO) > 0) {
            return amtForPay;
        }

        return minusToZero(amtForPay.add(amtAfter));               
    }
    
    private BigDecimal getRecvAmtForRepayment(BigDecimal amtAfter, BigDecimal amtForPay, LoanRepayPlan repayPlan, RecvAmtDivision recvAmtDiv) {

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
    private void handleOverdue(String baseDate, Loan loan) {
        
        for(LoanRepayPlan repayPlan :fixedLoanRepayPlans) {
            if(repayPlan.getRepayStatus() == RepayStatus.COMPLETE) continue;            
            if(baseDate.compareTo(repayPlan.getPlanDate()) <= 0) break;
            
            BigDecimal overDueFee = overdueFeeCalc(loan, repayPlan, loanRepayments, baseDate);
            repayPlan.setOverdueFee( repayPlan.getOverdueFee().add(overDueFee) );
            repayPlan.setAmountForPay( repayPlan.getAmountForPay().add(overDueFee) );
            repayPlan.setRepayStatus(RepayStatus.UNPAID_OVER);            
        }        
        
    }
    
    
    /**
     * 입력된 일자(baseDate)를 기준으로 해당 상환계획건의 연체료를 계산한다.
     */
    private BigDecimal overdueFeeCalc(Loan loan, LoanRepayPlan repayPlan, List<LoanRepayment> loanRepayments, String baseDate) {       
        
        BigDecimal currOverdueRate = loan.getApplyRate(repayPlan.getTermNo()).getOverdueRate();
        
        String lastRepaymentDate = getLastRepaymentDate(loanRepayments, repayPlan.getTermNo());
        
        if(CommUtil.isEmpty(lastRepaymentDate)) lastRepaymentDate = repayPlan.getPlanDate();
        
        String overdueStartDt = lastRepaymentDate.compareTo(repayPlan.getPlanDate()) < 0 ? repayPlan.getPlanDate() : lastRepaymentDate;        
        
        BigDecimal overdueDays = new BigDecimal(CommUtil.getDaysBetween(overdueStartDt, baseDate));
        
        BigDecimal overdueAmt = repayPlan.getAmountForPay()
                               .subtract(repayPlan.getRecvInterest())
                               .subtract(repayPlan.getRecvPrincipal());
                
        return CalcUtil.divideM(CalcUtil.multiply(overdueAmt, currOverdueRate, overdueDays), new BigDecimal("365"));
    }
    
    
    /**
     * 입력된 상환계획 회차에 해당하는 최종 상환일자를 반환한다.
     */
    private String getLastRepaymentDate(List<LoanRepayment> loanRepayments, int term) {
        
        String lastRepaymentDate = "";
        for(int i=loanRepayments.size()-1;i>=0;i--) {
            LoanRepayment loanRepayment = loanRepayments.get(i);
            if(loanRepayment.getTermNo() == term) {
                lastRepaymentDate = loanRepayment.getBaseDate();
            }
        }
        return lastRepaymentDate;
        
    }
    
    
    private BigDecimal minusToZero(BigDecimal decimal) {
        if(decimal.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
        return decimal;
    }
    
    /**
     * 입력된 상환계획을 기준으로 여부를 반환한다.
     */
    @Override
    public OverdueStatus getOverdueStatus(List<LoanRepayPlan> loanRepayPlans) {
        
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
