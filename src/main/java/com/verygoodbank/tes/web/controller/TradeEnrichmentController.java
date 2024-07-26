package com.verygoodbank.tes.web.controller;


import com.verygoodbank.tes.persistence.model.request.Trade;
import com.verygoodbank.tes.persistence.model.response.TradeResponse;
import com.verygoodbank.tes.processor.http.RequestProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@RestController
@RequestMapping("api/v1")
public class TradeEnrichmentController {

    private final RequestProcessor<Trade, Mono<TradeResponse>> tradesProcessor;

    @Autowired
    public TradeEnrichmentController(RequestProcessor<Trade, Mono<TradeResponse>> tradesProcessor) {
        this.tradesProcessor = tradesProcessor;
    }

    @PostMapping(
            value = "/enrich",
            produces = "application/json",
            consumes = "application/json"
    )
    public Flux<TradeResponse> enrichTrades(@RequestBody List<Trade> trades) {
        return Flux.fromIterable(trades)
                .flatMap(tradesProcessor::process)
                .subscribeOn(Schedulers.parallel());
    }

}


