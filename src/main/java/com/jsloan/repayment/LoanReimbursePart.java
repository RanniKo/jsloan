package com.jsloan.repayment;

import java.util.HashMap;
import java.util.List;
import lombok.Data;

/**
 * @Date : 2018. 2. 5
 * @author Kim jongseong
 * @Descrption : 상환내역, 상환계획 내역 계산을 위한 DTO
 */
@Data
public class LoanReimbursePart {
    //대출상환계획(스케줄)
    private List<LoanRepayPlan> resultRepayPlans;    
    
    //대출상환내역(수납내역의 처리결과)    
    private List<LoanRepayment> resultRepayments;
    
    //최종상환일자
    private String lastRepaymentDate;
    
    //최종처리회차
    private int lastTermNo;
}
