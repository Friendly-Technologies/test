package com.friendly.services.uiservices.auth;

import lombok.Data;

import java.util.List;

@Data
public class AuthConfigDto {
    private String value;
    private List<String> values;
}
