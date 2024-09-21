package com.friendly.services.infrastructure.config.jpa;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.settings.DatabaseType;
import lombok.RequiredArgsConstructor;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static com.friendly.commons.models.settings.DatabaseType.Clickhouse;
import static com.friendly.commons.models.settings.DatabaseType.MySQL;
import static com.friendly.commons.models.settings.DatabaseType.Oracle;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.WRONG_CONFIGURATION;

@Configuration
@RequiredArgsConstructor
public class DbConfig {

    private static DatabaseType DB_TYPE;


    private final static Map<DatabaseType, String> DRIVER_MAP = new HashMap<>();

    @Value("${server.path}")
    private String propertiesPath;

    public static DatabaseType getDbType() {
        return DB_TYPE;
    }

    public static boolean isOracle() {
        return Oracle.equals(DB_TYPE);
    }

    public static String getDriverMap(final DatabaseType databaseType) {
        return DRIVER_MAP.get(databaseType);
    }

    @PostConstruct
    public void init() {
        try {
            final PropertiesConfiguration properties = new PropertiesConfiguration(
                    propertiesPath + "db.properties");
            DB_TYPE = DatabaseType.valueOf((String) properties.getProperty("datasource.type"));

            DRIVER_MAP.clear();
            DRIVER_MAP.put(MySQL, (String) properties.getProperty( "mysql.driver"));
            DRIVER_MAP.put(Oracle, (String) properties.getProperty("oracle.driver"));
            DRIVER_MAP.put(Clickhouse, (String) properties.getProperty("clickhouse.driver"));
        } catch (ConfigurationException e) {
            throw new FriendlyIllegalArgumentException(WRONG_CONFIGURATION);
        }

    }
}