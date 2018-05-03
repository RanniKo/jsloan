package com.jsloan;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jsloan.repayment.*;

/**
 * @Date : 2018. 5. 2.
 * @author Kim jongseong
 * @Descrption : 
 */
@RunWith(Suite.class)
@SuiteClasses({
     LoanReimburse_EqualRepayPrcp.class
    ,LoanRepayPlan_EqualRepayPrcp.class
    ,LoanRepayPlan_EqualRepayPrcpIntr.class
    ,LoanRepayPlan_PaymentMaturity.class
    })
public class JsloanAllTest {

    
}