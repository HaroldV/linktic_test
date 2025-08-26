package com.example.product_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JsonApiResponse<T> {
    private T data;
    private List<T> dataList;

    public JsonApiResponse(T data) {
        this.data = data;
    }
}
