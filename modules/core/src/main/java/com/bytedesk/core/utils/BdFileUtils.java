package com.bytedesk.core.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.impl.client.HttpClients;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
// import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import java.nio.charset.Charset;

/**
 * File utilities.
 *
 * @author johnniang
 * @date 4/9/19
 */
@Slf4j
@UtilityClass
public class BdFileUtils {

    /**
     * 上传文件
     * 
     * @param url
     * @param file
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String uploadFile(String url, File file) throws IOException {
        log.info("uploadFile url: {}", url);

        HttpPost post = new HttpPost(url);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpEntity entity = null;
        HttpResponse response = null;
        String boundaryStr = "------------7da2e536604c8";
        post.addHeader("Connection", "keep-alive");
        post.addHeader("Accept", "*/*");
        post.addHeader("Content-Type", "multipart/form-data;boundary=" + boundaryStr);
        post.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
        MultipartEntityBuilder meb = MultipartEntityBuilder.create();
        meb.setBoundary(boundaryStr).setCharset(Charset.forName("utf-8")).setMode(HttpMultipartMode.EXTENDED);
        // meb.addBinaryBody("media", file, ContentType.APPLICATION_OCTET_STREAM, file.getName());
        // entity = meb.build();
        post.setEntity(entity);
        response = httpclient.execute(post);
        entity = response.getEntity();
        String result = EntityUtils.toString(entity, "utf-8");
        EntityUtils.consume(entity);// 关闭流

        return result;
    }

    /**
     * Copies folder.
     *
     * @param source source path must not be null
     * @param target target path must not be null
     */
    public static void copyFolder(@NonNull Path source, @NonNull Path target) throws IOException {
        Assert.notNull(source, "Source path must not be null");
        Assert.notNull(target, "Target path must not be null");

        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

            private Path current;

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                current = target.resolve(source.relativize(dir).toString());
                Files.createDirectories(current);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file).toString()),
                        StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Deletes folder recursively.
     *
     * @param deletingPath deleting path must not be null
     */
    public static void deleteFolder(@NonNull Path deletingPath) throws IOException {
        Assert.notNull(deletingPath, "Deleting path must not be null");

        log.debug("Deleting [{}]", deletingPath);

        Files.walk(deletingPath)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);

        log.debug("Deleted [{}] successfully", deletingPath);
    }

    /**
     * Unzip content to the target path.
     *
     * @param zis        zip input stream must not be null
     * @param targetPath target path must not be null and not empty
     * @throws IOException
     */
    public static void unzip(@NonNull ZipInputStream zis, @NonNull Path targetPath) throws IOException {
        Assert.notNull(zis, "Zip input stream must not be null");
        Assert.notNull(targetPath, "Target path must not be null");

        // Create path if absent
        createIfAbsent(targetPath);

        // Must be empty
        mustBeEmpty(targetPath);

        ZipEntry zipEntry = zis.getNextEntry();

        while (zipEntry != null) {
            // Resolve the entry path
            Path entryPath = targetPath.resolve(zipEntry.getName());

            // Check directory
            BdFileUtils.checkDirectoryTraversal(targetPath, entryPath);

            if (zipEntry.isDirectory()) {
                // Create directories
                Files.createDirectories(entryPath);
            } else {
                // Copy file
                Files.copy(zis, entryPath);
            }

            zipEntry = zis.getNextEntry();
        }
    }

    /**
     * Skips zip parent folder. (Go into base folder)
     *
     * @param unzippedPath unzipped path must not be null
     * @return path containing base files
     * @throws IOException
     */
    public static Path skipZipParentFolder(@NonNull Path unzippedPath) throws IOException {
        Assert.notNull(unzippedPath, "Unzipped folder must not be  null");

        List<Path> childrenPath = Files.list(unzippedPath).collect(Collectors.toList());

        if (childrenPath.size() == 1 && Files.isDirectory(childrenPath.get(0))) {
            return childrenPath.get(0);
        }

        return unzippedPath;
    }

    /**
     * Creates directories if absent.
     *
     * @param path path must not be null
     * @throws IOException
     */
    public static void createIfAbsent(@NonNull Path path) throws IOException {
        Assert.notNull(path, "Path must not be null");

        if (Files.notExists(path)) {
            // Create directories
            Files.createDirectories(path);

            log.debug("Created directory: [{}]", path);
        }
    }

    /**
     * Checks if the given path is empty.
     *
     * @param path path must not be null
     * @return true if the given path is empty; false otherwise
     * @throws IOException
     */
    public static boolean isEmpty(@NonNull Path path) throws IOException {
        Assert.notNull(path, "Path must not be null");

        return Files.list(path).count() == 0;
    }

    /**
     * The given path must be empty.
     *
     * @param path path must not be null
     * @throws IOException
     */
    public static void mustBeEmpty(@NonNull Path path) throws IOException {
        if (!isEmpty(path)) {
            throw new DirectoryNotEmptyException("Target directory: " + path + " was not empty");
        }
    }

    /**
     * Checks directory traversal vulnerability.
     *
     * @param parentPath  parent path must not be null.
     * @param pathToCheck path to check must not be null
     */
    public static void checkDirectoryTraversal(@NonNull String parentPath, @NonNull String pathToCheck) {
        checkDirectoryTraversal(Paths.get(parentPath), Paths.get(pathToCheck));
    }

    /**
     * Checks directory traversal vulnerability.
     *
     * @param parentPath  parent path must not be null.
     * @param pathToCheck path to check must not be null
     */
    public static void checkDirectoryTraversal(@NonNull Path parentPath, @NonNull String pathToCheck) {
        checkDirectoryTraversal(parentPath, Paths.get(pathToCheck));
    }

    /**
     * Checks directory traversal vulnerability.
     *
     * @param parentPath  parent path must not be null.
     * @param pathToCheck path to check must not be null
     */
    public static void checkDirectoryTraversal(@NonNull Path parentPath, @NonNull Path pathToCheck) {
        Assert.notNull(parentPath, "Parent path must not be null");
        Assert.notNull(pathToCheck, "Path to check must not be null");

        if (pathToCheck.startsWith(parentPath.normalize())) {
            return;
        }
    }

    /**
     * Closes input stream quietly.
     *
     * @param inputStream input stream
     */
    public static void closeQuietly(@Nullable InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            // Ignore this exception
            log.warn("Failed to close input stream", e);
        }
    }

    /**
     * Closes zip input stream quietly.
     *
     * @param zipInputStream zip input stream
     */
    public static void closeQuietly(@Nullable ZipInputStream zipInputStream) {
        try {
            if (zipInputStream != null) {
                zipInputStream.closeEntry();
                zipInputStream.close();
            }
        } catch (IOException e) {
            // Ignore this exception
            log.warn("Failed to close zip input stream", e);
        }
    }

    /**
     * Deletes folder quietly.
     *
     * @param deletingPath deleting path
     */
    public static void deleteFolderQuietly(@Nullable Path deletingPath) {
        try {
            if (deletingPath != null) {
                BdFileUtils.deleteFolder(deletingPath);
            }
        } catch (IOException e) {
            log.warn("Failed to delete " + deletingPath);
        }
    }

    /**
     * 删除文件或者文件夹
     *
     * @param path path
     * @return boolean
     * @throws Exception IORuntimeException
     */
    public static boolean del(Path path) throws Exception {
        if (Files.notExists(path)) {
            return true;
        }
        try {
            if (Files.isDirectory(path)) {
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
                        if (e == null) {
                            Files.delete(dir);
                            return FileVisitResult.CONTINUE;
                        } else {
                            throw e;
                        }
                    }
                });
            } else {
                Files.delete(path);
            }
        } catch (IOException e) {
            throw new Exception(e);
        }
        return true;
    }


    /**
     * 判断文件是否为Excel文件
     * 
     * @param fileName 文件名
     * @return 如果是Excel文件返回true，否则返回false
     */
    public static boolean isExcelFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        String lowerFileName = fileName.toLowerCase();
        return lowerFileName.endsWith(".xlsx") || lowerFileName.endsWith(".xls");
    }
}
