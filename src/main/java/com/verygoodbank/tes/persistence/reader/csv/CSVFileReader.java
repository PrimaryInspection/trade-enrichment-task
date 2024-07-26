package com.verygoodbank.tes.persistence.reader.csv;

import com.verygoodbank.tes.persistence.reader.Reader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class CSVFileReader implements Reader<String, Flux<CSVRecord>> {

    private static final Logger LOGGER = getLogger(CSVFileReader.class);

    @Override
    public Flux<CSVRecord> read(String productFileName) {
        ClassPathResource resource = new ClassPathResource(productFileName);
        return Flux.using(() -> new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)),
                reader -> {
                    try {
                        CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
                        List<CSVRecord> records = parser.getRecords();
                        return Flux.fromIterable(records);
                    } catch (IOException e) {
                        return Flux.error(e);
                    }
                },
                reader -> {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).subscribeOn(Schedulers.boundedElastic());
    }
}