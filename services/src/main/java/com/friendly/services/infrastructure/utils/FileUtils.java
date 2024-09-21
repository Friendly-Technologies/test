package com.friendly.services.infrastructure.utils;

import com.friendly.commons.exceptions.FriendlyIllegalArgumentException;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.friendly.services.infrastructure.base.ServicesErrorRegistryEnum.REPORT_IS_EMPTY;

@UtilityClass
public class FileUtils {

    public static File getFileIfExists(String filePath) {
        final Path path = Paths.get(filePath);
        if(Files.exists(path)) {
            return path.toFile();
        }
        return null;
    }

    public static List<File> getFilesFromFolder(String folderPath) {
        final Path path = Paths.get(folderPath);
        if (Files.exists(path)) {
            try (Stream<Path> paths = Files.walk(path)) {
                return paths.filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new FriendlyIllegalArgumentException(REPORT_IS_EMPTY);
            }
        }
        return Collections.emptyList();
    }
}
