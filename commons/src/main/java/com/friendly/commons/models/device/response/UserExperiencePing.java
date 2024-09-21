package com.friendly.commons.models.device.response;

import com.friendly.commons.models.device.DiagIpPing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserExperiencePing {
     List<DiagIpPing> items;
}
