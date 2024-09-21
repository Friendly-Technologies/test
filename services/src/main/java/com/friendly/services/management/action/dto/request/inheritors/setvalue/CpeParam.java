package com.friendly.services.management.action.dto.request.inheritors.setvalue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CpeParam implements Serializable {
    private String fullName;
    private String value;
}
