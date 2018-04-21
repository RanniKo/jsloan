package com.jsloan.common.exception;

import lombok.Getter;

/**
 * @Date : 2018. 2. 5
 * @author Kim jongseong
 * @Descrption : 대출관련 Exeption 
 */
public class InvalidLoanException extends RuntimeException{

    @Getter
    private LoanError loanError;

    public InvalidLoanException(LoanError loanError) {
        super(loanError.getMessage());
        this.loanError = loanError;
    }
    
    public InvalidLoanException(LoanError loanError, String message) {
        super(message);
        this.loanError = loanError;
    }

    public InvalidLoanException(LoanError loanError, Throwable throwable) {
        super(loanError.getMessage(), throwable);
        this.loanError = loanError;
    }

    public InvalidLoanException(LoanError loanError, String message, Throwable throwable) {
        super(message, throwable);
        this.loanError = loanError;
    }    
    
}
