package com.friendly.services.device.info.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FirmwareServerDetails {
    String url;
    String password;
    String username;
}
