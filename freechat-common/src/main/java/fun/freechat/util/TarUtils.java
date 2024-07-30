package fun.freechat.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.lang3.tuple.Triple;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.compress.archivers.tar.TarArchiveOutputStream.LONGFILE_GNU;

public class TarUtils {
    private static final int BUFFER_SIZE = 4096;

    public static void compressGzip(List<Triple<String, InputStream, Long>> entries, OutputStream out) throws IOException {
        try (GzipCompressorOutputStream gzipOut = new GzipCompressorOutputStream(out);
             TarArchiveOutputStream tarOut = new TarArchiveOutputStream(gzipOut)) {
            tarOut.setLongFileMode(LONGFILE_GNU);

            for (Triple<String, InputStream, Long> entry : entries) {
                TarArchiveEntry tarEntry = new TarArchiveEntry(entry.getLeft());
                InputStream entryInputStream = entry.getMiddle();
                Long size = entry.getRight();

                if (size == null || size <= BUFFER_SIZE) {
                    byte[] content = entryInputStream.readAllBytes();
                    tarEntry.setSize(content.length);
                    tarOut.putArchiveEntry(tarEntry);
                    tarOut.write(content);
                } else {
                    tarEntry.setSize(size);
                    tarOut.putArchiveEntry(tarEntry);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len;
                    while ((len = entryInputStream.read(buffer)) != -1) {
                        tarOut.write(buffer, 0, len);
                    }
                }

                tarOut.closeArchiveEntry();
            }

            tarOut.finish();
        }
    }

    public static void extractGzip(InputStream inputStream, Path dstDir) throws IOException {
        try (GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(inputStream);
             TarArchiveInputStream tarIn = new TarArchiveInputStream(gzipIn)) {
            TarArchiveEntry entry;
            while (Objects.nonNull(entry = tarIn.getNextEntry())) {
                if (entry.isDirectory()) {
                    continue;
                }
                Path curPath = dstDir.resolve(entry.getName());
                Path parent = curPath.getParent();
                if (!Files.exists(parent)) {
                    Files.createDirectories(parent);
                }
                try (OutputStream out = Files.newOutputStream(curPath)) {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int len;
                    while ((len = tarIn.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                }
            }
        }
    }
}
