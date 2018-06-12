package com.jsloan;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.jsloan.common.exception.InvalidLoanException;
import com.jsloan.common.exception.LoanError;
import com.jsloan.common.util.CommUtil;
import com.jsloan.repayment.LoanReceipt;
import com.jsloan.repayment.LoanReimburse;
import com.jsloan.repayment.calc.LoanAmountCalc;
import com.jsloan.repayment.calc.LoanAmountCalcFactory;

import lombok.Builder;
import lombok.Data;

import static com.jsloan.common.constant.Constants.*;
import static com.jsloan.common.exception.LoanError.*;

/**
 * @Date : 2018. 2. 5
 * @author Kim jongseong
 * @Descrption : 대출 Main Domain
 */
@Data
@Builder(buildMethodName = "buildInternal")
public class Loan {

    // 상품코드(enum)
    private LoanProduct productCode;

    // 선후불(enum)
    private RepayPoint repayPoint;

    // 금리
    private List<LoanRate> rates;
    // 상환방식 (enum)

    private RepayMethod repayMethod;
    
    // 대출일자(시작일)
    private String loanDate;

    // 종료일자(만기일)
    private String endDate;

    // 대출금액
    private BigDecimal loanAmt;
    
    // 만기지정금액
    private BigDecimal loanAmtForLast;    

    // 총개월수
    private int totLoanMonths;

    // 계산 LOGIC
    private LoanAmountCalc loanAmountCalc;
    
    // 수납내역
    private List<LoanReceipt> receipts;

    // 대출잔액
    private BigDecimal loanBalance;
    
    /**
     * 최초 Build 시점에 실행된다. 
     */
    private void init(){
        validateMember();
        validateRate();
        validateReceipt();
        loanAmountCalc = LoanAmountCalcFactory.getInstance(this);        
    }
    
    
    /**
     * 입력 맴버를 검증한다.
     */
    private void validateMember() {
        
        if(!CommUtil.dateCheck(loanDate)) {
            throw new InvalidLoanException(DATE_PARSE_EXCEPTION, "Invalid LoanDate");
        }
        
        if(!CommUtil.dateCheck(endDate)) {
            throw new InvalidLoanException(DATE_PARSE_EXCEPTION, "Invalid endDate");
        }
        
    }

    
    /**
     * LoanRate List 유효성을 검증한다.  
     * 입력된 List의 각 LoanRate의 시작/종료회차 검증
     */
    private void validateRate(){        
        if(rates == null || rates.size() == 0) throw new InvalidLoanException(INVALID_RATE_INIT);        
        
        rates.sort(new Comparator<LoanRate>() {
            public int compare(LoanRate arr1, LoanRate arr2) {
                if( arr1.getStartTerm() > arr2.getStartTerm() ) return 1;
                else return -1;
            }       
        });
        
        boolean[] checkTerm = new boolean[this.totLoanMonths];
        Arrays.fill(checkTerm, false);        
        for(LoanRate rate : rates) {
            
            if(rate.getAddRate() == null || rate.getApplyRate() == null || rate.getStndRate() == null || rate.getOverdueRate() == null) {
                throw new InvalidLoanException(INVALID_RATE_INIT, "(" + rate.getStartTerm() + "~" + rate.getEndTerm() +") Rate is null (Check add, appy, stnd, overdue)");
            }
            
            if (rate.getStartTerm() > rate.getEndTerm())  throw new InvalidLoanException(INVALID_RATE_TERM);
            
            for(int i=rate.getStartTerm()-1; i<rate.getEndTerm(); i++) {
                if(checkTerm[i])  throw new InvalidLoanException(INVALID_RATE_TERM);
                checkTerm[i] = true;
            };
            
        }
        
        for(boolean checked : checkTerm) if(!checked) throw new InvalidLoanException(INVALID_RATE_TERM);
    }
    
    
    /**
     * LoanReceipt List 유효성을 검증한다.  
     */
    private void validateReceipt() {
        
        if(receipts == null || receipts.size() == 0) return;               
                
        receipts.sort(new Comparator<LoanReceipt>() {
            public int compare(LoanReceipt arr1, LoanReceipt arr2) {
                return arr1.getReceiveDate().compareTo(arr2.getReceiveDate());                
            }       
        });
        
        for(LoanReceipt receipt : receipts) {            
            if (loanDate.compareTo(receipt.getBaseDate()) > 0) {  
                throw new InvalidLoanException(LoanError.INVALID_BASE_DATE, receipt.getBaseDate() + "=receipt baseDate is older than the loanDate");
            }
                            
        }        
    }

    
    /**
     * lombok 기본 [Builder]에 init 추가를 위한 내부 Static Class  
     */
    public static class LoanBuilder {
        public Optional<Loan> buildOptional() {
            return Optional.of(this.build());
        }

        public Loan build() {
            Loan loan = this.buildInternal();
            loan.init();
            return loan;
        }
    }    
    
    
    /**
     * 회차를 입력받아 해당시점의 적용 LoanRate를 반환한다.
     */
    public LoanRate getApplyRate(int term) {
        
        LoanRate returnRate = null; 
        
        for(LoanRate rate : rates) {
            if(rate.getStartTerm() <= term && rate.getEndTerm() >= term) {
                returnRate = rate;
                break;
            }
        }
        
        return returnRate;
    }
    
    
    /**
     * 대출스케줄(plan)확인을 위한 상환정보(LoanReimburse)를 반환한다. 
     * 수납내역/연체를 고려하지 않은 대출정보만을 사용한다.  
     */
    public LoanReimburse getFirstPlans() {       
        return loanAmountCalc.getRepayPlan(this);
    }
    
    
    /**
     * 현재일자를 기준으로 하는 상환정보(LoanReimburse)를 반환한다. 
     * 수납내역을 고려하여 연체등을 산출한다.
     */
    public LoanReimburse getRepay() {
        return loanAmountCalc.getRepay(this);
    }

    
    /**
     * 입력된 일자를 기준으로 상환정보(LoanReimburse)를 반환한다. 
     * 수납내역을 고려하여 연체등을 산출한다.
     */    
    public LoanReimburse getRepay(String baseDate) {
        return loanAmountCalc.getRepay(this, baseDate);
    }
    
}