package com.friendly.services.uiservices.system;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.response.AppPortsResponse;
import com.friendly.commons.models.system.ServerTimeResponse;
import com.friendly.commons.models.system.TimeZone;
import com.friendly.commons.models.system.response.DateFormatsResponse;
import com.friendly.commons.models.system.response.LocalesResponse;
import com.friendly.commons.models.system.response.TimeFormatsResponse;
import com.friendly.commons.models.system.response.TimeZonesResponse;
import com.friendly.commons.models.system.response.VersionResponse;
import com.friendly.commons.models.user.Locale;
import com.friendly.commons.models.user.Session;
import com.friendly.services.ServicesApplication;
import com.friendly.services.uiservices.auth.JwtService;
import com.friendly.services.infrastructure.base.XmlConverter;
import com.friendly.services.uiservices.customization.Customization;
import com.friendly.services.uiservices.system.orm.iotw.repository.LocaleRepository;
import com.friendly.services.infrastructure.config.provider.AcsProvider;
import com.friendly.services.uiservices.system.model.VersionXmlModel;
import com.friendly.services.infrastructure.utils.EntityDTOMapper;
import com.ftacs.ACSWebService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.VERSION_FILE_NOT_FOUND;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.VERSION_READ_ERROR;

/**
 * Service that exposes the base functionality for service
 *
 * @author Friendly Tech
 * @since 0.0.2
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SystemService {

    @NonNull
    private final LocaleRepository localeRepository;

    @NonNull
    private final JwtService jwtService;

    @NonNull
    public final AcsProvider acsProvider;

    @NonNull
    private final XmlConverter<VersionXmlModel> versionXmlConverter;

    private final Set<String> dateFormats = new LinkedHashSet<>();
    private final Set<String> timeFormats = new LinkedHashSet<>();
    private final Set<TimeZone> zoneIds = new LinkedHashSet<>();

    @PostConstruct
    public void init() {
        dateFormats.addAll(Arrays.asList("Default", "d.M.yy", "d/M/yy", "dd.MM.yy", "dd.MM.yyyy", "dd/MM/yy",
                                         "dd/MM/yyyy", "dd-MM-yyyy", "M/d/yy", "M/d/yyyy", "MM.dd.yyyy", "MM/dd/yy",
                                         "MM/dd/yyyy", "MM-dd-yyyy", "yy/MM/dd", "yyyy-MM-dd"));
        timeFormats.addAll(Arrays.asList("Default", "H:mm", "h:mm a", "H:mm:ss", "h:mm:ss a", "HH:mm", "hh:mm a",
                                         "HH:mm:ss", "hh:mm:ss a"));

        final LocalDateTime now = LocalDateTime.now();
        zoneIds.add(TimeZone.builder()
                            .id("Default")
                            .value("Default")
                            .build());
        zoneIds.add(TimeZone.builder()
                            .id(ZoneOffset.UTC.getId())
                            .value("UTC")
                            .build());
        zoneIds.addAll(ZoneId.getAvailableZoneIds()
                             .stream()
                             .map(ZoneId::of)
                             .filter(zoneId -> !zoneId.getId().startsWith("Etc/"))
                             .sorted(zoneComparator(now))
                             .map(id -> TimeZone.builder()
                                                .id(id.getId())
                                                .value(String.format("%s%s %s", "GMT", getOffset(now, id), id.getId()))
                                                .build())
                             .collect(Collectors.toList()));
    }

    public void restart(final String token) {
        jwtService.getUserIdByHeaderAuth(token);

        ServicesApplication.restart();
    }

    public LocalesResponse getLocales(final String token) {
        jwtService.getUserIdByHeaderAuth(token);
        List<Locale> locales = EntityDTOMapper.entitiesToDtos(localeRepository.findAll(), Locale.class);

        return new LocalesResponse(locales);
    }

    public DateFormatsResponse getDateFormats(final String token) {
        jwtService.getUserIdByHeaderAuth(token);

        return new DateFormatsResponse(dateFormats);
    }

    public TimeFormatsResponse getTimeFormats(final String token) {
        jwtService.getUserIdByHeaderAuth(token);

        return new TimeFormatsResponse(timeFormats);
    }

    public TimeZonesResponse getTimeZones(final String token) {
        jwtService.getUserIdByHeaderAuth(token);

        return new TimeZonesResponse(zoneIds);
    }

    private static Comparator<ZoneId> zoneComparator(final LocalDateTime now) {
        return (zoneId1, zoneId2) -> {
            final ZonedDateTime zone1 = now.atZone(zoneId1);
            final ZonedDateTime zone2 = now.atZone(zoneId2);
            final ZoneOffset offset1 = zone1.getOffset();
            final ZoneOffset offset2 = zone2.getOffset();

            final int result = offset2.compareTo(offset1);
            return result != 0 ? result : zone1.compareTo(zone2);
        };
    }

    private static String getOffset(final LocalDateTime dateTime, final ZoneId id) {
        return dateTime.atZone(id)
                       .getOffset()
                       .getId()
                       .replace("Z", "+00:00");
    }

    public ServerTimeResponse getServerTime(final String token){
        final Session session = jwtService.getSession(token);
        final ClientType clientType = session.getClientType();
        ACSWebService acsWebService = AcsProvider.getAcsWebService(clientType);
        GregorianCalendar gregorianCalendar = acsWebService.getServerDate().getDate().toGregorianCalendar();
        Instant utcDateTime = gregorianCalendar.toZonedDateTime().toInstant();
        LocalDateTime localDateTime = gregorianCalendar.toZonedDateTime().toLocalDateTime();

        return new ServerTimeResponse(localDateTime, utcDateTime);
    }

    public VersionResponse version() {
        File file = new File("version.xml");
        if (!file.exists()) {
            return VersionResponse.builder().build();
        }
        try {
            VersionXmlModel model = versionXmlConverter.convertToObject(file);
            return VersionResponse.builder()
                    .version(model.getVersion())
                    .build(model.getBuild().getId())
                    .gitRevision(model.getGit().getRevision())
                    .gitBranch(model.getGit().getBranch())
                    .started(model.getBuild().getDate())
                    .build();
        } catch (FileNotFoundException e) {
            return VersionResponse.builder().build();
        }
    }

    public Map<String, String> getAppVersion() {
        Map<String, String> response = new HashMap<>();
        try (InputStream inputStream = getVersionFileStream()) {
            if (inputStream == null) {
                throw new FriendlyIllegalArgumentException(VERSION_FILE_NOT_FOUND);
            }

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(inputStream);
            doc.getDocumentElement().normalize();

            String version = getAttributeValue(doc, "version", "id");
            String buildNumber = getAttributeValue(doc, "build", "id");

            response.put("appVersion", version + " Build " + buildNumber);
        } catch (Exception e) {
            throw new FriendlyIllegalArgumentException(VERSION_READ_ERROR, e);
        }
        return response;
    }

    private String getAttributeValue(Document doc, String elementName, String attributeName) {
        Node node = doc.getElementsByTagName(elementName).item(0);
        if (node != null) {
            NamedNodeMap attributes = node.getAttributes();
            Node attr = attributes.getNamedItem(attributeName);
            if (attr != null) {
                return attr.getNodeValue();
            }
        }
        return "unknown";
    }

    private InputStream getVersionFileStream() throws IOException {
        String pathname = "version.xml";
        File versionFile = new File(pathname);
        if (versionFile.exists()) {
            return Files.newInputStream(versionFile.toPath());
        } else {
            return getClass().getClassLoader().getResourceAsStream(pathname);
        }
    }

    public AppPortsResponse getAppPorts(final String token) {
        Session session = jwtService.getSession(token);

        return new AppPortsResponse(Customization.getAppPortsForClient(session.getClientType()));
    }
}
