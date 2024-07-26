package com.verygoodbank.tes.persistence.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Trade {

    private String productId;
    private BigDecimal price;
    private String date;
    private String currency; // better to use enum here, or some restricted dataset.

}
