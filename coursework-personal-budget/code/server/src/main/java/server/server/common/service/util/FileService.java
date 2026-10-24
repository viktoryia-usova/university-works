package server.server.common.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import server.server.common.exception.util.FileNotFoundException;
import server.server.common.util.file.FileProperties;
import server.server.common.util.log.AppLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private static final String LOG = AppLogger.name(FileService.class);

    private final FileProperties properties;

    public Resource get(String filename) {
        AppLogger.debug("Getting file: " + filename, LOG);

        Resource resource = null;
        try {
            resource = new UrlResource(Paths.get(properties.getContainer() + filename).toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        if (!resource.exists() || !resource.isReadable()) {
            throw new FileNotFoundException(filename);
        }

        return resource;
    }

    public String save(MultipartFile file) {
        String original = Objects.requireNonNull(file.getOriginalFilename());
        String extension = original.substring(original.lastIndexOf('.'));
        String filename = UUID.randomUUID() + extension;
        String filePath = properties.getContainer() + filename;

        AppLogger.info("Saving file: " + original + " -> " + filename, LOG);

        File dir = new File(properties.getContainer());
        if (!dir.exists()) {
            AppLogger.info("Creating folders: " + dir, LOG);
            if(!dir.mkdirs()) {
                throw new RuntimeException("Failed to create folders: " + dir);
            }
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AppLogger.info("File saved: " + filename, LOG);
        return filename;
    }

    public void delete(String filename) {
        AppLogger.info("Deleting file: " + filename, LOG);
        File file = new File(properties.getContainer() + filename);

        if (!file.exists()) {
            throw new FileNotFoundException(filename);
        }

        if (!file.delete()) {
            throw new RuntimeException("Failed to delete file: " + filename);
        }
    }

    public boolean exists(String filename) {
        return new File(properties.getContainer() + filename).exists();
    }
}
