package com.jsloan.repayment.calc.impl;

import com.jsloan.Loan;
import com.jsloan.common.constant.Constants;
import com.jsloan.common.constant.Constants.RecvAmtDivision;
import com.jsloan.common.constant.Constants.RepayStatus;
import com.jsloan.common.exception.InvalidLoanException;
import com.jsloan.common.exception.LoanError;
import com.jsloan.repayment.LoanReceipt;
import com.jsloan.repayment.LoanReimbursePart;
import com.jsloan.repayment.LoanRepayPlan;
import com.jsloan.repayment.LoanRepayment;
import com.jsloan.repayment.calc.LoanEarylyRedemptionCalc;

import com.jsloan.common.util.CalcUtil;
import com.jsloan.common.util.CommUtil;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Date : 2018. 6. 29
 * @author Kim jongseong
 * @Descrption : [Normal Case] 중도상환계산
 */
public class EarlyRedemptionNormal implements LoanEarylyRedemptionCalc {
    
    /**
     * 수납내역과 상환계획을 이용하여 부분변제(상환내역 생성, 상환계획 갱신)을 반환한다. 
     */ 
    @Override
    public LoanReimbursePart dealingLoanReceipt(Loan loan, LoanReceipt receipt, List<LoanRepayPlan> loanRepayPlans, int currTerm, String lastRepaymentDate) {

        validateEarlyRedemption(loan, receipt, loanRepayPlans, currTerm, lastRepaymentDate);

        LoanReimbursePart returnLoanReimbursePart = new LoanReimbursePart();

        BigDecimal applyRate = loan.getApplyRate(currTerm).getApplyRate();

        BigDecimal applyFeeRate = loan.getApplyFeeRate(currTerm).getApplyFeeRate();

        BigDecimal balance = loanRepayPlans.get(currTerm).getAfterBalance();

        BigDecimal interestDays = new BigDecimal( CommUtil.getDaysBetween(lastRepaymentDate, receipt.getBaseDate()) );

        BigDecimal interestForRedem = CalcUtil.divideM(CalcUtil.multiply(balance, applyRate, interestDays), new BigDecimal("365"));

        BigDecimal tempPrincipal = receipt.getReceiptAmount()
                                .subtract(interestForRedem)
                                .divide(BigDecimal.ONE.add(applyFeeRate), MathContext.DECIMAL64);

        BigDecimal earlyRedemFee =  CalcUtil.multiplyM(tempPrincipal, applyFeeRate);

        BigDecimal principalForRedem = receipt.getReceiptAmount()
                                    .subtract(interestForRedem)
                                    .subtract(earlyRedemFee);

        LoanRepayPlan repayPlan = new LoanRepayPlan() ;
        repayPlan.setTermNo(loanRepayPlans.get(currTerm).getTermNo());
        repayPlan.setPlanDate(receipt.getBaseDate());
        repayPlan.setInterest(interestForRedem);
        repayPlan.setPrincipal(principalForRedem);
        repayPlan.setAmountForPay(receipt.getReceiptAmount());
        repayPlan.setOverdueFee(BigDecimal.ZERO);
        repayPlan.setRecvOverdueFee(BigDecimal.ZERO);
        repayPlan.setRecvInterest(interestForRedem);
        repayPlan.setRecvPrincipal(principalForRedem);
        repayPlan.setBalance(balance);
        repayPlan.setAfterBalance(balance.subtract(principalForRedem));
        repayPlan.setRepayStatus(RepayStatus.COMPLETE);
        repayPlan.setRepayType(Constants.RepayType.EARL_REDEM);
        repayPlan.setEarlyRedemptionFee(earlyRedemFee);
        LoanRepayment repayment = LoanRepayment.createRepayment(receipt, repayPlan, BigDecimal.ZERO, interestForRedem, principalForRedem);

        List<LoanRepayPlan> createdRepayPlans = Arrays.asList(repayPlan);
        List<LoanRepayment> createdRepayments = Arrays.asList(repayment);

        returnLoanReimbursePart.setLastTermNo(currTerm);

        returnLoanReimbursePart.setLastRepaymentDate(receipt.getBaseDate());

        returnLoanReimbursePart.setResultRepayPlans(createdRepayPlans);

        returnLoanReimbursePart.setResultRepayments(createdRepayments);

        return returnLoanReimbursePart;
        
    }

    private void validateEarlyRedemption(Loan loan, LoanReceipt receipt, List<LoanRepayPlan> loanRepayPlans, int currTerm, String lastRepaymentDate) {

        if(loanRepayPlans.get(currTerm).getRepayStatus() != RepayStatus.COMPLETE){
           throw new InvalidLoanException(LoanError.EARLY_REDEM_FORBIDDEN, receipt.getBaseDate() + ":" + receipt.getReceiptAmount());
        }

        if(currTerm + 1 < loanRepayPlans.size()){

            LoanRepayPlan repayPlan = loanRepayPlans.get(currTerm + 1);
            if(   !repayPlan.getRecvPrincipal().equals(BigDecimal.ZERO)
               || !repayPlan.getRecvInterest().equals(BigDecimal.ZERO)
               || !repayPlan.getRecvOverdueFee().equals(BigDecimal.ZERO)){
                throw new InvalidLoanException(LoanError.EARLY_REDEM_FORBIDDEN, receipt.getBaseDate() + ":" + receipt.getReceiptAmount());
            }

        }

    }

}
