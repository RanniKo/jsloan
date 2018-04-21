package com.jsloan.repayment;

import java.math.BigDecimal;

import com.jsloan.common.constant.Constants;

import lombok.Builder;
import lombok.Data;

/**
 * @Date : 2018. 2. 5
 * @author Kim jongseong
 * @Descrption : 대출수납 (상환을 위해 입금된 내역)
 */
@Data
@Builder
public class LoanReceipt {

    //수납일자
    private String receiveDate;

    //기준일자
    private String baseDate;

    //수납금액
    private BigDecimal receiptAmount;
    
    //상환종류
    private Constants.RepayType repayType;

}