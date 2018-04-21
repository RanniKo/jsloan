package com.jsloan;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

/**
 * @Date : 2018. 2. 5
 * @author Kim jongseong
 * @Descrption : 대출이율 Domain
 */
@Data
@Builder
public class LoanRate {

    private BigDecimal applyRate;

    private BigDecimal stndRate;

    private BigDecimal addRate;
    
    private BigDecimal overdueRate;

    private int startTerm;
    
    private int endTerm;

}
