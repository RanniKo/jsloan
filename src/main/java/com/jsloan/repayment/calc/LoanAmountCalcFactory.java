package com.jsloan.repayment.calc;

import com.jsloan.Loan;
import com.jsloan.common.constant.Constants;
import com.jsloan.repayment.calc.impl.EqualRepayPrcp;
import com.jsloan.repayment.calc.impl.EqualRepayPrcpIntr;
import com.jsloan.repayment.calc.impl.PaymentMaturity;
import com.jsloan.repayment.calc.impl.ReceiveAndOverdueNormal;

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

        switch(loan.getRepayMethod()) {
            
            case EQUAL_PRCP_INTR:
                return new EqualRepayPrcpIntr(new ReceiveAndOverdueNormal());
            
            case EQUAL_PRCP:
                return new EqualRepayPrcp(new ReceiveAndOverdueNormal());
            
            case PAYMT_MTRT:
                return new PaymentMaturity(new ReceiveAndOverdueNormal());
                
            default:
                return null;
            
        }
        
    }    
}
