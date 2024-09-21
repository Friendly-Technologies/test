package com.friendly.commons.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Model that defines a Page Details
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FTPageDetails implements Serializable {

    //private List<Integer> currentPage;
    private Integer pageItems;
    //private Integer pageSize;
    private Long totalItems;
    private Integer totalPages;

}
