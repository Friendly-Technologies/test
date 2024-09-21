package com.friendly.commons.models.reports;

import com.friendly.commons.models.auth.ClientType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityLog implements Serializable {

    private Long userId;
    private ClientType clientType;
    private UserActivityType activityType;
    private String note;

}
