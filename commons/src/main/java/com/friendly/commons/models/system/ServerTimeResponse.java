package com.friendly.commons.models.system;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Data
@FieldDefaults(level = PRIVATE)
@AllArgsConstructor
public class ServerTimeResponse {
    LocalDateTime localDateTime;
    Instant utcDateTime;
}
