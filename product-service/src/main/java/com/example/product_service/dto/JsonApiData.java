package com.example.product_service.dto;

import lombok.Data;

@Data
public class JsonApiData<T> {
    private String id;
    private String type;
    private T attributes;
}
