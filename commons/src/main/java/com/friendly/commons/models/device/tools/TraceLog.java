package com.friendly.commons.models.device.tools;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TraceLog implements Serializable {

    private Integer id;
    private String name;
    private String message;

    @JsonIgnore
    private Instant created;
    @JsonIgnore
    private Integer previousId;

}
