package com.bytedesk.disk.archive;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.*;
import com.bytedesk.core.config.properties.BytedeskProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartFile;

import static java.nio.file.attribute.PosixFilePermissions.*;

@Service
public class DiskArchiveStorage {
    private static final Map<String, String> TYPES = Map.of(
            "pdf", "application/pdf", "jpg", "image/jpeg", "jpeg", "image/jpeg", "png", "image/png",
            "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    private final DiskArchiveSettings settings;
    private final List<Path> publicRoots;
    private final long servletLimit;

    @Autowired
    public DiskArchiveStorage(DiskArchiveSettings settings, BytedeskProperties properties, Environment environment) {
        this(settings, exposedRoots(properties, environment), servletLimit(environment));
    }

    DiskArchiveStorage(DiskArchiveSettings settings, List<Path> publicRoots, long servletLimit) {
        this.settings = settings;
        this.publicRoots = publicRoots;
        this.servletLimit = servletLimit;
    }

    private static long servletLimit(Environment environment) {
        long fileLimit = DataSize.parse(environment.getProperty("spring.servlet.multipart.max-file-size", "1MB"))
                .toBytes();
        long requestLimit = DataSize.parse(environment.getProperty("spring.servlet.multipart.max-request-size", "10MB"))
                .toBytes();
        return Math.min(fileLimit < 0 ? Long.MAX_VALUE : fileLimit,
                requestLimit < 0 ? Long.MAX_VALUE : Math.max(0, requestLimit - 65536));
    }

    private static List<Path> exposedRoots(BytedeskProperties properties, Environment environment) {
        List<Path> roots = new ArrayList<>();
        if (StringUtils.hasText(properties.getUploadDir())) roots.add(Path.of(properties.getUploadDir()));
        for (String prefix : List.of("", "src/main/resources/", "target/classes/",
                "starter/src/main/resources/", "starter/target/classes/")) {
            for (String root : List.of("META-INF/resources", "resources", "static", "templates", "public")) {
                roots.add(Path.of(prefix + root));
            }
        }
        roots.add(Path.of("..", "deploy", "freeswitch", "recordings"));
        String locations = environment.getProperty("spring.web.resources.static-locations", "");
        for (String location : locations.split(",")) {
            if (location.strip().startsWith("file:")) roots.add(Path.of(java.net.URI.create(location.strip())));
        }
        return roots;
    }

    public boolean configured() {
        if (!StringUtils.hasText(settings.getPrivateDirectory())) return false;
        root();
        return true;
    }

    public long maxFileSize() {
        return Math.min(settings.getMaxFileSize(), servletLimit);
    }

    public Path root() {
        if (!StringUtils.hasText(settings.getPrivateDirectory())) {
            throw new DiskArchiveException(503, "尚未配置归档文件私有存储目录");
        }
        try {
            Path requested = Path.of(settings.getPrivateDirectory()).toAbsolutePath().normalize();
            for (Path publicRoot : publicRoots) {
                if (requested.startsWith(canonical(publicRoot))) {
                    throw new DiskArchiveException(503, "归档私有目录不能位于公开文件目录中");
                }
            }
            Path resolved = canonical(requested);
            for (Path publicRoot : publicRoots) {
                if (resolved.startsWith(canonical(publicRoot))) {
                    throw new DiskArchiveException(503, "归档私有目录不能位于公开文件目录中");
                }
            }
            if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) {
                Files.createDirectories(resolved, asFileAttribute(fromString("rwx------")));
            } else {
                Files.createDirectories(resolved);
            }
            return resolved.toRealPath();
        } catch (IOException | InvalidPathException e) {
            throw new DiskArchiveException(503, "归档私有存储不可用");
        }
    }

    private static Path canonical(Path path) throws IOException {
        Path absolute = path.toAbsolutePath().normalize();
        if (Files.exists(absolute, LinkOption.NOFOLLOW_LINKS)) return absolute.toRealPath();
        Path parent = absolute.getParent();
        return parent == null ? absolute : canonical(parent).resolve(absolute.getFileName());
    }

    public String contentType(String name) {
        int dot = name.lastIndexOf('.');
        String type = dot < 0 ? null : TYPES.get(name.substring(dot + 1).toLowerCase(Locale.ROOT));
        if (type == null) throw new DiskArchiveException(415, "仅支持 PDF、JPEG、PNG 和 DOCX 归档文件");
        return type;
    }

    private Path object(String key) {
        if (!key.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
            throw DiskArchiveException.unavailable();
        }
        return root().resolve(key);
    }

    public void store(String key, MultipartFile file, String type) {
        Path path = object(key);
        try {
            var options = Set.<OpenOption>of(StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE,
                    LinkOption.NOFOLLOW_LINKS);
            var attributes = FileSystems.getDefault().supportedFileAttributeViews().contains("posix")
                    ? new FileAttribute<?>[]{asFileAttribute(fromString("rw-------"))}
                    : new FileAttribute<?>[0];
            try (InputStream input = file.getInputStream();
                    OutputStream output = Channels.newOutputStream(Files.newByteChannel(path, options, attributes))) {
                long total = 0;
                byte[] buffer = new byte[8192];
                int count;
                while ((count = input.read(buffer)) != -1) {
                    total += count;
                    if (total > maxFileSize()) throw new DiskArchiveException(413, "文件大小超限");
                    output.write(buffer, 0, count);
                }
                if (total == 0 || total != file.getSize()) {
                    throw new DiskArchiveException(400, "文件为空或传输不完整");
                }
            }
            validateContent(path, type);
        } catch (IOException e) {
            throw new DiskArchiveException(503, "归档文件存储失败");
        }
    }

    private void validateContent(Path path, String type) throws IOException {
        byte[] head;
        try (InputStream input = Files.newInputStream(path, LinkOption.NOFOLLOW_LINKS)) {
            head = input.readNBytes(8);
        }
        boolean valid = switch (type) {
            case "application/pdf" -> head.length >= 5
                    && new String(head, 0, 5, java.nio.charset.StandardCharsets.US_ASCII).equals("%PDF-");
            case "image/jpeg" -> head.length >= 3 && head[0] == (byte) 0xff && head[1] == (byte) 0xd8
                    && head[2] == (byte) 0xff;
            case "image/png" -> Arrays.equals(head, new byte[]{(byte) 137, 80, 78, 71, 13, 10, 26, 10});
            default -> validDocx(path);
        };
        if (!valid) throw new DiskArchiveException(415, "文件内容与格式不符");
    }

    private boolean validDocx(Path path) throws IOException {
        try (var zip = new java.util.zip.ZipFile(path.toFile())) {
            return zip.size() <= 10000 && zip.getEntry("[Content_Types].xml") != null
                    && zip.getEntry("word/document.xml") != null && zip.getEntry("word/vbaProject.bin") == null;
        } catch (java.util.zip.ZipException e) {
            return false;
        }
    }

    public InputStream read(String key) {
        try {
            return Files.newInputStream(object(key), LinkOption.NOFOLLOW_LINKS);
        } catch (NoSuchFileException e) {
            throw DiskArchiveException.unavailable();
        } catch (IOException e) {
            throw new DiskArchiveException(503, "归档文件读取失败");
        }
    }

    public void deleteUnpublished(String key) {
        try {
            Files.deleteIfExists(object(key));
        } catch (IOException e) {
            throw new DiskArchiveException(503, "归档暂存清理失败");
        }
    }
}
