package com.project.hmartweb.application.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VNPayResponse {
    private String message;

    private String url;
}
