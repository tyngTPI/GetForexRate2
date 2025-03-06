package com.example.getforexrate2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "forex_rates")
public class ForexRate {
    @Id
    private String id;
    private String date;
    private String currencyPair;
    private Double rate;
}
