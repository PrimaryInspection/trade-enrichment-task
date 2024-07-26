package com.verygoodbank.tes.enricher;

public interface RequestEnricher<R,T,W>{
    R enrich(T objectToEnrich, W enrichWIth);
}
