package com.jsloan.repayment.calc;

import com.jsloan.Loan;
import com.jsloan.repayment.LoanReceipt;
import com.jsloan.repayment.LoanReimbursePart;
import com.jsloan.repayment.LoanRepayPlan;

import java.util.List;

/**
 * @Date : 2018. 6. 29
 * @author Kim jongseong
 * @Descrption : 중도상환 계산 Interface
 */
public interface LoanEarylyRedemptionCalc {
    /**
     * 중도상환(수수료포함) 계산
     */    
    LoanReimbursePart dealingLoanReceipt(Loan loan, LoanReceipt receipt, List<LoanRepayPlan> loanRepayPlans, int currTerm, String lastRepaymentDate);
    
}
