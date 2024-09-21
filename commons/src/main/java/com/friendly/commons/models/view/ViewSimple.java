package com.friendly.commons.models.view;

import static lombok.AccessLevel.PRIVATE;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * Model that defines a Simple View
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = PRIVATE)
public class ViewSimple implements Serializable {

    Long id;
    String name;
    Boolean isDefault;
    Boolean isDefaultUser;
    Boolean isDefaultPublic;
    Boolean isDevicePriority;

}
