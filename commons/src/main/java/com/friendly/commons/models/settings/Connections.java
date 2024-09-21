package com.friendly.commons.models.settings;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Model that represents persistence version of ACS and DB Settings
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Connections implements Serializable {
    private Connection acsConnection;
    private DatabaseType databaseType;
    private Map<DatabaseType, Set<Connection>> dbConnections;
    private Connection qoeConnection;

}
