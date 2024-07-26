package com.verygoodbank.tes.persistence.reader;

public interface Reader<IN, OUT> {
    OUT read(IN input);
}
