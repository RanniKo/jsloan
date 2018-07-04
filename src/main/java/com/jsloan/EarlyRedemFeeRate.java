package com.jsloan;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Date : 2018. 6. 29
 * @author Kim jongseong
 * @Descrption : 중도상환수수료율
 */
@Data
@Builder
public class EarlyRedemFeeRate {

    private BigDecimal applyFeeRate;

    private int startTerm;
    
    private int endTerm;

}