package com.friendly.commons.models.auth.request;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Builder
@Data
@FieldDefaults(level = PRIVATE)
public class AuthBody {
    String token;
    String sessionHash;
}
