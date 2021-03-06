package com.jsloan.repayment;
import static com.jsloan.common.constant.Constants.*;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.jsloan.repayment.calc.impl.EarlyRedemptionNormal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.jsloan.Loan;
import com.jsloan.LoanRate;
import com.jsloan.common.constant.Constants;
import com.jsloan.common.constant.Constants.LoanProduct;
import com.jsloan.common.constant.Constants.OverdueStatus;
import com.jsloan.common.constant.Constants.RepayMethod;
import com.jsloan.repayment.LoanReimburse;
import com.jsloan.repayment.LoanRepayPlan;
import com.jsloan.repayment.calc.LoanAmountCalc;
import com.jsloan.repayment.calc.impl.EqualRepayPrcp;
import com.jsloan.repayment.calc.impl.EqualRepayPrcpIntr;
import com.jsloan.repayment.calc.impl.ReceiveAndOverdueNormal;

//원금균등대출 상환계획 TEST
@RunWith(Parameterized.class)
public class EqualRepayPrcp_unit {
    
    private Loan loan;
    
    private String baseDate;
    
    private BigDecimal expectTotAmountForPay;
    
    private BigDecimal expectTotInterest;
    
    private BigDecimal expectTotPrincipal;
    
    private BigDecimal expectTotOverdueFee;
    
    private OverdueStatus expectOverdueStatus;
    
    private List<LoanRepayment> expectLoanRepayments;
    
    private int expectRepaymentCount;
    
    public EqualRepayPrcp_unit(    Loan loan
                                    , String baseDate
                                    , String expectTotAmountForPay
                                    , String expectTotInterest
                                    , String expectTotPrincipal
                                    , String expectTotOverdueFee
                                    , OverdueStatus expectOverdueStatus
                                    , List<LoanRepayment> expectLoanRepayments
                                    , int repaymentCount) {
        this.loan = loan;
        this.baseDate = baseDate;
        this.expectTotAmountForPay = new BigDecimal(expectTotAmountForPay);
        this.expectTotInterest = new BigDecimal(expectTotInterest);
        this.expectTotPrincipal = new BigDecimal(expectTotPrincipal);
        this.expectTotOverdueFee = new BigDecimal(expectTotOverdueFee);
        this.expectOverdueStatus = expectOverdueStatus;
        this.expectLoanRepayments = expectLoanRepayments;
        this.expectRepaymentCount = repaymentCount;
     }
    
    
    @Parameterized.Parameters
    public static List parametersForSearch() {
        Loan paraLoan;
        List<LoanRate> loanRates;
        List<LoanReceipt> loanReceipts;
        List<Object> loanList = new ArrayList<Object>();
        
        loanRates = new ArrayList<LoanRate>();        
        loanRates.add(LoanRate.builder().addRate(new BigDecimal("0.07")).stndRate(new BigDecimal("0.03")).applyRate(new BigDecimal("0.10")).overdueRate(new BigDecimal("0.24")).startTerm(1).endTerm(24).build());
        
        loanReceipts = new ArrayList<LoanReceipt>();        
        loanReceipts.add(LoanReceipt.builder().receiveDate("20180518").baseDate("20180518").receiptAmount(new BigDecimal("499999")).repayType(Constants.RepayType.TERM_PAY).build());    
        loanReceipts.add(LoanReceipt.builder().receiveDate("20180618").baseDate("20180618").receiptAmount(new BigDecimal("496527")).repayType(Constants.RepayType.TERM_PAY).build());   
        
        paraLoan = createLoan(LoanProduct.CREDIT, "20180418", "10000000", "0", 24, loanRates, loanReceipts, "20200418");
        
        loanList.add(new Object[] {paraLoan, "20180718", "493054", "76388" ,"416666","0", OverdueStatus.NORMAL, null, 2});

        
       
        //case2 - 1회차 납부(5일연체), 2회차때 함께 납부, 2회차기준일로 계산(납부대상금액없음)
        loanRates = new ArrayList<LoanRate>();        
        loanRates.add(LoanRate.builder().addRate(new BigDecimal("0.10")).stndRate(new BigDecimal("0.03")).applyRate(new BigDecimal("0.13")).overdueRate(new BigDecimal("0.24")).startTerm(1).endTerm(12).build());        
        loanRates.add(LoanRate.builder().addRate(new BigDecimal("0.07")).stndRate(new BigDecimal("0.03")).applyRate(new BigDecimal("0.10")).overdueRate(new BigDecimal("0.24")).startTerm(13).endTerm(24).build());
        
        loanReceipts = new ArrayList<LoanReceipt>();
        loanReceipts.add(LoanReceipt.builder().receiveDate("20180115").baseDate("20180115").receiptAmount(new BigDecimal("524999")).repayType(Constants.RepayType.TERM_PAY).build());    
        loanReceipts.add(LoanReceipt.builder().receiveDate("20180210").baseDate("20180210").receiptAmount(new BigDecimal("522270")).repayType(Constants.RepayType.TERM_PAY).build());   
                
        paraLoan = createLoan(LoanProduct.CREDIT, "20171210", "10000000", "0", 24, loanRates, loanReceipts, "20200418");
        
        loanList.add(new Object[] {paraLoan, "20180210", "0", "0" ,"0","0", OverdueStatus.NORMAL, null,  3});
        
        
        
        //case3 - 1,2,3회차 정상 납부 이후 연체 (기준일 20160210)
        loanRates = new ArrayList<LoanRate>();        
        loanRates.add(LoanRate.builder().addRate(new BigDecimal("0.07")).stndRate(new BigDecimal("0.13")).applyRate(new BigDecimal("0.20")).overdueRate(new BigDecimal("0.24")).startTerm(1).endTerm(12).build());        
        loanRates.add(LoanRate.builder().addRate(new BigDecimal("0.07")).stndRate(new BigDecimal("0.03")).applyRate(new BigDecimal("0.10")).overdueRate(new BigDecimal("0.24")).startTerm(13).endTerm(18).build());          
        loanRates.add(LoanRate.builder().addRate(new BigDecimal("0.07")).stndRate(new BigDecimal("0.13")).applyRate(new BigDecimal("0.20")).overdueRate(new BigDecimal("0.24")).startTerm(19).endTerm(60).build());
        
        loanReceipts = new ArrayList<LoanReceipt>();
        loanReceipts.add(LoanReceipt.builder().receiveDate("20150901").baseDate("20150901").receiptAmount(new BigDecimal("83332")).repayType(Constants.RepayType.TERM_PAY).build());       
        loanReceipts.add(LoanReceipt.builder().receiveDate("20151001").baseDate("20151001").receiptAmount(new BigDecimal("82638")).repayType(Constants.RepayType.TERM_PAY).build());
        loanReceipts.add(LoanReceipt.builder().receiveDate("20151101").baseDate("20151101").receiptAmount(new BigDecimal("81943")).repayType(Constants.RepayType.TERM_PAY).build());
        
        paraLoan = createLoan(LoanProduct.CREDIT, "20150801", "2500000", "0", 60, loanRates, loanReceipts, "20200418");
        
        loanList.add(new Object[] {paraLoan, "20160210", "248046", "116665" ,"124998","6383", OverdueStatus.OVERDUE, null, 3});
        return loanList;       
    }

    public static Loan createLoan(LoanProduct loanProduct
            , String loanDate
            , String loanAmt
            , String loanAmtForLast
            , int totLoanMonths
            , List<LoanRate> loanRates
            , List<LoanReceipt> loanReceipts
            , String endDate) {
      
            return   Loan.builder()
            .productCode(loanProduct)
            .loanDate(loanDate)
            .loanAmt(new BigDecimal(loanAmt))
            .loanAmtForLast(new BigDecimal(loanAmtForLast) )
            .totLoanMonths(totLoanMonths)
            .rates(loanRates)
            .receipts(loanReceipts)
            .repayMethod(RepayMethod.EQUAL_PRCP)
            .endDate(endDate)
            .build();
    }
    
    // 원금균등 Normal Case TEST
    //@Test
    public void equalRepayPrcp_Norm() {
                       
        LoanReimburse reim = loan.getFirstPlans();
        System.out.println("========================================================");
        System.out.println("EQUAL_PRCP:"+loan);
        System.out.println("EQUAL_PRCP:"+reim);
        
        for(LoanRepayPlan plan:reim.getLoanRepayPlans()) {
            System.out.println("EQUAL_PRCP:"+plan);
        }
        
        assertTrue(reim.getTotAmountForPay().equals(expectTotAmountForPay));
        assertTrue(reim.getTotInterest().equals(expectTotInterest));
        assertTrue(reim.getTotPrincipal().equals(expectTotPrincipal));
        
    }
    
    @Test
    public void equalRepayPrcp_Norm_Unit_Getplans() {
        LoanAmountCalc loanAmountCalc = new EqualRepayPrcp(new ReceiveAndOverdueNormal(), new EarlyRedemptionNormal());

        try {

            Method method = LoanAmountCalc.class.getDeclaredMethod("getPlans",Loan.class, int.class, BigDecimal.class);

            method.setAccessible(true);

            List<LoanRepayPlan> result = (List<LoanRepayPlan>) method.invoke(loanAmountCalc, loan, 1, new BigDecimal("2000000"));

            System.out.println("========================================================");
            System.out.println("EQUAL_PRCP:"+loan);

            for(LoanRepayPlan plan:result) {
                System.out.println("EQUAL_PRCP:"+plan);
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    
}
