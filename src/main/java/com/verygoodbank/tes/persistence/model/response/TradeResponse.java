package com.verygoodbank.tes.persistence.model.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TradeResponse {
    private String productName;
    private String currency;
    private BigDecimal price;
    private String date;

    public TradeResponse(String productName, String currency, BigDecimal price, String date) {
        this.productName = productName;
        this.currency = currency;
        this.price = price;
        this.date = date;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
