package com.friendly.commons.models.settings.acs.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class HardcodedEventItem implements Serializable {
    Integer id;
    Boolean enabled;
    String name;
    String description;
    List<Integer> urlIds;
}