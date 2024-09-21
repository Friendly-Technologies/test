package com.friendly.services.settings.connections;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.UserActivityLog;
import com.friendly.commons.models.settings.Connection;
import com.friendly.commons.models.settings.Connections;
import com.friendly.commons.models.settings.DatabaseType;
import com.friendly.commons.models.user.Session;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.infrastructure.config.jpa.DbConfig;
import com.friendly.services.uiservices.statistic.StatisticService;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import static com.friendly.commons.models.reports.UserActivityType.CONFIGURING_DATABASE;
import static com.friendly.commons.models.settings.DatabaseType.*;
import static com.friendly.commons.models.websocket.ActionType.UPDATE;
import static com.friendly.commons.models.websocket.SettingType.CONNECTIONS;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.*;

/**
 * Service that exposes the base functionality for interacting with {@link Connections} data
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConnectionsService {

    @NonNull
    private final JwtService jwtService;

    @NonNull
    private final WsSender wsSender;

    @Value("${server.path}")
    private String propertiesPath;

    @Value("${mysql.iotw.host}")
    private String datasourceMySqlIotwHost;

    @Value("${mysql.iotw.port}")
    private String datasourceMySqlIotwPort;

    @Value("${mysql.iotw.database}")
    private String datasourceMySqlIotwDatabase;

    @Value("${mysql.iotw.username}")
    private String datasourceMySqlIotwUsername;

    @Value("${mysql.iotw.password}")
    private String datasourceMySqlIotwPassword;

    @Value("${oracle.iotw.host}")
    private String datasourceOracleIotwHost;

    @Value("${oracle.iotw.port}")
    private String datasourceOracleIotwPort;

    @Value("${oracle.iotw.database}")
    private String datasourceOracleIotwDatabase;

    @Value("${oracle.iotw.username}")
    private String datasourceOracleIotwUsername;

    @Value("${oracle.iotw.password}")
    private String datasourceOracleIotwPassword;

    @Value("${mysql.acs.host}")
    private String datasourceMySqlAcsHost;

    @Value("${mysql.acs.port}")
    private String datasourceMySqlAcsPort;

    @Value("${mysql.acs.database}")
    private String datasourceMySqlAcsDatabase;

    @Value("${mysql.acs.username}")
    private String datasourceMySqlAcsUsername;

    @Value("${mysql.acs.password}")
    private String datasourceMySqlAcsPassword;

    @Value("${oracle.acs.host}")
    private String datasourceOracleAcsHost;

    @Value("${oracle.acs.port}")
    private String datasourceOracleAcsPort;

    @Value("${oracle.acs.database}")
    private String datasourceOracleAcsDatabase;

    @Value("${oracle.acs.username}")
    private String datasourceOracleAcsUsername;

    @Value("${oracle.acs.password}")
    private String datasourceOracleAcsPassword;
    

    @Value("${clickhouse.qoe.host:#{null}}")
    private String datasourceQoeHost;

    @Value("${clickhouse.qoe.port:#{null}}")
    private String datasourceQoePort;

    @Value("${clickhouse.qoe.database:#{null}}")
    private String datasourceQoeDatabase;

    @Value("${clickhouse.qoe.username:#{null}}")
    private String datasourceQoeUsername;

    @Value("${clickhouse.qoe.password:#{null}}")
    private String datasourceQoePassword;

    @Value("${acs.host}")
    private String soapAcsHost;

    @Value("${acs.port}")
    private String soapAcsPort;

    @Value("${acs.username}")
    private String soapAcsUsername;

    @Value("${acs.password}")
    private String soapAcsPassword;

    private Connections connections;

    @Autowired
    private ConfigurableApplicationContext congAppContext;

    @Autowired
    private ApplicationContext appContext;

    @NonNull
    private final StatisticService statisticService;

    @PostConstruct
    public void init() {
        final Connection mySqlIotwDatabaseSetting = Connection.builder()
                .serviceName("iotw")
                .title("IOTW database setting")
                .host(datasourceMySqlIotwHost)
                .port(datasourceMySqlIotwPort)
                .database(datasourceMySqlIotwDatabase)
                .username(datasourceMySqlIotwUsername)
                .password(datasourceMySqlIotwPassword)
                .build();
        final Connection mySqlAcsDatabaseSetting = Connection.builder()
                .serviceName("acs")
                .title("ACS database setting")
                .host(datasourceMySqlAcsHost)
                .port(datasourceMySqlAcsPort)
                .database(datasourceMySqlAcsDatabase)
                .username(datasourceMySqlAcsUsername)
                .password(datasourceMySqlAcsPassword)
                .build();
        final Connection oracleIotwDatabaseSetting = Connection.builder()
                .serviceName("iotw")
                .title("IOTW database setting")
                .host(datasourceOracleIotwHost)
                .port(datasourceOracleIotwPort)
                .database(datasourceOracleIotwDatabase)
                .username(datasourceOracleIotwUsername)
                .password(datasourceOracleIotwPassword)
                .build();
        final Connection oracleAcsDatabaseSetting = Connection.builder()
                .serviceName("acs")
                .title("ACS database setting")
                .host(datasourceOracleAcsHost)
                .port(datasourceOracleAcsPort)
                .database(datasourceOracleAcsDatabase)
                .username(datasourceOracleAcsUsername)
                .password(datasourceOracleAcsPassword)
                .build();
        final Connection qoeDatabaseSetting = StringUtils.isEmpty(datasourceQoeHost) ? null :
                Connection.builder()
                .serviceName("qoe")
                .title("QoE database setting")
                .host(datasourceQoeHost)
                .port(datasourceQoePort)
                .database(datasourceQoeDatabase)
                .username(datasourceQoeUsername)
                .password(datasourceQoePassword)
                .build();

        final Map<DatabaseType, Set<Connection>> resourceDetailsMap = new TreeMap<>();
        resourceDetailsMap.put(MySQL, new LinkedHashSet<>(Arrays.asList(mySqlIotwDatabaseSetting,
                mySqlAcsDatabaseSetting)));
        resourceDetailsMap.put(Oracle, new LinkedHashSet<>(Arrays.asList(oracleIotwDatabaseSetting,
                oracleAcsDatabaseSetting)));


        final Connection acsServiceSetting = Connection.builder()
                .title("ACS service setting")
                .host(soapAcsHost)
                .port(soapAcsPort)
                .username(soapAcsUsername)
                .password(soapAcsPassword)
                .build();
        connections = Connections.builder()
                .acsConnection(acsServiceSetting)
                .databaseType(DbConfig.getDbType())
                .dbConnections(resourceDetailsMap)
                .qoeConnection(qoeDatabaseSetting)
                .build();


    }

    /**
     * Get ACS and DB Setting
     *
     * @return {@link Connections} setting
     */
    public Connections getConnections() {
        return connections;
    }

    /**
     * Update ACS and DB Setting
     *
     * @return {@link Connections} setting
     */
    public void updateConnections(final String token, final Connections connections) {
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();

        validateDbConnections(connections);
        validateAcsWebService(connections.getAcsConnection());

        if (connections.getQoeConnection() != null) {
            setQoeConnection(connections.getQoeConnection(), clientType, session.getUserId());
        }
        setDbConnections(connections, clientType, session.getUserId());
        setDbType(connections.getDatabaseType(), clientType, session.getUserId());
        setAcsUrl(connections.getAcsConnection(), clientType, session.getUserId());

        init();
        wsSender.sendSettingEvent(clientType, UPDATE, CONNECTIONS, connections);
        this.connections = connections;
    }

    private void validateAcsWebService(final Connection connection) {
        try (final CloseableHttpClient client = HttpClientBuilder.create().build()) {
            final HttpGet httpGet = new HttpGet("http://" + connection.getHost() + ":" + connection.getPort() +
                    "/ACSServer-ACS/ACSWebService?wsdl");
            final String encoding = DatatypeConverter.printBase64Binary(
                    (connection.getUsername() + ":" + connection.getPassword()).getBytes(StandardCharsets.UTF_8));
            httpGet.setHeader("Authorization", "Basic " + encoding);
            final HttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 401) {
                throw new FriendlyIllegalArgumentException(INCORRECT_LOGIN_OR_PASSWORD, connection.getConnectionName());
            }
        } catch (IOException e) {
            throw new FriendlyIllegalArgumentException(UNABLE_TO_MAKE_CONNECTION, connection.getConnectionName());
        }
    }

    private void setDbType(final DatabaseType databaseType, final ClientType clientType, final Long userId) {
        try {
            final PropertiesConfiguration properties =
                    new PropertiesConfiguration(propertiesPath + "db.properties");
            final String propertyKey = "datasource.type";
            if (ObjectUtils.notEqual(databaseType, DbConfig.getDbType())) {
                properties.setProperty(propertyKey, databaseType);
                if (databaseType.equals(Oracle)) {
                    properties.setProperty("spring.datasource.iotw.jdbcUrl",
                            "${oracle.iotw.url}");
                    properties.setProperty("spring.datasource.iotw.username",
                            "${oracle.iotw.username}");
                    properties.setProperty("spring.datasource.iotw.password",
                            "${oracle.iotw.password}");
                    properties.setProperty("spring.datasource.iotw.driver-class-name",
                            "${oracle.driver}");
                    properties.setProperty("spring.datasource.acs.jdbcUrl",
                            "${oracle.acs.url}");
                    properties.setProperty("spring.datasource.acs.username",
                            "${oracle.acs.username}");
                    properties.setProperty("spring.datasource.acs.password",
                            "${oracle.acs.password}");
                    properties.setProperty("spring.datasource.acs.driver-class-name",
                            "${oracle.driver}");
                } else {
                    properties.setProperty("spring.datasource.iotw.jdbcUrl",
                            "${mysql.iotw.url}");
                    properties.setProperty("spring.datasource.iotw.username",
                            "${mysql.iotw.username}");
                    properties.setProperty("spring.datasource.iotw.password",
                            "${mysql.iotw.password}");
                    properties.setProperty("spring.datasource.iotw.driver-class-name",
                            "${mysql.driver}");
                    properties.setProperty("spring.datasource.acs.jdbcUrl",
                            "${mysql.acs.url}");
                    properties.setProperty("spring.datasource.acs.username",
                            "${mysql.acs.username}");
                    properties.setProperty("spring.datasource.acs.password",
                            "${mysql.acs.password}");
                    properties.setProperty("spring.datasource.acs.driver-class-name",
                            "${mysql.driver}");
                }
                properties.save();
                statisticService.addUserLogAct(UserActivityLog.builder()
                        .userId(userId)
                        .clientType(clientType)
                        .activityType(CONFIGURING_DATABASE)
                        .note("Set Db type=" + databaseType.name())
                        .build());
            }
        } catch (ConfigurationException e) {
            throw new FriendlyIllegalArgumentException(WRONG_CONFIGURATION);
        }
    }

    private void setAcsUrl(final Connection connection, final ClientType clientType, final Long userId) {
        try {
            final PropertiesConfiguration properties =
                    new PropertiesConfiguration(propertiesPath + "db.properties");
            final String hostKey = "acs.host";
            final String portKey = "acs.port";
            final String usernameKey = "acs.username";
            final String passwordKey = "acs.password";
            StringBuilder note = new StringBuilder().append("Set WSDL");
            String host = getProperty(properties, hostKey);
            String port = getProperty(properties, portKey);
            String username = getProperty(properties, usernameKey);
            String password = getProperty(properties, passwordKey);

            setProperty(properties, hostKey, host, note, connection.getHost(), " Host=");
            setProperty(properties, portKey, port, note, connection.getPort(), " Port=");
            setProperty(properties, usernameKey, username, note, connection.getUsername(), " Username=");
            setProperty(properties, passwordKey, password, note, connection.getPassword(), " Password=");
            properties.save();
            statisticService.addUserLogAct(UserActivityLog.builder()
                    .userId(userId)
                    .clientType(clientType)
                    .activityType(CONFIGURING_DATABASE)
                    .note(note.toString())
                    .build());
        } catch (ConfigurationException e) {
            throw new FriendlyIllegalArgumentException(WRONG_CONFIGURATION);
        }
    }

    private void validateDbConnections(final Connections connections) {
        final DatabaseType databaseType = connections.getDatabaseType();
        connections.getDbConnections()
                .get(databaseType)
                .forEach(c -> validateDbConnection(c, databaseType));
    }

    private boolean validateQoeConnection(Connection qoeConnection) {
        final String url = "jdbc:" + Clickhouse.name().toLowerCase() + "://" +
                qoeConnection.getHost() + ":" + qoeConnection.getPort() + "/" + qoeConnection.getDatabase() + "?socket_timeout=120000&use_client_time_zone=1";
        return tryConnectCH(Clickhouse, url, qoeConnection);
    }

    private void validateDbConnection(Connection c, DatabaseType databaseType) {
        final String url;
        switch (databaseType) {
            case MySQL:
                url = "jdbc:" + databaseType.name().toLowerCase() + "://" +
                        c.getHost() + ":" + c.getPort() + "/" + c.getDatabase() + "?serverTimezone=UTC";
                tryConnectMySql(databaseType, url, c);
                break;
            case Oracle:
                url = "jdbc:" + databaseType.name().toLowerCase() + ":thin:@" +
                        c.getHost() + ":" + c.getPort() + ":" + c.getDatabase();
                tryConnectOracle(databaseType, url, c);
                break;
            default:
                throw new FriendlyIllegalArgumentException(UNKNOWN_DB_TYPE, c.getConnectionName());
        }
    }

    private void connectToDatasource(DatabaseType databaseType, String url, Connection c) throws ClassNotFoundException, SQLException {
        Class.forName(DbConfig.getDriverMap(databaseType));
        java.sql.Connection connection =
                DriverManager.getConnection(url, c.getUsername(), c.getPassword());
        connection.close();
    }

    private void tryConnectMySql(final DatabaseType databaseType, final String url, final Connection c) {
        try {
            connectToDatasource(databaseType, url, c);
        } catch (ClassNotFoundException | SQLException e) {
            if (e instanceof SQLException) {
                final int errorCode = ((SQLException) e).getErrorCode();
                switch (errorCode) {
                    case 1049:
                        throw new FriendlyIllegalArgumentException(DATABASE_NOT_FOUND, c.getDatabase());
                    case 1045:
                        throw new FriendlyIllegalArgumentException(INCORRECT_LOGIN_OR_PASSWORD, c.getConnectionName());
                    default:
                        throw new FriendlyIllegalArgumentException(UNABLE_TO_MAKE_CONNECTION, c.getConnectionName());
                }
            }
        }
    }

    private void tryConnectOracle(final DatabaseType databaseType, final String url, final Connection c) {
        try {
            connectToDatasource(databaseType, url, c);
        } catch (ClassNotFoundException | SQLException e) {
            if (e instanceof SQLException) {
                final int errorCode = ((SQLException) e).getErrorCode();
                switch (errorCode) {
                    case 01017:
                        throw new FriendlyIllegalArgumentException(INCORRECT_LOGIN_OR_PASSWORD, c.getConnectionName());
                    case 12505:
                        throw new FriendlyIllegalArgumentException(DATABASE_NOT_FOUND, c.getDatabase());
                    default:
                        throw new FriendlyIllegalArgumentException(UNABLE_TO_MAKE_CONNECTION, c.getConnectionName());
                }
            }
        }

    }

    private boolean tryConnectCH(final DatabaseType databaseType, final String url, final Connection c) {
        try {
            connectToDatasource(databaseType, url, c);
        } catch (ClassNotFoundException | SQLException e) {
            if (e instanceof SQLException) {
                final int errorCode = ((SQLException) e).getErrorCode();
                switch (errorCode) {
                    case 10:
                        log.error("Clickhouse " + DATABASE_NOT_FOUND.getFormattedMessage(c.getDatabase()));
                        return false;
                    case 516:
                        log.error("Clickhouse " + INCORRECT_LOGIN_OR_PASSWORD.getFormattedMessage(c.getConnectionName()));
                        return false;
                    default:
                        log.error("Clickhouse " + UNABLE_TO_MAKE_CONNECTION.getFormattedMessage(c.getConnectionName()));
                        return false;
                }
            }
        }
        return true;
    }

    private void setQoeConnection(final Connection qoeConnection, final ClientType clientType, final Long userId) {
        if (!StringUtils.isEmpty(qoeConnection.getHost())) {
            if (validateQoeConnection(qoeConnection)) {
                setDbConnection(qoeConnection, clientType, Clickhouse, userId);
                try {
                    final PropertiesConfiguration properties =
                            new PropertiesConfiguration(propertiesPath + "db.properties");
                    properties.setProperty("spring.datasource.qoe.jdbcUrl",
                            "${clickhouse.qoe.url}");
                    properties.setProperty("spring.datasource.qoe.username",
                            "${clickhouse.qoe.username}");
                    properties.setProperty("spring.datasource.qoe.password",
                            "${clickhouse.qoe.password}");
                    properties.setProperty("spring.datasource.qoe.driver-class-name",
                            "${clickhouse.driver}");
                } catch (ConfigurationException e) {
                    throw new FriendlyIllegalArgumentException(WRONG_CONFIGURATION);
                }
            }
        } else {
            log.warn("qoeConnection credentials has empty field 'host'");
        }
    }

    private void setDbConnections(final Connections connections, final ClientType clientType, final Long userId) {
        final DatabaseType databaseType = connections.getDatabaseType();
        connections.getDbConnections()
                .get(databaseType)
                .forEach(c -> setDbConnection(c, clientType, databaseType, userId));
    }

    private void setDbConnection(final Connection connection, final ClientType clientType,
                                 final DatabaseType databaseType, final Long userId) {
        try {
            final PropertiesConfiguration properties =
                    new PropertiesConfiguration(propertiesPath + "db.properties");
            final String hostKey = databaseType.name().toLowerCase() + "." + connection.getServiceName() + ".host";
            final String portKey = databaseType.name().toLowerCase() + "." + connection.getServiceName() + ".port";
            final String dbKey = databaseType.name().toLowerCase() + "." + connection.getServiceName() + ".database";
            final String usernameKey = databaseType.name().toLowerCase() + "." + connection.getServiceName() + ".username";
            final String passwordKey = databaseType.name().toLowerCase() + "." + connection.getServiceName() + ".password";

            String host = getProperty(properties, hostKey);
            String port = getProperty(properties, portKey);
            String database = getProperty(properties, dbKey);
            String username = getProperty(properties, usernameKey);
            String password = getProperty(properties, passwordKey);

            StringBuilder note = new StringBuilder().append("Set DB");
            setProperty(properties, hostKey, host, note, connection.getHost(), " Host=");
            setProperty(properties, portKey, port, note, connection.getPort(), " Port=");
            setProperty(properties, dbKey, database, note, connection.getDatabase(), " Database=");
            setProperty(properties, usernameKey, username, note, connection.getUsername(), " Username=");
            setProperty(properties, passwordKey, password, note, connection.getPassword(), " Password=");

            properties.save();
            statisticService.addUserLogAct(UserActivityLog.builder()
                    .userId(userId)
                    .clientType(clientType)
                    .activityType(CONFIGURING_DATABASE)
                    .note(note.toString())
                    .build());
        } catch (ConfigurationException e) {
            log.error(WRONG_CONFIGURATION.getErrorMessage(), e);
            throw new FriendlyIllegalArgumentException(WRONG_CONFIGURATION);
        }
    }

    private String getProperty(final PropertiesConfiguration properties, final String key) {
        final Object property = properties.getProperty(key);
        String value = null;
        if (property != null) {
            value = (String) property;
        }
        return value;
    }

    private void setProperty(final PropertiesConfiguration properties, final String key, final String oldValue,
                             final StringBuilder note, final String newValue, final String fieldName) {
        if (ObjectUtils.notEqual(newValue, oldValue)) {
            properties.setProperty(key, newValue);
            note.append(fieldName).append(newValue).append(";");
        }
    }
}
