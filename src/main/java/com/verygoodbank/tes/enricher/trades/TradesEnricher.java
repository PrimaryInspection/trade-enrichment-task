package com.verygoodbank.tes.enricher.trades;

import com.verygoodbank.tes.enricher.RequestEnricher;
import com.verygoodbank.tes.persistence.model.request.Trade;
import com.verygoodbank.tes.persistence.model.response.TradeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Service
public class TradesEnricher implements RequestEnricher<Mono<TradeResponse>, Trade, Map<String, String>> {
    private static final Logger logger = LoggerFactory.getLogger(TradesEnricher.class);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public Mono<TradeResponse> enrich(Trade trade, Map<String, String> productIdMap) {
        logger.info("Enriching trade: {} with productIdMap: {}", trade, productIdMap);
        LocalDate date = validateDate(trade.getDate());

        // If the date is invalid, return an empty Mono to skip processing this row
        if (date == null) {
            logger.warn("Skipping trade due to invalid date: {}", trade);
            return Mono.empty();
        }
        String productName = productIdMap.getOrDefault(trade.getProductId(), "Missing Product Name");
        return Mono.just(new TradeResponse(
                productName,
                trade.getCurrency(),
                trade.getPrice(),
                date.format(DATE_FORMATTER)
        ));
    }

    private LocalDate validateDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format for trade: {}", dateStr);
            return null; // Return null to indicate an invalid date
        }
    }
}