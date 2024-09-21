package com.friendly.commons.models.device.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserExperienceRebootResetEvents {
    private EventDate reboot;
    private EventDate reset;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class EventDate {
        private Long daily;
        private Long weekly;
        private Long monthly;
    }
}


