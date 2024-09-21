package com.friendly.services.management.groupupdate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GroupUpdateActivationPeriod implements Serializable {
    private String from;
    private String to;
    private Integer devicesAmount;
    private Integer interval;
}