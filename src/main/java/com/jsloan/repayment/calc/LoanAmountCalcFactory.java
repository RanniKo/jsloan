package com.jsloan.repayment.calc;

import com.jsloan.Loan;
import com.jsloan.common.constant.Constants;
import com.jsloan.repayment.calc.impl.*;

/**
 * @Date : 2018. 2. 5
 * @author Kim jongseong
 * @Descrption : 대출상환 관련 계산모듈 Factory
 */
public class LoanAmountCalcFactory {

    /**
     * 대출 조건별 상환계획 산출 책임분리 
     * 금액계산 Main(LoanAmountCalc) 하위 클래스를 선택 및 객체생성하여 반환한다.
     * 현재 상환방식을 기준으로만 조건이 정해진다. (조건이 바뀔 경우 변경)
     */
    public static LoanAmountCalc getInstance(Loan loan) {
        LoanReceiveAndOverdueCalc receiveAndOverdueCalcNormal = new ReceiveAndOverdueNormal();
        LoanEarylyRedemptionCalc earylyRedemptionCalcNormal = new EarlyRedemptionNormal();

        switch(loan.getRepayMethod()) {
            
            case EQUAL_PRCP_INTR:
                return new EqualRepayPrcpIntr(receiveAndOverdueCalcNormal, earylyRedemptionCalcNormal);
            
            case EQUAL_PRCP:
                return new EqualRepayPrcp(receiveAndOverdueCalcNormal, earylyRedemptionCalcNormal);
            
            case PAYMT_MTRT:
                return new PaymentMaturity(receiveAndOverdueCalcNormal, earylyRedemptionCalcNormal);
                
            default:
                return null;
            
        }
        
    }    
}
