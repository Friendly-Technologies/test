package com.friendly.services.uiservices.customization;

import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.tree.*;
import com.friendly.commons.models.tabs.DeviceTab;
import com.friendly.commons.models.device.setting.DeviceObjectSimple;
import com.friendly.commons.models.device.setting.DeviceParameterSimple;
import com.friendly.commons.models.device.setting.TabViewType;
import com.friendly.services.device.parameterstree.utils.ParameterUtil;
import com.friendly.services.infrastructure.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class TabUtil {

    static Comparator<DeviceObjectSimple> comparatorDeviceObjectSimple =
            Comparator.comparing((DeviceObjectSimple o) -> o.getShortName().replaceAll("\\d", ""), String.CASE_INSENSITIVE_ORDER)
                    .thenComparingInt(o -> {
                        String[] parts = o.getShortName().split("\\D+");
                        return parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
                    });




    public static void fillDeviceTabMap(String path, ClientType ct) {
        Map<String, Map<String, TreeTab>> map = new HashMap<>();
        fillTabMap(map, path);
        Customization.fillDeviceTabViewMapForClient(ct, map);
    }

    public static void fillProfileDefaultTabMap(String path) {
        Map<String, Map<String, TreeTab>> map = new HashMap<>();
        fillTabMap(map, path);
        Customization.fillProfileDefaultTabViewMap(map);
    }

    public static void fillProfileAllTabMap(String path) {
        Map<String, Map<String, TreeTab>> map = new HashMap<>();
        fillTabMap(map, path);
        Customization.fillProfileAllTabViewMap(map);
    }



    public static List<DeviceTab> getDeviceTabs(ClientType clientType) {
        return Customization.getDeviceTabsForClient(clientType);
    }

    public static Map<String, TreeTab> getDeviceTabForRoot(ClientType ct, String rootName) {
        return Customization.getDeviceTabViewMapForClient(ct).get(rootName.endsWith(".") ? rootName.substring(0, rootName.length() - 1) : rootName);
    }

    public static Map<String, TreeTab> getProfileAllTabForRoot(String rootName) {
        return Customization.getProfileAllTabViewMap().get(rootName.endsWith(".") ? rootName.substring(0, rootName.length() - 1) : rootName);
    }

    public static List<String> getParameterListForObject(String rootName, String objName, ClientType clientType) {
        Map<String, TreeTab> tabs = getDeviceTabForRoot(clientType, rootName);
        objName = objName.replaceAll("\\.[0-9]+\\.", ".i.");
        Set<String> params = new HashSet<>();
        String finalObjName = objName;
        tabs.values().forEach(tab -> walkWithDeviceChildBranchFill(finalObjName, params, tab.getRoot()));
        return new ArrayList<>(params);
    }

    private static void walkWithDeviceChildBranchFill(String objName, Set<String> params, TreeTabObject treeObject) {
        if (objName.startsWith(treeObject.getFullName())) {
            treeObject.getItems().forEach(obj -> walkWithDeviceChildBranchFill(objName, params, obj));
        } else if (treeObject.getFullName().startsWith(objName)) {
            params.add(treeObject.getFullName());
            params.addAll(treeObject.getParameters().stream().map(TreeTabParameter::getFullName).collect(Collectors.toList()));
            treeObject.getItems().forEach(obj -> walkWithDeviceChildBranchFill(objName, params, obj));
        }
    }

    public static List<String> getDeviceTabObjectsWithParameters(TreeTab treeTab) {
        List<String> list = new ArrayList<>();
        walkToObjectsWithParameters(treeTab.getRoot(), list);
        return list;
    }

    public static List<String> getDeviceTabObjectsUnderRoot(TreeTab treeTab) {
        return treeTab.getRoot().getFullName().equals(treeTab.getRoot().getShortName() + ".") ?
                treeTab.getRoot().getItems().stream().map(AbstractTreeElement::getFullName).collect(Collectors.toList()) :
                Collections.singletonList(treeTab.getRoot().getFullName());
    }

    public static TreeTab getDeviceTabForRootAndPath(String rootName, String tabPath, ClientType clientType) {
        Map<String, TreeTab> map = getDeviceTabForRoot(clientType, rootName);
        return map == null ? null : map.get(tabPath);
    }

    public static List<DeviceObjectSimple> paramsToTabTree(TreeTab treeTab, List<String> deviceParams) {
        Map<String, List<String>> deviceParamsMask = deviceParams
                .stream()
                .collect(Collectors.groupingBy(p -> p.replaceAll("\\.[0-9]+\\.", ".i.")));
        if (treeTab.getRoot().getFullName().equals(treeTab.getRoot().getShortName() + ".")) {
            return treeTab.getRoot().getItems().stream()
                    .map(o -> walkWithDeviceObjectFill(o, null, deviceParamsMask))
                    .flatMap(List::stream)
                    .sorted(Comparator.comparing(DeviceObjectSimple::getShortName, String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());
        } else {
            // root contains mask
            return walkWithDeviceObjectFill(treeTab.getRoot(), null, deviceParamsMask);
        }

    }

    private static List<DeviceObjectSimple> walkWithDeviceObjectFill(TreeTabObject obj, DeviceObjectSimple parentObj, Map<String, List<String>> deviceParams) {
        List<DeviceObjectSimple> list = new ArrayList<>();
        String objName = obj.getFullName();
        objName = objName.replaceAll("\\.[0-9]+\\.", ".i.");
        String parentObjName = parentObj == null ? obj.getFullName().substring(0, obj.getFullName().indexOf(".") + 1) : parentObj.getFullName();
        String parentObjNameMask = parentObjName.replaceAll("\\.[0-9]+\\.", ".i.");

        String delta = objName.substring(parentObjNameMask.length());
        while (StringUtils.countMatches(delta, ".") > 1) {
            String shortName = delta.substring(0, delta.indexOf(".") + 1);
            String name = parentObjName + shortName;
            DeviceObjectSimple absentObj = DeviceObjectSimple.builder()
                    .fullName(name)
                    .shortName(shortName)
                    .build();
            absentObj.setItems(walkWithDeviceObjectFill(obj, absentObj, deviceParams));
            list.add(absentObj);
            return list;
        }
        list.addAll(
                getObjFromDevice(obj.getFullName(), deviceParams)
                        .stream()
                        .filter(fullName -> parentObj == null || fullName.startsWith(parentObj.getFullName()))
                        .map(fullName -> {
                            DeviceObjectSimple deviceObj = new DeviceObjectSimple();
                            deviceObj.setFullName(fullName);
                            deviceObj.setShortName(ParameterUtil.getShortName(deviceObj.getFullName()));
                            deviceObj.setItems(obj.getItems() == null ? Collections.emptyList()
                                    : obj.getItems()
                                    .stream()
                                    .filter(o -> containsObjInDevice(o.getFullName(), deviceParams) || (o.getFullName().endsWith(".i.")
                                            && containsObjInDevice(o.getFullName().substring(0, o.getFullName().lastIndexOf(".i.")) + ".", deviceParams)))
                                    .map(o -> walkWithDeviceObjectFill(o, deviceObj, deviceParams))
                                    .flatMap(List::stream)
                                    .sorted(comparatorDeviceObjectSimple)
                                    .collect(Collectors.toList())
                            );


                            if (obj.getParameters() != null) {
                                deviceObj.setParameters(obj.getParameters()
                                        .stream()
                                        .filter(p -> containsObjInDevice(p.getFullName(), deviceParams))
                                        .map(p -> {
                                            String paramName = getParameterFromDevice(p.getFullName(), deviceObj.getFullName(), deviceParams);
                                            return paramName == null ? null :
                                                    DeviceParameterSimple.builder()
                                                    .shortName(ParameterUtil.getShortName(paramName))
                                                    .fullName(paramName)
                                                    .parentName(deviceObj.getFullName().substring(0, deviceObj.getFullName().length() - 1))
                                                    .possibleValues(p.getParameterValue() instanceof SelectTreeParameterValue ?
                                                            ((SelectTreeParameterValue) p.getParameterValue()).getPossibleValues()
                                                            : Collections.emptyList())
                                                    .valueType(TabViewType.valueOf(p.getParameterValue().getValueType().name()))
                                                    .build();
                                        })
                                        .filter(Objects::nonNull)
                                        .sorted(Comparator.comparing(DeviceParameterSimple::getShortName, String.CASE_INSENSITIVE_ORDER))
                                        .collect(Collectors.toList()));

                            }
                            return deviceObj;
                        })
                        .sorted(comparatorDeviceObjectSimple)
                        .collect(Collectors.toList()));

        return list;
    }

    private static boolean containsObjInDevice(String objName, Map<String, List<String>> deviceParams) {
        if (deviceParams.containsKey(objName)) {
            return true;
        }
        if (!objName.endsWith(".")) {
            return false;
        }
        return deviceParams.keySet().stream().anyMatch(k -> k.startsWith(objName));
    }

    private static String getParameterFromDevice(String paramName, String deviceParentName, Map<String, List<String>> deviceParams) {
        return deviceParams.get(paramName).stream().filter(p -> p.startsWith(deviceParentName)).findFirst().orElse(null);
    }

    private static Set<String> getObjFromDevice(String objName, Map<String, List<String>> deviceParams) {
        if (deviceParams.containsKey(objName)) {
            return new HashSet<>(deviceParams.get(objName));
        }
        if (!objName.endsWith(".")) {
            return Collections.emptySet();
        }

        Map<String, List<String>> objMap = deviceParams.entrySet()
                .stream()
                .filter(e -> e.getKey().startsWith(objName))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Set<String> result = objMap.entrySet().stream().map(objEntry -> {
                    String mask = objEntry.getKey();
                    Set<String> names = new HashSet<>(objEntry.getValue());
                    while (!mask.equals(objName)) {
                        mask = mask.substring(0, mask.length() - 1);
                        mask = mask.substring(0, mask.lastIndexOf(".") + 1);

                        names = names.stream().map(name -> {
                            name = name.substring(0, name.length() - 1);
                            name = name.substring(0, name.lastIndexOf(".") + 1);
                            return name;
                        }).collect(Collectors.toSet());

                    }
                    return names;
                })
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        return result;
    }

    private static void walkToObjectsWithParameters(TreeTabObject treeObject, List<String> list) {
        if ((treeObject.getParameters() == null || treeObject.getParameters().isEmpty()) && (treeObject.getItems() != null && !treeObject.getItems().isEmpty())) {
            treeObject.getItems().forEach(e -> walkToObjectsWithParameters(e, list));
        } else {
            list.add(treeObject.getFullName());
        }
    }

    private static void fillTabMap(Map<String, Map<String, TreeTab>> map, String path) {
        FileUtils.getFilesFromFolder(path)
                .forEach(file -> {
                    Map<String, TreeTab> rootTreeTab = getTreeTabsFromXml(file.getPath(), file.getName());
                    for (String root : rootTreeTab.keySet()) {
                        Map<String, TreeTab> tabPathMap = map.computeIfAbsent(root, k -> new HashMap<>());
                        tabPathMap.put(rootTreeTab.get(root).getName(), rootTreeTab.get(root));
                    }
                });
    }

    private static Map<String, TreeTab> getTreeTabsFromXml(final String filePath, String fileName) {
        Map<String, TreeTab> rootTreeMap = new HashMap<>();
        try {
            final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            final Document document = documentBuilder.parse(filePath);
            final Node root = document.getDocumentElement();
            final NodeList roots = root.getChildNodes();

            for (int i = 0; i < roots.getLength(); i++) {
                try {
                    final Node deviceRoot = roots.item(i);
                    if (deviceRoot.getNodeType() == Node.ELEMENT_NODE) {
                        final String rootPath = deviceRoot.getNodeName();
                        final Node path = deviceRoot.getAttributes().getNamedItem("path");
                        String fullName = path == null ? deviceRoot.getNodeName() : path.getNodeValue();
                        fullName = fullName.endsWith(".") ? fullName : fullName + ".";

                        TreeTab treeTab = TreeTab.builder()
                                .name(fileName.substring(0, fileName.lastIndexOf(".xml")))
                                .root(TreeTabObject.builder()
                                        .shortName(deviceRoot.getNodeName())
                                        .fullName(fullName)
                                        .build())
                                .build();
                        rootTreeMap.put(rootPath, treeTab);
                        walkTree(treeTab.getRoot(), deviceRoot.getChildNodes(), fullName);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            log.error("{} not found", filePath);
        }
        return rootTreeMap;
    }

    private static void walkTree(TreeTabObject parentObject, final NodeList nodeList, final String parentPath) {
        if (parentObject.getParameters() == null) {
            parentObject.setParameters(new ArrayList<>());
        }
        if (parentObject.getItems() == null) {
            parentObject.setItems(new ArrayList<>());
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                final Node path = node.getAttributes().getNamedItem("path");
                final Node type = node.getAttributes().getNamedItem("type");
                if (type != null || !containsChildElements(node)) {
                    // it is parameter node
                    String fullName = path == null ? node.getNodeName() : path.getNodeValue();
                    fullName = parentPath + fullName;

                    TreeParameterValueType parameterValueType = type == null ? TreeParameterValueType.textbox :
                            TreeParameterValueType.valueOf(type.getTextContent());

                    if (fullName.contains("Password") ||
                            fullName.contains(".WEPKey") ||
                            fullName.contains(".PreSharedKey") ||
                            fullName.contains(".KeyPassphrase")) {
                        parameterValueType = TreeParameterValueType.password;
                    }
                    AbstractTreeParameterValue parameterValue = null;
                    switch (parameterValueType) {
                        case textbox:
                            parameterValue = new TextboxTreeParameterValue();
                            parameterValue.setDefaultValue(node.getTextContent() == null ? null : node.getTextContent().trim());
                            break;
                        case password:
                            parameterValue = new PasswordTreeParameterValue();
                            parameterValue.setDefaultValue(node.getTextContent() == null ? null : node.getTextContent().trim());
                            break;
                        case checkbox:
                            parameterValue = new CheckboxTreeParameterValue();
                            final Node checked = node.getAttributes().getNamedItem("checked");
                            if (checked != null && Objects.equals(checked.getTextContent(), "checked")) {
                                parameterValue.setDefaultValue("1");
                            } else {
                                parameterValue.setDefaultValue("0");
                            }
                            break;
                        case select:
                            parameterValue = new SelectTreeParameterValue();
                            fillValues(node, (SelectTreeParameterValue) parameterValue);
                            break;
                        default:
                            break;

                    }

                    parentObject.getParameters().add(TreeTabParameter.builder()
                            .fullName(fullName)
                            .shortName(node.getNodeName())
                            .parameterValue(parameterValue)
                            .build()
                    );
                } else {
                    // it is object node
                    String fullName = path == null ? node.getNodeName() : path.getNodeValue();
                    fullName = parentPath + fullName;
                    fullName = fullName.endsWith(".") ? fullName : fullName + ".";
                    TreeTabObject treeObject = TreeTabObject.builder()
                            .fullName(fullName)
                            .shortName(node.getNodeName())
                            .build();
                    parentObject.getItems().add(treeObject);
                    walkTree(treeObject, node.getChildNodes(), fullName);
                }
            }
        }
    }

    private static boolean containsChildElements(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
    }

    private static void fillValues(Node node, SelectTreeParameterValue parameterValue) {
        List<String> values = new ArrayList<>();
        String defaultValue = null;
        if (node.hasChildNodes()) {
            NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node1 = nodeList.item(i);
                if (node1.getNodeType() == Node.ELEMENT_NODE) {
                    NodeList nodeList2 = node1.getChildNodes();
                    for (int j = 0; j < nodeList2.getLength(); j++) {
                        Node node2 = nodeList2.item(j);
                        if (node2.getNodeType() == Node.ELEMENT_NODE) {
                            final Node selected = node2.getAttributes().getNamedItem("selected");
                            if (selected != null) {
                                defaultValue = node2.getTextContent();
                            }
                            values.add(node2.getTextContent());
                        }
                    }
                }
            }
        }
        parameterValue.setPossibleValues(values);
        parameterValue.setDefaultValue(defaultValue);
    }


}