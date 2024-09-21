package com.friendly.commons.models.settings.config.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateIotPropertyRequest implements Serializable {

    private List<UpdateIotProperty> items;
}
