package com.friendly.services.management.profiles.entity;


import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SimpleCondition {
    private Long id;
    private String name;
}
