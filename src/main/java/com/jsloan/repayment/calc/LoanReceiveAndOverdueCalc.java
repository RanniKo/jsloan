package com.jsloan.repayment.calc;

import java.util.List;

import com.jsloan.Loan;
import com.jsloan.common.constant.Constants.OverdueStatus;
import com.jsloan.repayment.LoanReceipt;
import com.jsloan.repayment.LoanReimbursePart;
import com.jsloan.repayment.LoanRepayPlan;
import com.jsloan.repayment.LoanRepayment;

/**
 * @Date : 2018. 2. 5
 * @author Kim jongseong
 * @Descrption : 수납/연체 계산 Interface
 */
public interface LoanReceiveAndOverdueCalc {
    
    /**
     * 입력된 상환계획을 기준으로 연체상태를 반환한다.
     */
    OverdueStatus getOverdueStatus(List<LoanRepayPlan> loanRepayPlans);    
    
    /**
     * 수납내역과 상환계획을 이용하여 부분변제(상환내역 생성, 상환계획 갱신)을 반환한다. 
     */    
    LoanReimbursePart dealingLoanReceipt(Loan loan, LoanReceipt receipt, List<LoanRepayPlan> loanRepayPlans, int currTerm, String lastRepaymentDate);
    

    /**
     * 입력된 상환계획 및 최종상환일자를 기준으로 이후 상환계획에 대한 연체정보(연체금액, 상태)를 갱신한다. 
     */    
    void handleOverdue(String baseDate, Loan loan, String lastRepaymentDate, List<LoanRepayPlan> loanRepayPlans);
    
}
