package com.friendly.commons.models.reports;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

/**
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDownloadReport implements Serializable {

    private Long id;
    private String name;
    private String manufacturer;
    private String model;
    private String domain;
    private String version;
    private String fileType;
    private String url;
    private String created;
    private Instant createdIso;
    private String creator;

    private Integer pendingTasks;
    private Integer completedTasks;
    private Integer failedTasks;
    private Integer rejectedTasks;

}
