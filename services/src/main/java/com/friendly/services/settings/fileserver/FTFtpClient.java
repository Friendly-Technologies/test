package com.friendly.services.settings.fileserver;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.CAN_NOT_CONNECT_TO_FTP;
import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import com.friendly.services.infrastructure.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class FTFtpClient implements Closeable {

    private final String server;
    private final String user;
    private final String password;
    private final List<String> extensions;

    private FTPClient ftp;

    public void open() throws IOException {
        ftp = new FTPClient();

        ftp.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

        ftp.connect(StringUtils.substringBetween(server, "ftp://", "/"));
        ftp.setConnectTimeout(5000);
        ftp.enterLocalPassiveMode();
        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new FriendlyIllegalArgumentException(CAN_NOT_CONNECT_TO_FTP);
        }

        if (!ftp.login(user, password)) {
            throw new FriendlyIllegalArgumentException(CAN_NOT_CONNECT_TO_FTP);
        }
    }

    public void close() throws IOException {
        ftp.disconnect();
    }

    public Map<String, String> getConfigFilesBySerial(final String serial, final String zoneId,
                                              final String dateFormat, final String timeFormat) throws IOException {
        return Arrays.stream(ftp.listFiles())
                     .filter(ftpFile -> ftpFile.getName().contains("_") && ftpFile.getName().substring(ftpFile.getName().indexOf("_") + 1).startsWith(serial))
                     .filter(ftpFile -> extensions.stream()
                                               .anyMatch(ftpFile.getName()::endsWith))
                .collect(Collectors.toMap(ftpFile -> convertFileName(serial, ftpFile.getName(), zoneId, dateFormat, timeFormat), FTPFile::getName));
    }

    private String convertFileName(final String serial, String fileName, final String zoneId,
                                   final String dateFormat, final String timeFormat) {
        fileName = fileName.replace(serial + "_", "");
        final String[] s = fileName.split("_");
        if (s.length == 3) {
            final String s3 = s[2];
            final int index = s3.lastIndexOf(".");
            final Instant fileDate = LocalDateTime.parse(s[1] + StringUtils.substring(s3, 0, index),
                            DateTimeFormatter.ofPattern("yyyy.MM.ddHH-mm-ss")
                                    .withZone(ZoneId.from(ZoneOffset.UTC)))
                    .atZone(ZoneId.from(ZoneOffset.UTC))
                    .toInstant();
            return DateTimeUtils.format(fileDate, zoneId, dateFormat, timeFormat);
        } else {
            StringBuilder result = new StringBuilder();
            for (int i = 2; i < s.length; i++) {
                result.append(s[i]);
            }
            return result.toString();
        }
    }

    public String getFileNameForMask(String serial, String fileName, String zoneId, String dateFormat, String timeFormat) throws IOException {
        String[] s = fileName.split(" ");
        if (s.length > 2) {
            fileName = fileName.substring(fileName.indexOf(" ") + 1);
        }
        final Instant fileDate = DateTimeUtils.parse(fileName, zoneId, dateFormat, timeFormat);
        String dateTime = DateTimeFormatter.ofPattern("yyyy.MM.dd_HH-mm-ss").withZone(ZoneId.from(ZoneOffset.UTC)).format(fileDate);
        List<String> list = Arrays.stream(ftp.listFiles())
                .map(FTPFile::getName)
                .filter(name -> name.contains("_") && name.substring(name.indexOf("_") + 1).startsWith(serial))
                .filter(name -> extensions.stream()
                        .anyMatch(name::endsWith))
                .filter(name -> name.contains(dateTime))
                .collect(Collectors.toList());
        return list.isEmpty() ? "" : list.get(0);
    }

    public void deleteFile(String url) throws IOException {
        ftp.deleteFile(url.substring(url.lastIndexOf("/") + 1));
    }

    public void deleteFile(String location, String fileName) throws IOException {
        if (StringUtils.isNotBlank(location)) {
            ftp.changeWorkingDirectory(location);
        }
        ftp.deleteFile(fileName);
    }

    public boolean addFile(String location, MultipartFile file) throws IOException {
        if (StringUtils.isNotBlank(location)) {
            ftp.changeWorkingDirectory(location);
        }
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        return ftp.storeFile(file.getOriginalFilename(), file.getInputStream());
    }

    public FTPFile[] getFilesFromLocation(String location)  throws IOException{
        return ftp.listFiles(location, FTPFileFilters.NON_NULL);
    }
}