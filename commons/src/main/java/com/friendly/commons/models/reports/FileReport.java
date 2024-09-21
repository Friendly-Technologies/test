package com.friendly.commons.models.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 * Model that represents persistence version of ACS Address
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FileReport implements Serializable {

    private String domain;
    private String name;
    private String type;
    private String created;
    private Instant createdIso;
    private String size;
    private String link;
    private String updated;
    private Instant updatedIso;
    private String creator;

}
