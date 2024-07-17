package fun.freechat.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.compress.archivers.tar.TarArchiveOutputStream.LONGFILE_GNU;

public class TarUtils {
    private static final int BUFFER_SIZE = 4096;

    public static void compressToGzip(List<Triple<String, InputStream, Long>> entries, OutputStream out) throws IOException {
        try (GzipCompressorOutputStream gzipOut = new GzipCompressorOutputStream(out);
             TarArchiveOutputStream tarOut = new TarArchiveOutputStream(gzipOut)) {
            tarOut.setLongFileMode(LONGFILE_GNU);

            for (Triple<String, InputStream, Long> entry : entries) {
                TarArchiveEntry tarEntry = new TarArchiveEntry(entry.getLeft());
                InputStream entryInputStream = entry.getMiddle();
                Long size = entry.getRight();

                if (Objects.isNull(size) || size <= BUFFER_SIZE) {
                    byte[] content = entryInputStream.readAllBytes();
                    tarEntry.setSize(content.length);
                    tarOut.putArchiveEntry(tarEntry);
                    tarOut.write(content);
                } else {
                    tarEntry.setSize(size);
                    tarOut.putArchiveEntry(tarEntry);
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int readSize = entryInputStream.read(buffer);
                    while (readSize > 0) {
                        tarOut.write(buffer, 0, readSize);
                        readSize = entryInputStream.read(buffer);
                    }
                }

                tarOut.closeArchiveEntry();
            }

            tarOut.finish();
        }
    }
}
