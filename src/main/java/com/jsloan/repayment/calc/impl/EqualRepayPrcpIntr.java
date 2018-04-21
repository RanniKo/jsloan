package com.jsloan.repayment.calc.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.jsloan.Loan;
import com.jsloan.LoanRate;
import com.jsloan.common.constant.Constants.RepayStatus;
import com.jsloan.common.util.CalcUtil;
import com.jsloan.common.util.CommUtil;
import com.jsloan.repayment.LoanRepayPlan;
import com.jsloan.repayment.calc.LoanAmountCalc;
import com.jsloan.repayment.calc.LoanReceiveAndOverdueCalc;


/**
 * @Date : 2018. 2. 5
 * @author Kim jongseong
 * @Descrption : [원리금균등방식] 상환계획 산출 
 */
public class EqualRepayPrcpIntr extends LoanAmountCalc {   
    
    public EqualRepayPrcpIntr(LoanReceiveAndOverdueCalc loanReceiveAndOverdueCalc) {
        this.loanReceiveAndOverdueCalc = loanReceiveAndOverdueCalc;
    }    

    
    @Override
    protected List<LoanRepayPlan> getPlans(Loan loan) {
        
        List<LoanRepayPlan> repayPlans = new ArrayList<LoanRepayPlan>();  
        
        int remainTerm = loan.getTotLoanMonths();
        
        BigDecimal balance = loan.getLoanAmt();
        
        for(LoanRate rate : loan.getRates()) {
            
            BigDecimal monthRate = CalcUtil.divide(rate.getApplyRate(), new BigDecimal("12"));
            
            BigDecimal pmt = CalcUtil.calcPmt(monthRate
                                                , remainTerm
                                                , CalcUtil.getMinus(balance)
                                                , loan.getLoanAmtForLast()
                                                , loan.getRepayPoint());
            
            for(int i=rate.getStartTerm(); i<=rate.getEndTerm(); i++) {
                
                LoanRepayPlan repayPlan = new LoanRepayPlan();
                
                BigDecimal monthInterest = CalcUtil.multiplyM(balance, monthRate);                    
                BigDecimal monthPrincipal = (i==loan.getTotLoanMonths()) ? balance : pmt.subtract(monthInterest);
                BigDecimal amountForPay =  monthInterest.add(monthPrincipal);   
                BigDecimal afterBalance = balance.subtract(monthPrincipal);                    
                String planDate = CommUtil.addMonth(loan.getLoanDate(), i, false);
                
                repayPlan.setTermNo(i);
                repayPlan.setPlanDate(planDate);                    
                repayPlan.setInterest(monthInterest);
                repayPlan.setPrincipal(monthPrincipal);
                repayPlan.setAmountForPay(amountForPay);
                repayPlan.setOverdueFee(BigDecimal.ZERO);
                repayPlan.setBalance(balance);
                repayPlan.setAfterBalance(afterBalance);
                repayPlan.setRepayStatus(RepayStatus.UNPAID_YET);
                repayPlan.setRecvOverdueFee(BigDecimal.ZERO);
                repayPlan.setRecvInterest(BigDecimal.ZERO);
                repayPlan.setRecvPrincipal(BigDecimal.ZERO);
                repayPlans.add(repayPlan);
                
                balance = afterBalance;
            }
            
            remainTerm -= (rate.getEndTerm() - rate.getStartTerm() + 1);
            
        } 
        
        return repayPlans;
    }        

      
    

}
