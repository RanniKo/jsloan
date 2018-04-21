package com.jsloan.repayment;
import static com.jsloan.common.constant.Constants.*;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.jsloan.Loan;
import com.jsloan.LoanRate;
import com.jsloan.repayment.LoanReimburse;
import com.jsloan.repayment.LoanRepayPlan;

//원리금균등대출 상환계획 TEST
@RunWith(Parameterized.class)
public class LoanRepayPlan_EqualRepayPrcpIntr {
    
    private Loan loan;   

    private BigDecimal expectTotAmountForPay;
    
    private BigDecimal expectTotInterest;
    
    private BigDecimal expectTotPrincipal;
    
    
    public LoanRepayPlan_EqualRepayPrcpIntr(Loan loan, String expectTotAmountForPay, String expectTotInterest, String expectTotPrincipal) {
        this.loan = loan;
        this.expectTotAmountForPay = new BigDecimal(expectTotAmountForPay);
        this.expectTotInterest = new BigDecimal(expectTotInterest);
        this.expectTotPrincipal = new BigDecimal(expectTotPrincipal);
     }
    
    
    @Parameterized.Parameters
    public static List parametersForSearch() {
        Loan paraLoan;
        List<LoanRate> loanRates;
        List<Object> loanList = new ArrayList<Object>();
        
        //case1
        loanRates = new ArrayList<LoanRate>();        
        loanRates.add(LoanRate.builder().addRate(new BigDecimal("0.07")).stndRate(new BigDecimal("0.03")).applyRate(new BigDecimal("0.10")).overdueRate(new BigDecimal("0.24")).startTerm(1).endTerm(24).build());                  
        paraLoan = createLoan(LoanProduct.CREDIT, "20180418", "10000000", "0", 24, loanRates, "20200418");
        loanList.add(new Object[] {paraLoan, "11074769", "1074769" ,"10000000"});

        //case2
        loanRates = new ArrayList<LoanRate>();        
        loanRates.add(LoanRate.builder().addRate(new BigDecimal("0.10")).stndRate(new BigDecimal("0.03")).applyRate(new BigDecimal("0.13")).overdueRate(new BigDecimal("0.24")).startTerm(1).endTerm(12).build());        
        loanRates.add(LoanRate.builder().addRate(new BigDecimal("0.07")).stndRate(new BigDecimal("0.03")).applyRate(new BigDecimal("0.10")).overdueRate(new BigDecimal("0.24")).startTerm(13).endTerm(24).build());          
        paraLoan = createLoan(LoanProduct.CREDIT, "20171210", "10000000", "0", 24, loanRates, "20200418");
        loanList.add(new Object[] {paraLoan, "11320514", "1320514" ,"10000000"});
        
        //case3
        loanRates = new ArrayList<LoanRate>();        
        loanRates.add(LoanRate.builder().addRate(new BigDecimal("0.07")).stndRate(new BigDecimal("0.13")).applyRate(new BigDecimal("0.20")).overdueRate(new BigDecimal("0.24")).startTerm(1).endTerm(12).build());        
        loanRates.add(LoanRate.builder().addRate(new BigDecimal("0.07")).stndRate(new BigDecimal("0.03")).applyRate(new BigDecimal("0.10")).overdueRate(new BigDecimal("0.24")).startTerm(13).endTerm(18).build());          
        loanRates.add(LoanRate.builder().addRate(new BigDecimal("0.07")).stndRate(new BigDecimal("0.13")).applyRate(new BigDecimal("0.20")).overdueRate(new BigDecimal("0.24")).startTerm(19).endTerm(60).build());
        paraLoan = createLoan(LoanProduct.CREDIT, "20150118", "10000000", "0", 60, loanRates, "20200418");
        loanList.add(new Object[] {paraLoan, "15409637", "5409637" ,"10000000"});

        //case4        
        loanRates = new ArrayList<LoanRate>();        
        loanRates.add(LoanRate.builder().addRate(new BigDecimal("0.07")).stndRate(new BigDecimal("0.03")).applyRate(new BigDecimal("0.12")).overdueRate(new BigDecimal("0.24")).startTerm(1).endTerm(12).build());        
        loanRates.add(LoanRate.builder().addRate(new BigDecimal("0.03")).stndRate(new BigDecimal("0.02")).applyRate(new BigDecimal("0.05")).overdueRate(new BigDecimal("0.24")).startTerm(13).endTerm(48).build());          
        paraLoan = createLoan(LoanProduct.CREDIT, "20180218", "25000000", "0", 48, loanRates, "20200418");
        loanList.add(new Object[] {paraLoan, "29286208", "4286208" ,"25000000"});       
        
        return loanList;       
    }
    
    public static Loan createLoan(LoanProduct loanProduct
                                , String loanDate
                                , String loanAmt
                                , String loanAmtForLast
                                , int totLoanMonths
                                , List<LoanRate> loanRates
                                , String endDate) {
                          
        return   Loan.builder()
                .productCode(loanProduct)
                .loanDate(loanDate)
                .loanAmt(new BigDecimal(loanAmt))
                .loanAmtForLast(new BigDecimal(loanAmtForLast) )
                .totLoanMonths(totLoanMonths)
                .rates(loanRates)
                .repayMethod(RepayMethod.EQUAL_PRCP_INTR)
                .endDate(endDate)
                .build();
    }     
    
    // 원리금균등 Normal Case TEST
    @Test
    public void equalRepayPrcp_Norm() {
                       
        LoanReimburse reim = loan.getRepayPlan();
        System.out.println("========================================================");
        System.out.println("EQUAL_PRCP_INTR:"+loan);
        System.out.println("EQUAL_PRCP_INTR:"+reim);
        
        for(LoanRepayPlan plan:reim.getLoanRepayPlans()) {
            System.out.println("EQUAL_PRCP_INTR:"+plan);
        }
        
        assertTrue(reim.getTotAmountForPay().equals(expectTotAmountForPay));
        assertTrue(reim.getTotInterest().equals(expectTotInterest));
        assertTrue(reim.getTotPrincipal().equals(expectTotPrincipal));
        
    }

}
