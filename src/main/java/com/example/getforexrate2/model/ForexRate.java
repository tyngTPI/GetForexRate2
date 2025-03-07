package com.example.getforexrate2.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "forex_rates")
public class ForexRate {
    @Id
    private String id;
    private Date date;
    private String currencyPair;
    private Double rate;
}
