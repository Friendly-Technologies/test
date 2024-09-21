package com.friendly.services.device.info.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Timestamp;

@AllArgsConstructor
@Getter
public class FileFtpDto {
    private String fileName;
    private Timestamp fileDate;
}
