package com.friendly.services.reports.service;

import com.friendly.commons.exceptions.FriendlyEntityNotFoundException;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.commons.models.auth.ClientType;
import com.friendly.commons.models.reports.FileReport;
import com.friendly.commons.models.reports.ReportType;
import com.friendly.commons.models.reports.XmlFile;
import com.friendly.commons.models.view.ViewColumn;
import com.friendly.services.infrastructure.base.XmlConverter;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import com.friendly.services.infrastructure.utils.websocket.WsSender;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.TypedQuery;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.friendly.commons.models.reports.ReportType.ACTIVE_SESSIONS;
import static com.friendly.commons.models.reports.ReportType.DEVICE_DISTRIBUTION;
import static com.friendly.commons.models.reports.ReportType.DEVICE_EVENT;
import static com.friendly.commons.models.reports.ReportType.DEVICE_HISTORY;
import static com.friendly.commons.models.reports.ReportType.DEVICE_OFFLINE;
import static com.friendly.commons.models.reports.ReportType.DEVICE_ONLINE;
import static com.friendly.commons.models.reports.ReportType.DEVICE_REGISTRATION;
import static com.friendly.commons.models.reports.ReportType.DEVICE_UPDATE;
import static com.friendly.commons.models.reports.ReportType.FIRMWARE_VERSION;
import static com.friendly.commons.models.reports.ReportType.GROUP_UPDATE;
import static com.friendly.commons.models.reports.ReportType.PROFILE_DOWNLOAD;
import static com.friendly.commons.models.reports.ReportType.SESSION_STATISTIC;
import static com.friendly.commons.models.reports.ReportType.STATISTIC_OPERATIONS;
import static com.friendly.commons.models.reports.ReportType.USER_ACTIVITY_DEVICE;
import static com.friendly.commons.models.reports.ReportType.USER_ACTIVITY_SYSTEM;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.CAN_NOT_DELETE_FILE;
import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.REPORT_IS_EMPTY;

@Component
@RequiredArgsConstructor
public class ReportFileService {

    @NonNull
    private final XmlConverter<XmlFile> reportXmlConverter;
    private static final String EMPTY_STRING = "";
    public static final String SLASH = "/";

    private static final int BATCH_SIZE = 100000;
    private static final int MAX_ROWS_PER_SHEET = 1048575;
    private final ExecutorService taskExecutorService = Executors.newSingleThreadExecutor(); // for process queue of reports requests
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(); // requests queue
    private Future<?> currentTask = null;
    private final Object lock = new Object();


    private static String reportsPath;
    @Value("${reports.path}")
    public void setReportsPath(String reportsPath) {
        ReportFileService.reportsPath = reportsPath;
    }

    private static final Logger log = LoggerFactory.getLogger(ReportFileService.class);
    private final WsSender wsSender;

    private static final class ReportAttributes {
        final List<String> fields;
        final String name;
        final String headerTemplate;

        private ReportAttributes(List<String> fields, String name, String headerTemplate) {
            this.fields = fields;
            this.name = name;
            this.headerTemplate = headerTemplate;
        }
    }

    private static final Map<ReportType, ReportAttributes> reportAttributesMap;

    static {
        reportAttributesMap = new EnumMap<>(ReportType.class);
        reportAttributesMap.put(DEVICE_REGISTRATION, new ReportAttributes(
                Arrays.asList("Domain", "Serial number", "Manufacturer", "Model", "Registration date", "Last connection date", "Phone"),
                "Device registration", "Device registration report from %s to %s"));

        reportAttributesMap.put(ACTIVE_SESSIONS, new ReportAttributes(
                Arrays.asList("User name", "Domain", "Logged in at", "Last activity", "Amount of devices"),
                "Active sessions", "Active sessions report for %s"));

        reportAttributesMap.put(SESSION_STATISTIC, new ReportAttributes(
                Arrays.asList("User name", "Domain", "Logged in at", "Duration"),
                "Session statistic", "Sessions statistic report for %s"));

        reportAttributesMap.put(USER_ACTIVITY_DEVICE, new ReportAttributes(
                Arrays.asList("User name", "Manufacturer", "Model", "Serial number", "Domain", "Date", "Activity type", "Note"),
                "User activity: device update", "User activity(Device Update) report from %s to %s"));

        reportAttributesMap.put(USER_ACTIVITY_SYSTEM, new ReportAttributes(
                Arrays.asList("Application", "User name", "Activity type", "Date", "Note"),
                "User activity: system", "User activity (System) report from %s to %s"));

        reportAttributesMap.put(STATISTIC_OPERATIONS, new ReportAttributes(
                Arrays.asList("Domain", "Date/Time", "Operations"),
                "Statistic on operations", "Statistics on operations from %s to %s"));

        reportAttributesMap.put(DEVICE_UPDATE, new ReportAttributes(
                Arrays.asList("Domain", "Created", "Status", "Serial", "Updated",
                        "Firmware", "Protocol Type", "Manufacturer",
                        "Model Name", "OUI", "Login", "Name", "Telephone",
                        "Zip", "Location", "Tag", "User Status", "User ID",
                        "Hardware", "Software", "Ip Address", "Mac Address", "Uptime",
                        "My Cust 1", "My Cust 2", "My Cust 3", "My Cust 4", "My Cust 5", "My Cust 6",
                        "My Cust 7", "My Cust 8", "My Cust 9", "My Cust 10", "Completed Tasks",
                        "Failed Tasks", "Pending Tasks", "Rejected Tasks", "Sent Tasks"),
                "Device", "Device update report from %s to %s"));

        reportAttributesMap.put(DEVICE_HISTORY, new ReportAttributes(
                Arrays.asList("Activity type", "Created", "Parameter name", "Old value", "New value"),
                "Device history", "Device history from %s"));

        reportAttributesMap.put(DEVICE_DISTRIBUTION, new ReportAttributes(
                Arrays.asList("Domain", "Manufacturer", "Model Type", "Quantity", "Percentage"),
                "Device distribution", null));

        reportAttributesMap.put(DEVICE_ONLINE, new ReportAttributes(
                Arrays.asList("Domain", "ID", "Manufacturer", "Model name", "Serial", "Created", "Last session on"),
                "Online devices", null));

        reportAttributesMap.put(DEVICE_OFFLINE, new ReportAttributes(
                Arrays.asList("Domain", "ID", "Manufacturer", "Model name", "Serial", "Created", "Updated"),
                "Offline devices", null));

        reportAttributesMap.put(DEVICE_EVENT, new ReportAttributes(
                Arrays.asList("Domain", "ID", "Manufacturer", "Model name", "Serial", "Created", "Last connected",
                        "Event", "Quantity"),
                "Device's Events", null));

        reportAttributesMap.put(PROFILE_DOWNLOAD, new ReportAttributes(
                Arrays.asList("Domain", "ID", "Profile name", "Manufacturer", "Model name", "File type", "Created",
                        "Creator", "Version", "Completed Tasks", "Pending Tasks", "Rejected Tasks", "Failed Tasks"),
                "Profile download status", null));

        reportAttributesMap.put(FIRMWARE_VERSION, new ReportAttributes(
                Arrays.asList("Manufacturer", "Model name", "Firmware", "Domain", "Quantity"),
                "Firmware versions", null));

        reportAttributesMap.put(GROUP_UPDATE, new ReportAttributes(
                Arrays.asList("Manufacturer", "Model name", "Name", "Created", "Creator", "Domain", "Updated",
                        "Activated", "State"),
                "Group update", null));
    }

    public static String createExcelTable(final Integer domainId, final List<Object[]> report,
                                          final ReportType reportType, String fileName) {
        return createExcelTable(domainId, report, reportType, false, null, null, null, null, null, null, fileName);
    }

    public static String createExcelTable(final Integer domainId, final List<Object[]> report,
                                          final ReportType reportType, final boolean isAcs,
                                          final ClientType clientType, final String zoneId,
                                          final String dateFormat, final String timeFormat,
                                          Instant from, Instant to, String fileName) {
        return createExcelTable(domainId, report, reportType, isAcs, clientType, zoneId, dateFormat, timeFormat, from, to, null, fileName);
    }

    public static String createExcelTable(final Integer domainId, final List<Object[]> report,
                                          final ReportType reportType, final boolean isAcs,
                                          final ClientType clientType, final String zoneId,
                                          final String dateFormat, final String timeFormat,
                                          Instant from, Instant to, List<String> viewColumnNames, String fileName) {
        if (report.isEmpty()) {
            throw new FriendlyIllegalArgumentException(REPORT_IS_EMPTY);
        }
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(reportType.name());

        int rowNum = 0;
        Cell cell;
        Row row = sheet.createRow(rowNum);
        XSSFCellStyle styleForHeaderAndFooter = createStyleForTitle(workbook);
        XSSFCellStyle styleForTitles = createStyleForTitle(workbook);
        XSSFCellStyle style = createStyleForTitle(workbook);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        styleForTitles.setAlignment(HorizontalAlignment.CENTER);
        styleForTitles.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(false);
        style.setFont(font);
        makeBordersBold(style);
        makeBordersBold(styleForTitles);

        ReportAttributes attributes = reportAttributesMap.get(reportType);
        String header = attributes != null ? attributes.headerTemplate : null;
        if (header != null) {
            if (from == null || to == null) {
                from = Instant.now();
                to = Instant.now();
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedStart = formatter.format(from.atZone(ZoneId.of(zoneId)).toLocalDate());
            String formattedEnd = formatter.format(to.atZone(ZoneId.of(zoneId)).toLocalDate());
            cell = row.createCell(0, CellType.STRING);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 20));
            cell.setCellValue(String.format(header, formattedStart, formattedEnd));
            cell.setCellStyle(styleForHeaderAndFooter);
            rowNum++;
        }

        row = sheet.createRow(rowNum);

        // Title
        final List<String> titles;
        if (viewColumnNames == null) {
            titles = attributes == null ? null : attributes.fields;
        } else {
            titles = viewColumnNames;
        }

        if (titles != null) {
            for (int i = 0; i < titles.size(); i++) {
                cell = row.createCell(i, CellType.STRING);
                cell.setCellValue(titles.get(i));
                cell.setCellStyle(styleForTitles);
            }
        }

        // Data
        for (Object[] reportItem : report) {
            rowNum++;
            row = sheet.createRow(rowNum);

            for (int i = 0; i < reportItem.length; i++) {
                final Object item = reportItem[i];
                cell = row.createCell(i, CellType.STRING);
                cell.setCellValue(getCellValue(item, isAcs, clientType, zoneId, dateFormat, timeFormat));
                cell.setCellStyle(style);
            }
        }

        rowNum++;
        row = sheet.createRow(rowNum);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 20));
        String footer = "Amount of rows: %d";
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue(String.format(footer, rowNum - 2));
        cell.setCellStyle(styleForHeaderAndFooter);

        for(int i = 0; i < rowNum; ++i) {
            sheet.autoSizeColumn(i);
        }

        final String publicPath = domainId + SLASH + fileName;
        final String path = reportsPath + publicPath;
        final File file = new File(path);
        file.getParentFile().mkdirs();

        try (final FileOutputStream outFile = new FileOutputStream(file)) {
            workbook.write(outFile);
        } catch (IOException e) {
            throw new FriendlyIllegalArgumentException(REPORT_IS_EMPTY);
        }

        return publicPath;
    }

    private static void saveToExcelHeader(SXSSFSheet sheet, Set<String> strings) {
        SXSSFRow header = sheet.createRow(0);
        int cellNum = 0;
        for (String cellName : strings) {
            header.createCell(cellNum++).setCellValue(cellName);
        }
    }

    private synchronized void saveToExcel(SXSSFSheet sheet, Map<String, String> params) {
        int rowNum = sheet.getLastRowNum() + 1;
        SXSSFRow row = sheet.createRow(rowNum);
        int cellNum = 0;
        for (String value : params.values()) {
            row.createCell(cellNum++).setCellValue(value != null ? value : "");
        }
    }


    private static void makeBordersBold(XSSFCellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // Set border color (optional)
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());

        // Set bold border width
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.MEDIUM);
        style.setBorderRight(BorderStyle.MEDIUM);
    }

    private static String getCellValue(final Object item, final boolean isAcs,
                                       final ClientType clientType, final String zoneId,
                                       final String dateFormat, final String timeFormat) {
        if (item == null) {
            return EMPTY_STRING;
        }
        if (item instanceof Instant) {
            return isAcs ? DateTimeUtils.formatAcs((Instant) item, clientType, zoneId, dateFormat, timeFormat)
                    : DateTimeUtils.format((Instant) item, zoneId, dateFormat, timeFormat);
        } else {
            return item.toString();
        }
    }

    private static XSSFCellStyle createStyleForTitle(final XSSFWorkbook workbook) {
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    public static List<FileReport> getReportFiles(final List<Integer> domainIds, final ClientType clientType,
                                                  final String zoneId, final Instant date,
                                                  final String dateFormat, final String timeFormat) {
        return domainIds.stream()
                .flatMap(domainId -> getFiles(domainId)
                        .stream()
                        .map(file -> {
                            BasicFileAttributes attr;
                            try {
                                attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            return getFileReport(file, attr, zoneId, dateFormat, timeFormat,
                                domainId == null || domainId == -1
                                        ? null : domainId.toString()); }))
                .filter(file -> {
                    Instant instant = DateTimeUtils.serverToClient(
                            DateTimeUtils.convertIsoDateToServer(file.getCreatedIso()), clientType, zoneId
                    );
                    return date == null || instant.isAfter(date) && instant.isBefore(date.plus(1, ChronoUnit.DAYS));
                })
                .collect(Collectors.toList());
    }

    private static FileReport getFileReport(final File file, BasicFileAttributes attr, final String zoneId, final String dateFormat,
                                            final String timeFormat, final String domain) {
        String creator = null;
        final String fileName = file.getName();
            int start = fileName.indexOf("'SP'") + 4;

            int end = fileName.lastIndexOf(".xlsx");

            if (start < end && start >= 0 && end >= 0) {
                creator = fileName.substring(start, end);
        }
        return FileReport.builder()
                .domain(domain)
                .name(fileName)
                .type(getReportType(file.getName()))
                .createdIso(Instant.ofEpochMilli(attr.creationTime().toMillis()))
                .created(DateTimeUtils.format(Instant.ofEpochMilli(attr.creationTime().toMillis()),
                        zoneId, dateFormat, timeFormat))
                .size(Math.round((double) file.length() / 1000) + " Kb")
                .link(domain + SLASH + file.getName())
                .creator(creator)
                .updated(DateTimeUtils.format(Instant.ofEpochMilli(file.lastModified()),
                        zoneId, dateFormat, timeFormat))
                .updatedIso(Instant.ofEpochMilli(file.lastModified()))
                .build();
    }

    public static List<File> getFiles(final Integer domainId) {
        if (Files.exists(Paths.get(reportsPath + domainId))) {
            try (Stream<Path> paths = Files.walk(Paths.get(reportsPath + domainId))) {
                List<File> collect = paths.filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());
                return collect;
            } catch (IOException e) {
                throw new FriendlyIllegalArgumentException(REPORT_IS_EMPTY);
            }
        }
        return Collections.emptyList();
    }

    public static File getReportFile(final String link) {
        return new File(reportsPath + link);
    }

    public static void deleteReportFile(final String link) {
        final File fileToDel = new File(reportsPath + link);
        if (!fileToDel.delete()) {
            throw new FriendlyEntityNotFoundException(CAN_NOT_DELETE_FILE, link);
        }
    }

    private static String getReportType(final String fileName) {
        return Arrays.stream(ReportType.values())
                .filter(type -> fileName.toLowerCase().startsWith(type.name().toLowerCase()))
                .findFirst()
                .map(reportAttributesMap::get)
                .map(attributes -> attributes.name)
                .orElse(null);
    }

    public String createXml(final Integer domainId, final ReportType reportType,
                            final XmlFile report, final Class<? extends XmlFile> mappedClass) {
        final String publicPath = domainId + SLASH + reportType.name().toLowerCase() + "_" +
                Instant.now().toString().replace(":", "-") + ".xml";
        final String path = reportsPath + publicPath;
        final File file = new File(path);
        file.getParentFile().mkdirs();

        final String xmlString = reportXmlConverter.convertToXml(report, mappedClass);
        try (final FileOutputStream outFile = new FileOutputStream(file)) {
            outFile.write(xmlString.getBytes());
        } catch (IOException e) {
            throw new FriendlyIllegalArgumentException(REPORT_IS_EMPTY);
        }

        return publicPath;
    }

    public static String createCsv(final Integer domainId, final ReportType reportType,
                                   final List<?> report, List<ViewColumn> columns) {
        if (report.isEmpty()) {
            throw new FriendlyIllegalArgumentException(REPORT_IS_EMPTY);
        }

        final String title = Arrays.stream(report.get(0).getClass().getDeclaredFields())
                .map(Field::getName)
                .map(field -> {
                    if (columns == null || field.equals("id")) {
                        return field;
                    } else {
                        return columns.stream()
                                .filter(c -> c.getColumnKey().equals(field))
                                .map(ViewColumn::getColumnName)
                                .findAny()
                                .orElse(null);
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(","));
        final String csvString =
                report.stream()
                        .map(r -> Arrays.stream(r.getClass().getDeclaredFields())
                                .map(field -> {
                                    try {
                                        final String fieldName = field.getName();
                                        if (columns == null || fieldName.equals("id")
                                                || columns.stream()
                                                .map(ViewColumn::getColumnKey)
                                                .anyMatch(column -> column.equals(fieldName))) {
                                            field.setAccessible(true);
                                            final Object o = field.get(r);
                                            if (o == null) {
                                                return null;
                                            }
                                            final String s = o.toString().trim();
                                            field.setAccessible(false);
                                            return s;
                                        }
                                        return null;
                                    } catch (IllegalAccessException e) {
                                        // ignore
                                    }
                                    return null;
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.joining(",")))
                        .collect(Collectors.joining(",\n"));

        final String publicPath = domainId + SLASH + reportType.name().toLowerCase() + "_" +
                Instant.now().toString().replace(":", "-") + ".csv";
        final String path = reportsPath + publicPath;
        final File file = new File(path);
        file.getParentFile().mkdirs();

        try (final FileOutputStream outFile = new FileOutputStream(file)) {
            outFile.write((title + ",\n" + csvString).getBytes());
        } catch (IOException e) {
            throw new FriendlyIllegalArgumentException(REPORT_IS_EMPTY);
        }

        return publicPath;
    }


    public void createExcelFile(TypedQuery<Object[]> query, String publicPath, ClientType clientType, Map<String, Integer> fieldIndexMap, String sheetName) {
        Runnable task = () -> {
            try {
                processFileCreation(query, publicPath, clientType, fieldIndexMap, sheetName);
            } catch (Exception e) {
                log.error("Error generating Excel report", e);
            }
        };

        synchronized (lock) {
            taskQueue.add(task);
            if (currentTask == null || currentTask.isDone()) {
                executeNextTask();
            }
        }
    }

    private void executeNextTask() {
        Runnable nextTask = taskQueue.poll();
        if (nextTask != null) {
            synchronized (lock) {
                currentTask = taskExecutorService.submit(() -> {
                    try {
                        nextTask.run();
                    } finally {
                        executeNextTask();
                    }
                });
            }
        }
    }

    public void processFileCreation(TypedQuery<Object[]> query, String publicPath, ClientType clientType, Map<String, Integer> fieldIndexMap, String sheetName) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int i = 0;
        int sheetNumber = 1;

        final String tempPath = reportsPath + "temp/";
        Files.createDirectories(Paths.get(tempPath));

        final String tempFileName = publicPath.substring("%s/".length() - 1 );
        final String tempFilePath = tempPath + tempFileName;

        log.info("Attempting to write to temporary path: {}", tempFilePath);

        final File tempFile = new File(tempFilePath);
        try (SXSSFWorkbook workbook = new SXSSFWorkbook();
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {

            Files.createDirectories(tempFile.getParentFile().toPath());
            if (!Files.isWritable(tempFile.getParentFile().toPath())) {
                throw new IOException("Directory is not writable: " + tempFile.getParent());
            }

            SXSSFSheet sheet = workbook.createSheet(sheetName + sheetNumber);
            saveToExcelHeader(sheet, fieldIndexMap.keySet());

            List<Object[]> values = query.getResultList();
            precessDataToExcel(fieldIndexMap, values, i, sheetNumber, sheet, workbook, executorService, sheetName);

            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            workbook.write(outputStream);
            outputStream.flush();

            log.info("Report successfully generated in temporary path: {}", tempFilePath);

        } catch (IOException | java.util.concurrent.ExecutionException e) {
            log.error("Error generating Excel report", e);
            return;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error generating Excel report", e);
            return;
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        final String finalFilePath = reportsPath + publicPath;
        try {
            Files.createDirectories(Paths.get(finalFilePath).getParent());
            Files.move(Paths.get(tempFilePath), Paths.get(finalFilePath), StandardCopyOption.REPLACE_EXISTING);

            log.info("Report successfully moved to final path: {}", finalFilePath);

        } catch (IOException e) {
            log.error("Error moving file to final path: {}", finalFilePath, e);
        }

        wsSender.sendCompleteFileEvent(clientType, publicPath);
    }



    private void precessDataToExcel(Map<String, Integer> fieldIndexMap, List<Object[]> values, int i,
                                    int sheetNumber, SXSSFSheet sheet, SXSSFWorkbook workbook,
                                    ExecutorService executorService, String sheetName) throws InterruptedException, ExecutionException {
        for (Object[] row : values) {
            Map<String, String> rowData = new LinkedHashMap<>();
            processRowDataMap(fieldIndexMap, row, rowData);
            if (i >= MAX_ROWS_PER_SHEET) {
                sheetNumber++;
                sheet = workbook.createSheet(sheetName + sheetNumber);
                saveToExcelHeader(sheet, fieldIndexMap.keySet());
                i = 0;
            }
            saveToExcel(sheet, rowData);  // Save data to Excel based on rowData.
            i++;
            checkBatchSizeAndFlush(i, sheet, executorService);
        }
    }

    private static void processRowDataMap(Map<String, Integer> fieldIndexMap, Object[] row, Map<String, String> rowData) {
        for (Map.Entry<String, Integer> entry : fieldIndexMap.entrySet()) {
            String key = entry.getKey();
            Integer index = entry.getValue();
            if (index < row.length) {
                rowData.put(key, row[index] != null ? row[index].toString() : "");
            } else {
                rowData.put(key, "");
            }
        }
    }

    private static void checkBatchSizeAndFlush(int i, SXSSFSheet sheet, ExecutorService executorService) throws InterruptedException, ExecutionException {
        if (i % BATCH_SIZE == 0) {
            SXSSFSheet currentSheet = sheet;
            Future<Void> future = executorService.submit(() -> {
                synchronized (currentSheet) {
                    currentSheet.flushRows(BATCH_SIZE);
                }
                return null;
            });
            future.get();
        }
    }

}
