package org.multicast.server;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;

public class Stock {
    private final String ticker;
    private BigDecimal price;
    private Instant latestTimeStamp;

    public Stock(String ticker) {
        this.ticker = ticker;
        this.price = BigDecimal.ONE;
    }

    public void changePrice(float coefficient) {
        this.price = price().multiply(BigDecimal.valueOf(coefficient));
        this.latestTimeStamp = Instant.now();
    }

    public BigDecimal price() {return price;}

    @Override
    public String toString() {
        BigDecimal rounded = price.setScale(2, RoundingMode.HALF_UP);

        return "Ticker = %s , Price = %s , Timestamp = %s".formatted(ticker, rounded.toString(), latestTimeStamp.toString());
    }
}
