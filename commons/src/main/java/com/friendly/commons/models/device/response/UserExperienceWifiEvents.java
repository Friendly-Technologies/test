package com.friendly.commons.models.device.response;


import com.friendly.commons.models.device.WifiEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserExperienceWifiEvents {
    List<WifiEvent> items;
}
