package com.bytedesk.disk.archive;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.mock.web.MockMultipartFile;
import com.bytedesk.core.config.properties.BytedeskProperties;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class DiskArchiveStorageTest {
    @TempDir Path temp;

    private DiskArchiveStorage storage(DiskArchiveSettings settings, List<Path> publicRoots) {
        return new DiskArchiveStorage(settings, publicRoots, Long.MAX_VALUE);
    }

    @Test
    void neverFallsBackToPublicStorageAndRejectsNestedPublicPaths() throws Exception {
        DiskArchiveSettings settings = new DiskArchiveSettings();
        assertThat(storage(settings, List.of(temp.resolve("public"))).configured()).isFalse();
        assertThatThrownBy(() -> storage(settings, List.of(temp.resolve("public"))).root())
                .isInstanceOf(DiskArchiveException.class);
        Files.createDirectory(temp.resolve("public"));
        settings.setPrivateDirectory(temp.resolve("public/contracts").toString());
        assertThatThrownBy(() -> storage(settings, List.of(temp.resolve("public"))).root())
                .isInstanceOf(DiskArchiveException.class);
    }

    @Test
    void storesOnlyGeneratedKeysAndChecksActualContent() throws Exception {
        DiskArchiveSettings settings = new DiskArchiveSettings();
        settings.setPrivateDirectory(temp.resolve("private").toString());
        DiskArchiveStorage archive = storage(settings, List.of(temp.resolve("public")));
        String key = UUID.randomUUID().toString();
        archive.store(key, new MockMultipartFile("file", "contract.pdf", "application/pdf",
                "%PDF-1.7\n".getBytes()), "application/pdf");
        assertThat(Files.readString(archive.root().resolve(key))).isEqualTo("%PDF-1.7\n");
        assertThatThrownBy(() -> archive.read("../public")).isInstanceOf(DiskArchiveException.class);
        assertThatThrownBy(() -> archive.store(key, new MockMultipartFile("file", new byte[]{1}), "application/pdf"))
                .isInstanceOf(DiskArchiveException.class);
        assertThatThrownBy(() -> archive.store(UUID.randomUUID().toString(),
                new MockMultipartFile("file", "fake.pdf", "application/pdf", "<html>bad</html>".getBytes()),
                "application/pdf")).isInstanceOf(DiskArchiveException.class);
        assertThatThrownBy(() -> archive.contentType("memo.txt")).isInstanceOf(DiskArchiveException.class);
    }

    @Test
    void refusesSymlinkToPublicRootAndClampsToServletLimit() throws Exception {
        Path publicRoot = Files.createDirectory(temp.resolve("public"));
        Path link = Files.createSymbolicLink(temp.resolve("link"), publicRoot);
        DiskArchiveSettings settings = new DiskArchiveSettings();
        settings.setPrivateDirectory(link.toString());
        assertThatThrownBy(() -> storage(settings, List.of(publicRoot)).root())
                .isInstanceOf(DiskArchiveException.class);

        var properties = mock(BytedeskProperties.class);
        when(properties.getUploadDir()).thenReturn(temp.resolve("public").toString());
        DiskArchiveSettings limited = new DiskArchiveSettings();
        limited.setPrivateDirectory(temp.resolve("private2").toString());
        limited.setMaxFileSize(20L * 1024 * 1024);
        var servlet = new DiskArchiveStorage(limited, properties, new MockEnvironment()
                .withProperty("spring.servlet.multipart.max-file-size", "2MB")
                .withProperty("spring.servlet.multipart.max-request-size", "1MB"));
        assertThat(servlet.maxFileSize()).isEqualTo(1024 * 1024 - 65536);
    }
}
