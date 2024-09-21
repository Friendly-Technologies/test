package com.friendly.services.management.action.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class CpeMethodResponse extends AbstractActionMethodDetailsParameters {
    private String value;
    private String name;
    @JsonIgnore // should be added later, logic for instance ready but need check
    private Integer instance;

}
