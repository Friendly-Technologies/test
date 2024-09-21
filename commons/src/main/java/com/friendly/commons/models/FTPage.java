package com.friendly.commons.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;

/**
 * Model that defines a Page
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FTPage<T> implements Serializable {

    private Collection<T> items;
    private FTPageDetails pageDetails;

}
