package com.jsloan.common.exception;

/**
 * @Date : 2018. 2. 5
 * @author Kim jongseong
 * @Descrption : Exception 에러코드 정의 
 */
public enum LoanError {
    
        INVALID_RATE_INIT("LoanRate not exists") //대출이율이 없습니다.        
    ,   INVALID_RATE_TERM("Invalid LoanRate term (compare with Loan totLoanMonths or check overlaped)") //대출이율의 기간이 올바르지 않습니다. 대출의 총개월수 및 겹치는 구간 Check
    ,   INVALID_BASE_DATE("Invalid baseDate for CalcReceiveAndOverdue (check loanReceipt)") //입력된 계산 기준일자보다 수납내역의 기준일자가 더 큽니다.
    ,   DATE_PARSE_EXCEPTION("Invalid date(String)") //잘못된 날짜 형태
    ,   EARLY_REDEM_FORBIDDEN("Forbidden ealry redemption (Overdue or pre-paid)"); //잘못된 날짜 형태

    private final String code;
    private final String message;

    LoanError(String message) {
        this.code = this.name();
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }    
}
