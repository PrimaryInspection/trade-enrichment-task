package com.verygoodbank.tes.processor.http.trades;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.verygoodbank.tes.enricher.RequestEnricher;
import com.verygoodbank.tes.persistence.model.request.Trade;
import com.verygoodbank.tes.persistence.model.response.TradeResponse;
import com.verygoodbank.tes.persistence.reader.Reader;
import com.verygoodbank.tes.processor.http.RequestProcessor;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class TradesProcessor implements RequestProcessor<Trade, Mono<TradeResponse>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradesProcessor.class);

    private final Reader<String, Flux<CSVRecord>> csvFileReader;
    private final RequestEnricher<Mono<TradeResponse>, Trade, Map<String, String>> tradesEnricher;
    private final Cache<String, Map<String, String>> productDataCache;

    public TradesProcessor(Reader<String, Flux<CSVRecord>> csvFileReader,
                           RequestEnricher<Mono<TradeResponse>, Trade, Map<String, String>> tradesEnricher) {
        this.csvFileReader = csvFileReader;
        this.tradesEnricher = tradesEnricher;
        this.productDataCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();
        LOGGER.info("Initialized TradesProcessor with CSV file reader and trades enricher.");
    }

    @Override
    public Mono<TradeResponse> process(Trade trade) {
        LOGGER.debug("Processing trade: {}", trade);
        return getProductData()
                .flatMap(productIdToNameMap -> {
                    LOGGER.debug("Enriching trade with product data: {}", productIdToNameMap);
                    return tradesEnricher.enrich(trade, productIdToNameMap);
                })
                .doOnSuccess(response -> LOGGER.info("Successfully processed trade: {}", trade))
                .doOnError(e -> LOGGER.error("Error processing trade {}: {}", trade, e.getMessage()))
                .onErrorResume(e -> Mono.empty());
    }

    private Mono<Map<String, String>> getProductData() {
        LOGGER.debug("Fetching product data from cache or loading from CSV.");
        return Mono.defer(() -> {
            Map<String, String> cachedData = productDataCache.getIfPresent("product_data");
            if (cachedData != null) {
                LOGGER.debug("Product data retrieved from cache.");
                return Mono.just(cachedData);
            } else {
                LOGGER.debug("Product data not found in cache, loading from CSV.");
                return loadProductData()
                        .doOnNext(data -> {
                            LOGGER.info("Loaded product data with {} entries", data.size());
                            productDataCache.put("product_data", data);
                        })
                        .doOnError(e -> LOGGER.error("Error loading product data: {}", e.getMessage()));
            }
        });
    }

    private Mono<Map<String, String>> loadProductData() {
        LOGGER.debug("Reading product data from CSV file.");
        return csvFileReader.read("product.csv") // fixme Hardcoded due to lack of requirements
                .collectMap(
                        record -> record.get("product_id"),
                        record -> record.get("product_name"))
                .doOnNext(map -> LOGGER.info("Loaded product data with {} entries", map.size()))
                .doOnError(e -> LOGGER.error("Error reading CSV file: {}", e.getMessage()));
    }
}