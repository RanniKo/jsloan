package com.jsloan.repayment;

import com.jsloan.EarlyRedemFeeRate;
import com.jsloan.Loan;
import com.jsloan.LoanRate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.jsloan.common.constant.Constants.*;
import static org.junit.Assert.assertTrue;

//원금균등대출 대출수납(중도상환)결과 (상환내역/연체) TEST
@RunWith(Parameterized.class)
public class LoanReimburse_EarlyRedem_EqualRepayPrcp {

    private Loan loan;

    private String baseDate;

    private BigDecimal expectTotAmountForPay;

    private BigDecimal expectTotInterest;

    private BigDecimal expectTotPrincipal;

    private BigDecimal expectTotOverdueFee;

    private OverdueStatus expectOverdueStatus;

    private List<LoanRepayment> expectLoanRepayments;

    private int expectRepaymentCount;

    public LoanReimburse_EarlyRedem_EqualRepayPrcp(Loan loan
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


    @Parameters
    public static List parametersForSearch() {
        Loan paraLoan;
        List<LoanRate> loanRates;
        List<LoanReceipt> loanReceipts;
        List<EarlyRedemFeeRate> earlyRedemFeeRates;
        List<Object> loanList = new ArrayList<>();


        //case1 - 1,2회차 납부 후 중도상환일부, 3회차기준일로 계산
        loanRates = new ArrayList<>();
        loanRates.add(LoanRate.builder().addRate(new BigDecimal("0.07")).stndRate(new BigDecimal("0.03")).applyRate(new BigDecimal("0.10")).overdueRate(new BigDecimal("0.24")).startTerm(1).endTerm(24).build());

        loanReceipts = new ArrayList<>();
        loanReceipts.add(LoanReceipt.builder().receiveDate("20180518").baseDate("20180518").receiptAmount(new BigDecimal("499999")).repayType(RepayType.TERM_PAY).build());
        loanReceipts.add(LoanReceipt.builder().receiveDate("20180618").baseDate("20180618").receiptAmount(new BigDecimal("496527")).repayType(RepayType.TERM_PAY).build());
        loanReceipts.add(LoanReceipt.builder().receiveDate("20180620").baseDate("20180620").receiptAmount(new BigDecimal("100000")).repayType(RepayType.EARL_REDEM).build());
        loanReceipts.add(LoanReceipt.builder().receiveDate("20180718").baseDate("20180718").receiptAmount(new BigDecimal("481990")).repayType(RepayType.TERM_PAY).build());
        loanReceipts.add(LoanReceipt.builder().receiveDate("20180801").baseDate("20180801").receiptAmount(new BigDecimal("500000")).repayType(RepayType.EARL_REDEM).build());

        earlyRedemFeeRates = new ArrayList<>();
        earlyRedemFeeRates.add(EarlyRedemFeeRate.builder().applyFeeRate(new BigDecimal("0.01")).startTerm(1).endTerm(24).build());

        paraLoan = createLoan(LoanProduct.CREDIT, "20180418", "10000000", "0", 24, loanRates, loanReceipts, earlyRedemFeeRates, "20200418");

        loanList.add(new Object[] {paraLoan, "20190118", "2807365", "330968" ,"2342304","134093", OverdueStatus.OVERDUE, null, 5});


        return loanList;
    }

    public static Loan createLoan(LoanProduct loanProduct
            , String loanDate
            , String loanAmt
            , String loanAmtForLast
            , int totLoanMonths
            , List<LoanRate> loanRates
            , List<LoanReceipt> loanReceipts
            , List<EarlyRedemFeeRate> earlyRedemFeeRates
            , String endDate) {
                          
        return   Loan.builder()
                .productCode(loanProduct)
                .loanDate(loanDate)
                .loanAmt(new BigDecimal(loanAmt))
                .loanAmtForLast(new BigDecimal(loanAmtForLast) )
                .totLoanMonths(totLoanMonths)
                .rates(loanRates)
                .receipts(loanReceipts)
                .earlyRedemFeeRates(earlyRedemFeeRates)
                .repayMethod(RepayMethod.EQUAL_PRCP)
                .endDate(endDate)
                .build();
    }     
    
    // 원금균등 Normal Case TEST
    @Test
    public void paymentMaturity_Norm() {
                        
        LoanReimburse reim = loan.getRepay(baseDate);
        System.out.println("========================================================");
        System.out.println("EQUAL_PRCP:"+loan);
        System.out.println("EQUAL_PRCP:"+reim);
        
        for(LoanRepayPlan plan:reim.getLoanRepayPlans()) {
            System.out.println("EQUAL_PRCP:"+ plan);
        }
        
        for(LoanRepayment plan:reim.getLoanRepayments()) {
            System.out.println("EQUAL_PRCP repay:"+plan);
        }
        
        assertTrue(reim.getTotAmountForPay().equals(expectTotAmountForPay));
        assertTrue(reim.getTotInterest().equals(expectTotInterest));
        assertTrue(reim.getTotPrincipal().equals(expectTotPrincipal));
        assertTrue(reim.getTotOverdueFee().equals(expectTotOverdueFee));
        assertTrue(reim.getOverDueStatus().equals(expectOverdueStatus));
        assertTrue(reim.getLoanRepayments().size()==expectRepaymentCount);
        
        if(expectLoanRepayments != null && expectLoanRepayments.size() > 0) {
            
        }
    }
    
    private boolean checkRepayments() {
        
        return true;
    }

}
