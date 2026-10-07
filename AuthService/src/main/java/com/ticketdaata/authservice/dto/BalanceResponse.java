package com.ticketdaata.authservice.dto;

import java.math.BigDecimal;

public class BalanceResponse {

    private String username;
    private BigDecimal balance;

    public BalanceResponse() {
    }

    public BalanceResponse(String username, BigDecimal balance) {
        this.username = username;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
