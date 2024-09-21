package com.friendly.services.infrastructure.config;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.settings.acs.AcsProperties;
import com.friendly.services.infrastructure.base.ACSWebServiceMock;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.ftacs.ACSWebService;
import com.ftacs.ACSWebServiceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Slf4j
@Configuration
public class AcsConfig {

    @Value("${acs.host}")
    private String acsHost;

    @Value("${acs.port}")
    private String acsPort;

    @Value("${acs.username}")
    private String acsLogin;

    @Value("${acs.password}")
    private String acsPassword;

    @Value("${server.path}")
    private String wsdlPath;

    private static AcsProperties PROPERTIES;

    public AcsConfig() {
        PROPERTIES = AcsProperties.builder()
                .host(acsHost)
                .port(acsPort)
                .login(acsLogin)
                .password(acsPassword)
                .build();
    }

    public static AcsProperties getAcsProperties() {
        return PROPERTIES;
    }

    @Bean
    public ACSWebService acsWebService() {
        return getAcsWebService(acsLogin, acsPassword, acsHost, acsPort);
    }

    private ACSWebService getAcsWebService(final String login, final String password,
                                           final String host, final String port) {
        try (final CloseableHttpClient client = HttpClientBuilder.create().build()) {
            final HttpGet httpGet = new HttpGet("http://" + host + ":" + port +
                                                        "/ACSServer-ACS/ACSWebService?wsdl");
            final String encoding = DatatypeConverter.printBase64Binary(
                    (login + ":" + password).getBytes(StandardCharsets.UTF_8));
            httpGet.setHeader("Authorization", "Basic " + encoding);
            final HttpResponse response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 401) {
                log.error("Unable to make connection with ACS service");
            }
            final String wsdl = wsdlPath + "ACSWebService.wsdl";
            final File file = new File(wsdl);
            file.getParentFile().mkdirs();
            try (final InputStream inputStream = response.getEntity().getContent()) {
                FileUtils.copyInputStreamToFile(inputStream, file);
            }

            final ACSWebService service = new ACSWebServiceService(file.toURI().toURL()).getACSWebServicePort();
            BindingProvider prov = (BindingProvider) service;
            prov.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, login);
            prov.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);

            saveServerZoneOffset(service);

            return service;

        } catch (IOException e) {
            log.error("Unable to make connection with ACS service");
            DateTimeUtils.setTimeZone(ClientType.mc, ZoneOffset.UTC);
            DateTimeUtils.setTimeZone(ClientType.sc, ZoneOffset.UTC);
            return new ACSWebServiceMock();
        }
    }

    private static void saveServerZoneOffset(ACSWebService service) {
        XMLGregorianCalendar serverDate = service.getServerDate().getDate();
        OffsetDateTime offsetDateTime = serverDate.toGregorianCalendar().toZonedDateTime().toOffsetDateTime();
        ZoneOffset zoneOffset = offsetDateTime.getOffset();
        DateTimeUtils.setTimeZone(ClientType.mc, zoneOffset);
        DateTimeUtils.setTimeZone(ClientType.sc, zoneOffset);
    }

}
