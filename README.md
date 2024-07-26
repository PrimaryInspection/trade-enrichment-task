# Trade Enrichment Service

## How to Run the Service

To run the service, you have two options:

1. **Run from IDE:** Execute the `TradeEnrichmentServiceApplication.java` class from your Integrated Development Environment (IDE).

OR
2. **Run from Terminal:** Execute the following command:
    ```bash
    mvn spring-boot:run
    ```

   **Important:** Performance measurements indicate that running the application via `mvn spring-boot:run` results in significantly slower request processing times (10-15x slower) compared to running it from an IDE. For instance, processing a request with 1700 trades takes approximately 150-200ms (20-40ms in subsequent calls) in the IDE, whereas it takes 1.5-2 seconds from the command line.

## How to interract with endoint

To interact with the service, send an HTTP POST request to `localhost:8080/api/v1/enrich` with the following body format (example):

```json
[
  {
    "date": "20160101",
    "productId": "1",
    "currency": "EUR",
    "price": "10"
  },
  {
    "date": "20160101",
    "productId": "2",
    "currency": "EUR",
    "price": "20.1"
  },
  {
    "date": "20160101",
    "productId": "3",
    "currency": "EUR",
    "price": "30.34"
  },
  {
    "date": "20160101",
    "productId": "11",
    "currency": "EUR",
    "price": "35.34"
  }
]
```

## Design Overview

To maintain high performance in a high-load environment, this service provides the following technologies and approaches:

- **Reactive Programming:** Utilizes Project Reactor's `Flux` and `Mono` for non-blocking, asynchronous processing of large volumes of data, enhancing scalability and responsiveness.

- **Caching:** Employs Caffeine to cache product data, reducing the need for frequent CSV file reads and improving response times by quickly retrieving frequently accessed data.

- **Parallel Processing:** Leverages `Schedulers.parallel()` for concurrent trade processing.

- **CSV Processing with NIO:** Uses Java NIO for efficient, non-blocking CSV file reading. This approach minimizes I/O overhead and enhances performance by processing large CSV files asynchronously.

## Ideas for Improvement

**Critical Improvement:**
- **Optimize CSV File Reading:** A better design for reading CSV files is crucial, as this is currently a bottleneck and critical point in the system.

**Additional Improvements:**

1. **Implement a Robust Cache:** The current caching implementation is just for example. A more advanced cache mechanism is needed.

2. **Duplicate Trade Filtering:** Introduce functionality to filter out duplicate trades. For example, if a request contains 10 identical trades, process them only once and retrieve the results from the cache.

3. **Refactor Code:** Replace parts of the code with separate Mappers/Translators to achieve a cleaner design.

4. **Add Tests:**
   - **Container Tests:** Use the Citrus Framework to implement container tests.
   - **Unit Tests:** Use JUnit to test file reading, date validation, and handle null scenarios.

5. **Controller Improvement:** Update the controller to handle `Flux<Trades>` instead of `List<Trades>`. This change is pending due to deserialization challenges; consider configuring a custom deserializer.
And add HTTP validation.

6. **Exception Handling:** Implement comprehensive exception handling with appropriate translation to HTTP responses.
