package com.friendly.commons.models.auth.response;

import com.friendly.commons.models.auth.LogoutReasonType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import static lombok.AccessLevel.PRIVATE;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class LogoutReasonTypeResponse {
    LogoutReasonType reason;
}
