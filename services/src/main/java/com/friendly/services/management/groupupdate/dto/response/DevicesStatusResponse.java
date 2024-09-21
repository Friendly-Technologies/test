package com.friendly.services.management.groupupdate.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DevicesStatusResponse implements Serializable {
    Long completed;
    Long pending;
    Long offline;
    Long notSent;
    Long failed;
    Long skipped;
}