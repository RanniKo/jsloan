package com.jsloan.repayment.calc;

import java.util.List;

import com.jsloan.Loan;
import com.jsloan.common.constant.Constants.OverdueStatus;
import com.jsloan.repayment.LoanRepayPlan;
import com.jsloan.repayment.LoanRepayment;

/**
 * @Date : 2018. 2. 5
 * @author Kim jongseong
 * @Descrption : 수납/연체 계산 Interface
 */
public interface LoanReceiveAndOverdueCalc {

    /**
     * 상환계획 및 수납내역을 이용하여 상환내역/연체를 계산한다.
     * void 현재 계산된 결과는 객체 내부 List에 저장한다.
     */
    void calcReceiveAndOverdue(String baseDate, Loan loan, List<LoanRepayPlan> loanRepayPlans);

    /**
     * 입력된 상환계획을 기준으로 연체상태를 반환한다.
     */
    OverdueStatus getOverdueStatus(List<LoanRepayPlan> loanRepayPlans);    
    
    /**
     * 생성된 상환내역을 반환한다.
     */    
    List<LoanRepayment> getLoanRepayments();

    
    /**
     * 연체 및 상환이 반영된 상환계획을 반환한다.
     */
    List<LoanRepayPlan> getFixedLoanRepayPlans();
    
}
