package com.verygoodbank.tes.processor.http;

public interface RequestProcessor<IN,OUT> {
    OUT process(IN request);
}
