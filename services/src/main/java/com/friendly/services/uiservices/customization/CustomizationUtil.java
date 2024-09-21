package com.friendly.services.uiservices.customization;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.device.AppPorts;
import com.friendly.commons.models.device.DeviceConfig;
import com.friendly.commons.models.device.DeviceConfigType;
import com.friendly.commons.models.tabs.DeviceTab;
import com.friendly.commons.models.device.rpc.RpcMethod;
import com.friendly.commons.models.device.setting.DeviceSimplifiedParams;
import com.friendly.commons.models.device.tools.ReplaceService;
import com.friendly.services.device.parameterstree.service.ParameterNameService;
import com.friendly.services.device.info.utils.DeviceUtils;
import com.friendly.services.device.info.utils.DeviceViewUtil;
import com.friendly.services.settings.alerts.AlertProvider;
import com.friendly.services.infrastructure.utils.FileUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Component
@RequiredArgsConstructor
public class CustomizationUtil {

    @Value("${customization.path}")
    private String customizationPath;

    @Value("${server.path}")
    private String appPath;

    @NonNull
    private final ObjectMapper mapper;

    @NonNull
    private final ParameterNameService parameterNameService;

    @NonNull
    private final AlertProvider alertProvider;

    @NonNull
    DeviceUtils deviceUtils;

    @PostConstruct
    public void init() {
        try {
            for (ClientType ct : ClientType.values()) {
                Map<String, List<String>> deviceParameterMapping =
                        fillJsonFromFileForMap(customizationPath + ct + "/custom_params.json", ct, Customization::fillCustomParamsForClient);
                deviceUtils.fillParameterMapping(deviceParameterMapping);
                fillFramesParameters(customizationPath + ct + "/frame/", ct);
                fillDefaultMonitoringList(customizationPath + ct + "/deviceMonitoring.json", ct);
                fillRpcMethodsMap(customizationPath + ct + "/RPCmethods.xml", ct);
                TabUtil.fillDeviceTabMap(customizationPath + ct + "/tabs", ct);
                fillJsonFromFileForList(customizationPath + ct + "/appPorts.json", ct,
                        AppPorts.class, Customization::fillAppPortsForClient);
                fillJsonFromFileForList(customizationPath + ct + "/replaceServices.json", ct,
                        ReplaceService.class, Customization::fillReplaceServicesForClient);
                fillJsonFromFileForMap(customizationPath + ct + "/simplifiedView.json", ct,
                        DeviceSimplifiedParams.class, Customization::fillSimplifiedViewMapForClient);
                fillJsonFromFileForList(customizationPath + ct + "/tabs.json", ct,
                        DeviceTab.class, Customization::fillDeviceTabsForClient);
                fillDeviceActivityMap(customizationPath + ct + "/device_activity.json", ct);
                fillDeviceParameterMapping(customizationPath + ct + "/parameterMapping.json");
            }
            TabUtil.fillProfileDefaultTabMap(customizationPath + ClientType.mc + "/profile/default");
            TabUtil.fillProfileAllTabMap(customizationPath + ClientType.mc + "/profile/all");
            DeviceViewUtil.fillCustomParamsMacAddress();
        } catch (JDBCConnectionException | DataAccessResourceFailureException | InvalidDataAccessResourceUsageException e) {
            log.error("Can not connect to ACS DB");
            alertProvider.setDbIsDown();
        }
    }

    private void fillDefaultMonitoringList(String path, ClientType ct) {
        if (Files.exists(Paths.get(path))) {
            try (FileReader reader = new FileReader(path)) {
                final List<String> deviceMonitoring = mapper.readValue(reader, new TypeReference<List<String>>() {
                });
                final List<Long> monitoringIds =
                        deviceMonitoring.stream()
                                .map(parameterNameService::getIdByName)
                                .collect(Collectors.toList());

                Customization.fillDefaultMonitoringListForClient(ct, monitoringIds);
            } catch (IOException e) {
                log.error("deviceMonitoring.json not found");
            }
        }
    }

    private void fillRpcMethodsMap(String path, ClientType ct) {
        if (!Files.exists(Paths.get(path))) {
            return;
        }
        try {
            final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Document document = documentBuilder.parse(path);
            final Node root = document.getDocumentElement();
            final NodeList methods = root.getChildNodes();
            final List<RpcMethod> internetGatewayMethods = new ArrayList<>();
            final List<RpcMethod> deviceMethods = new ArrayList<>();
            final List<RpcMethod> rootMethods = new ArrayList<>();
            final Map<String, List<RpcMethod>> methodsMap = new HashMap<>();
            Set<String> deviceMethodNames = new HashSet<>();
            Map<String, String> igdMethodMap = new HashMap<>();
            for (int i = 0; i < methods.getLength(); i++) {
                final Node method = methods.item(i);

                if (method.getNodeType() == Node.ELEMENT_NODE) {
                    final Node rootnode = method.getAttributes().getNamedItem("rootnode");
                    final String textContent = method.getChildNodes().item(0).getTextContent();
                    final RpcMethod rpcMethod = RpcMethod.builder()
                            .method(method.getNodeName())
                            .request(textContent)
                            .build();

                    if (rootnode != null && rootnode.getNodeValue().equals("Device.")) {
                        deviceMethods.add(rpcMethod);
                        deviceMethodNames.add(rpcMethod.getMethod());
                    } else if (textContent.contains("InternetGatewayDevice.")) {
                        internetGatewayMethods.add(rpcMethod);
                        igdMethodMap.put(rpcMethod.getMethod(), rpcMethod.getRequest());
                    } else {
                        rootMethods.add(rpcMethod);
                    }
                }
            }
            for (String name : igdMethodMap.keySet()) {
                if (!deviceMethodNames.contains(name)) {
                    String request = igdMethodMap.get(name);
                    if (request.contains("InternetGatewayDevice.")) {
                        request = request.replace("InternetGatewayDevice.", "Device.");
                    }
                    RpcMethod rpcMethod = new RpcMethod(name, request);
                    deviceMethods.add(rpcMethod);
                }
            }
            internetGatewayMethods.addAll(rootMethods);
            deviceMethods.addAll(rootMethods);
            methodsMap.put("InternetGatewayDevice.", internetGatewayMethods);
            methodsMap.put("Device.", deviceMethods);
            methodsMap.put("Root.", rootMethods);

            Customization.fillRpcMethodsMapForClient(ct, methodsMap);
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            log.error("RPCmethods.xml not found");
        }
    }


    private void fillDeviceActivityMap(String path, ClientType ct) {
        File file = FileUtils.getFileIfExists(path);
        if (file == null) {
            return;
        }
        try {
            List<LinkedHashMap<String, Object>> listFromJson
                    = new ObjectMapper().readValue(file, new TypeReference<List<LinkedHashMap<String, Object>>>() {
            });
            Map<String, String> deviceActivityMap = listFromJson.stream()
                    .collect(Collectors.toMap(s -> (String) s.get("id"),
                            s -> (String) s.get("text")));
            Customization.fillDeviceActivityForClient(ct, deviceActivityMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillDeviceParameterMapping(String path) {
        File file = FileUtils.getFileIfExists(path);
        if (file == null) {
            return;
        }
        try {
            List<LinkedHashMap<String, Object>> listFromJson
                    = new ObjectMapper().readValue(file, new TypeReference<List<LinkedHashMap<String, Object>>>() {
            });
            Map<String, List<String>> deviceParameterMapping = listFromJson.stream()
                    .collect(Collectors.toMap(s -> (String) s.get("name"),
                            s -> (List<String>) s.get("parameters")));
            deviceUtils.fillParameterMapping(deviceParameterMapping);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private <E> void fillJsonFromFileForList(final String filePath, ClientType ct, Class<E> cl, BiConsumer<ClientType, List<E>> consumer) {
        if (Files.exists(Paths.get(filePath))) {
            JavaType type = mapper.getTypeFactory().
                    constructCollectionType(List.class, cl);
            fillJsonFromFile(filePath, ct, type, consumer);
        }
    }

    private <V> void fillJsonFromFileForMap(final String filePath, ClientType ct, Class<V> clVal, BiConsumer<ClientType, Map<String, List<V>>> consumer) {
        fillJsonFromFileForMap(filePath, ct, String.class, clVal, consumer);
    }

    private Map<String, List<String>> fillJsonFromFileForMap(final String filePath, ClientType ct, BiConsumer<ClientType, Map<String, List<String>>> consumer) {
        return fillJsonFromFileForMap(filePath, ct, String.class, String.class, consumer);
    }

    private <K, V> Map<K, List<V>> fillJsonFromFileForMap(final String filePath, ClientType ct, Class<K> clKey, Class<V> clVal, BiConsumer<ClientType, Map<K, List<V>>> consumer) {
        if (Files.exists(Paths.get(filePath))) {
            JavaType type = mapper.getTypeFactory().
                    constructMapType(Map.class, mapper.getTypeFactory().constructType(clKey),
                            mapper.getTypeFactory().constructCollectionType(List.class, clVal));
            return fillJsonFromFile(filePath, ct, type, consumer);
        }
        return Collections.emptyMap();
    }

    private <E> E fillJsonFromFile(final String filePath, ClientType ct, JavaType type, BiConsumer<ClientType, E> consumer) {
        try (FileReader reader = new FileReader(filePath)) {
            E e = mapper.readValue(reader, type);
            consumer.accept(ct, e);
            return e;
        } catch (IOException e) {
            log.error(filePath + " can not read");
        }
        return null;
    }

    private void fillFramesParameters(String path, ClientType ct) {
        if (Files.exists(Paths.get(path))) {
            try (Stream<Path> paths = Files.walk(Paths.get(path))) {
                paths.filter(Files::isRegularFile)
                        .forEach(path1 -> fillParameters(path1, ct));
            } catch (IOException e) {
                log.error("frame parameters not found");
            }
        }
    }

    private void fillParameters(final Path file, ClientType ct) {
        fillJsonFromFileForMap(file.toString(), ct, DeviceConfigType.class, DeviceConfig.class, Customization::fillDeviceConfigForClient);
    }


}
