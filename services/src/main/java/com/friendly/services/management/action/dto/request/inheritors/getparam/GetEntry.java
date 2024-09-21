package com.friendly.services.management.action.dto.request.inheritors.getparam;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetEntry implements Serializable {
    private String fullName;
    private boolean names;
    private boolean values;
    private boolean attributes;
}
