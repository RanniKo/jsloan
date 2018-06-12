package com.jsloan.repayment.calc.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.jsloan.Loan;
import com.jsloan.LoanRate;
import com.jsloan.common.constant.Constants.RepayStatus;
import com.jsloan.common.constant.Constants.RepayType;
import com.jsloan.common.util.CalcUtil;
import com.jsloan.common.util.CommUtil;
import com.jsloan.repayment.LoanRepayPlan;
import com.jsloan.repayment.calc.LoanAmountCalc;
import com.jsloan.repayment.calc.LoanReceiveAndOverdueCalc;


/**
 * @Date : 2018. 2. 5
 * @author Kim jongseong
 * @Descrption : [원금균등방식] 상환계획 산출
 */
public class EqualRepayPrcp extends LoanAmountCalc {
    
    public EqualRepayPrcp(LoanReceiveAndOverdueCalc loanReceiveAndOverdueCalc) {
            super(loanReceiveAndOverdueCalc);
    }        
    
    @Override
    protected List<LoanRepayPlan> getPlans(Loan loan, int startTerm, BigDecimal balance) {
        
        BigDecimal calcBalance = balance;
        
        List<LoanRepayPlan> repayPlans = new ArrayList<LoanRepayPlan>();
        
        BigDecimal monthPrincipal = CalcUtil.divideM(calcBalance, new BigDecimal(loan.getTotLoanMonths() - startTerm + 1));
        
        
        for(LoanRate rate : loan.getRates()) {
            
            //최초 Loop, 입력된 시작회차에 해당하는 Rate가 아닐경우 다음으로 continue;
            if(  repayPlans.size() == 0
             && !loan.getApplyRate(startTerm).equals(rate)) continue;
            
            BigDecimal monthRate = CalcUtil.divide(rate.getApplyRate(), new BigDecimal("12"));                                
                        
            int calcStartTerm = repayPlans.size() == 0 ? startTerm : rate.getStartTerm();
            
            for(int i=calcStartTerm; i<=rate.getEndTerm(); i++) {
                
                LoanRepayPlan repayPlan = new LoanRepayPlan();
                
                BigDecimal monthInterest = CalcUtil.multiplyM(calcBalance, monthRate);             
                if (i==loan.getTotLoanMonths()) monthPrincipal = calcBalance;
                BigDecimal amountForPay =  monthInterest.add(monthPrincipal);                    
                BigDecimal afterBalance = calcBalance.subtract(monthPrincipal);                                    
                
                String planDate = CommUtil.addMonth(loan.getLoanDate(), i, false);
                
                repayPlan.setTermNo(i);
                repayPlan.setPlanDate(planDate);                    
                repayPlan.setInterest(monthInterest);
                repayPlan.setPrincipal(monthPrincipal);
                repayPlan.setAmountForPay(amountForPay);
                repayPlan.setOverdueFee(BigDecimal.ZERO);
                repayPlan.setRecvOverdueFee(BigDecimal.ZERO);
                repayPlan.setRecvInterest(BigDecimal.ZERO);
                repayPlan.setRecvPrincipal(BigDecimal.ZERO);                
                repayPlan.setBalance(calcBalance);
                repayPlan.setAfterBalance(afterBalance);                
                repayPlan.setRepayStatus(RepayStatus.UNPAID_YET);
                repayPlan.setRepayType(RepayType.TERM_PAY);
                repayPlans.add(repayPlan);
                
                calcBalance = afterBalance;
            }                                
        }    
        
        return repayPlans;
    }    
    
}