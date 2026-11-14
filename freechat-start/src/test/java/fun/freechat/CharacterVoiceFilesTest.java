package fun.freechat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import fun.freechat.api.util.CharacterVoiceFiles;
import fun.freechat.service.common.FileStore;
import fun.freechat.service.common.impl.LocalFileStoreImpl;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockMakers;
import org.springframework.test.util.ReflectionTestUtils;

class CharacterVoiceFilesTest {
    // FileStore keys are relative to root; every physical fixture stays inside @TempDir.
    private static final String DIRECTORY = "voices/backend-a";
    private static final String SPEAKER = "engine-generated-id.wav";
    // Synthetic opaque bytes, not real audio: resolution must neither decode nor rewrite them.
    private static final byte[] MP3 = {'I', 'D', '3', 4, 0, 0, 0, 0, 0, 3, 1, 2, 3};
    private static final byte[] WAV = "RIFF-synthetic-WAVE-fixture".getBytes(StandardCharsets.UTF_8);

    @TempDir
    Path root;

    private final LocalFileStoreImpl store = new LocalFileStoreImpl();

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(store, "basePathStr", root.toString());
        store.postConstruct();
    }

    @ParameterizedTest
    @CsvSource({
        "UUID-s_voice.wav, UUID-s_voice.mp3",
        "original.wav, original.wav",
        "voice.sample.v2.wav, voice.sample.v2.mp3",
        "妮可.原声.v2.wav, 妮可.原声.v2.mp3"
    })
    void historicalAndRememberedSourcesPreserveOriginalFilenameAndBytes(String speaker, String name)
            throws IOException {
        byte[] content = name.endsWith(".mp3") ? MP3 : WAV;
        String source = upload(DIRECTORY, name, content);
        List<String> files = store.list(DIRECTORY, null, false);

        assertEquals(source, CharacterVoiceFiles.match(speaker, files));
        assertEquals(source, CharacterVoiceFiles.resolve(store, DIRECTORY, speaker));
        assertFalse(store.exists(DIRECTORY + ".sources"), "Historical resolution must not create mappings");

        CharacterVoiceFiles.remember(store, DIRECTORY, speaker, name);

        String resolved = CharacterVoiceFiles.resolve(store, DIRECTORY, speaker);
        assertEquals(source, resolved);
        assertEquals(name, Path.of(resolved).getFileName().toString());
        assertArrayEquals(content, store.readBytes(resolved));
        assertEquals(List.of(source), store.list(DIRECTORY, null, false), "No renamed audio or mapping in audio list");
    }

    @ParameterizedTest
    @ValueSource(strings = {SPEAKER, "引擎.标识.wav"})
    void explicitMappingOverridesEvenExactAndAmbiguousHistoricalMatches(String speaker) throws Exception {
        String name = "上传的.原始声音.mp3";
        String source = upload(DIRECTORY, name, MP3);
        String exact = upload(DIRECTORY, speaker, WAV);
        String competing = upload(DIRECTORY, speaker.substring(0, speaker.length() - 4) + ".mp3", WAV);
        String digest = HexFormat.of()
                .formatHex(MessageDigest.getInstance("SHA-256").digest(speaker.getBytes(StandardCharsets.UTF_8)));
        String mapping = DIRECTORY + ".sources/" + digest;

        CharacterVoiceFiles.remember(store, DIRECTORY, speaker, name);

        assertEquals(mapping, CharacterVoiceFiles.mappingPath(DIRECTORY, speaker));
        assertEquals(name, store.readString(mapping));
        assertEquals(source, CharacterVoiceFiles.resolve(store, DIRECTORY, speaker));
        assertArrayEquals(MP3, store.readBytes(source));
        assertEquals(List.of(mapping), store.list(DIRECTORY + ".sources", null, false));
        assertEquals(Set.of(source, exact, competing), Set.copyOf(store.list(DIRECTORY, null, false)));
    }

    @Test
    void identicalSpeakerIdentifiersDoNotShareMappingsAcrossBackends() throws IOException {
        String secondDirectory = "voices/backend-b";
        String first = upload(DIRECTORY, "first.mp3", MP3);
        String second = upload(secondDirectory, "second.wav", WAV);
        CharacterVoiceFiles.remember(store, DIRECTORY, SPEAKER, "first.mp3");
        assertThrows(NoSuchFileException.class, () -> CharacterVoiceFiles.resolve(store, secondDirectory, SPEAKER));

        CharacterVoiceFiles.remember(store, secondDirectory, SPEAKER, "second.wav");

        assertEquals(first, CharacterVoiceFiles.resolve(store, DIRECTORY, SPEAKER));
        assertEquals(second, CharacterVoiceFiles.resolve(store, secondDirectory, SPEAKER));
        assertArrayEquals(MP3, store.readBytes(first));
        assertArrayEquals(WAV, store.readBytes(second));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void blankSpeakerUsesOnlyItsBackendPendingMapping(String speaker) throws IOException {
        String otherDirectory = "voices/backend-b";
        String otherVoice = upload(DIRECTORY, "someone-else.wav", WAV);
        CharacterVoiceFiles.remember(store, DIRECTORY, "someone-else.wav", "someone-else.wav");
        assertNull(CharacterVoiceFiles.resolve(store, DIRECTORY, speaker), "Do not guess from an unrelated upload");

        String pendingVoice = upload(DIRECTORY, "pending-upload.mp3", MP3);
        CharacterVoiceFiles.remember(store, DIRECTORY, speaker, "pending-upload.mp3");
        upload(otherDirectory, "someone-else.wav", WAV);

        assertEquals(DIRECTORY + ".sources/pending", CharacterVoiceFiles.mappingPath(DIRECTORY, speaker));
        assertEquals(pendingVoice, CharacterVoiceFiles.resolve(store, DIRECTORY, speaker));
        assertArrayEquals(MP3, store.readBytes(pendingVoice));
        assertEquals(otherVoice, CharacterVoiceFiles.resolve(store, DIRECTORY, "someone-else.wav"));
        assertNull(CharacterVoiceFiles.resolve(store, otherDirectory, speaker));
        assertThrows(NoSuchFileException.class, () -> CharacterVoiceFiles.resolve(store, DIRECTORY, SPEAKER));
    }

    @Test
    void namedSpeakersInOneBackendKeepIndependentMappings() throws IOException {
        String first = upload(DIRECTORY, "first.mp3", MP3);
        String second = upload(DIRECTORY, "second.wav", WAV);
        CharacterVoiceFiles.remember(store, DIRECTORY, SPEAKER, "first.mp3");
        CharacterVoiceFiles.remember(store, DIRECTORY, "another-id.wav", "second.wav");

        assertEquals(first, CharacterVoiceFiles.resolve(store, DIRECTORY, SPEAKER));
        assertEquals(second, CharacterVoiceFiles.resolve(store, DIRECTORY, "another-id.wav"));
    }

    @Test
    void missingDirectoryEmptyDirectoryAndUnrelatedFilesNeverResolveByGuessing() throws IOException {
        assertThrows(NoSuchFileException.class, () -> CharacterVoiceFiles.resolve(store, DIRECTORY, SPEAKER));
        store.createDirectories(DIRECTORY);
        assertThrows(NoSuchFileException.class, () -> CharacterVoiceFiles.resolve(store, DIRECTORY, SPEAKER));
        String unrelated = upload(DIRECTORY, "different-basename.mp3", MP3);
        assertThrows(NoSuchFileException.class, () -> CharacterVoiceFiles.resolve(store, DIRECTORY, SPEAKER));
        assertThrows(NoSuchFileException.class, () -> CharacterVoiceFiles.match(SPEAKER, List.of()));
        assertThrows(NoSuchFileException.class, () -> CharacterVoiceFiles.match(SPEAKER, List.of(unrelated)));
    }

    @Test
    void multipleSameStemFilesAreAmbiguousEvenIfOneExactlyMatchesSpeaker() throws IOException {
        String exact = upload(DIRECTORY, "voice.wav", WAV);
        String converted = upload(DIRECTORY, "voice.mp3", MP3);

        assertThrows(IOException.class, () -> CharacterVoiceFiles.match("voice.wav", List.of(exact, converted)));
        assertThrows(IOException.class, () -> CharacterVoiceFiles.match("voice.wav", List.of(converted, exact)));
        assertThrows(IOException.class, () -> CharacterVoiceFiles.resolve(store, DIRECTORY, "voice.wav"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {SPEAKER})
    void missingMappedSourceDoesNotFallBackToAnotherVoice(String speaker) throws IOException {
        upload(DIRECTORY, SPEAKER, WAV);
        CharacterVoiceFiles.remember(store, DIRECTORY, speaker, "deleted-upload.mp3");

        NoSuchFileException failure =
                assertThrows(NoSuchFileException.class, () -> CharacterVoiceFiles.resolve(store, DIRECTORY, speaker));

        assertEquals(DIRECTORY + "/deleted-upload.mp3", failure.getFile());
    }

    @ParameterizedTest
    @MethodSource("unsafeNames")
    void unsafeSpeakersAreRejectedBeforeAnyStoreAccess(String speaker) {
        FileStore files = mockStore();

        assertAll(
                () -> assertThrows(IOException.class, () -> CharacterVoiceFiles.mappingPath(DIRECTORY, speaker)),
                () -> assertThrows(IOException.class, () -> CharacterVoiceFiles.resolve(files, DIRECTORY, speaker)),
                () -> assertThrows(
                        IOException.class, () -> CharacterVoiceFiles.remember(files, DIRECTORY, speaker, "voice.mp3")),
                () -> assertThrows(IOException.class, () -> CharacterVoiceFiles.match(speaker, List.of())));
        verifyNoInteractions(files);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void matchRejectsBlankSpeakerInsteadOfGuessing(String speaker) {
        assertThrows(IOException.class, () -> CharacterVoiceFiles.match(speaker, List.of(DIRECTORY + "/voice.mp3")));
    }

    @ParameterizedTest
    @MethodSource("unsafeNames")
    @NullSource
    @ValueSource(strings = {"", " ", "\t"})
    void unsafeSourcesCannotBeRememberedOrUsedToAccessPathsOutsideDirectory(String source) throws IOException {
        FileStore writes = mockStore();
        assertThrows(IOException.class, () -> CharacterVoiceFiles.remember(writes, DIRECTORY, SPEAKER, source));
        verifyNoInteractions(writes);

        FileStore reads = mockStore();
        String mapping = CharacterVoiceFiles.mappingPath(DIRECTORY, SPEAKER);
        when(reads.exists(mapping)).thenReturn(true);
        when(reads.size(mapping)).thenReturn(32L);
        when(reads.readString(mapping)).thenReturn(source);

        assertThrows(IOException.class, () -> CharacterVoiceFiles.resolve(reads, DIRECTORY, SPEAKER));
        verify(reads).exists(mapping);
        verify(reads).isRegularFile(mapping);
        verify(reads).size(mapping);
        verify(reads).readString(mapping);
        verifyNoMoreInteractions(reads);
    }

    @ParameterizedTest
    @ValueSource(longs = {1025, 1048576})
    void oversizedMappingsAreRejectedWithoutReadingTheirContent(long size) throws IOException {
        FileStore files = mockStore();
        String mapping = CharacterVoiceFiles.mappingPath(DIRECTORY, SPEAKER);
        when(files.exists(mapping)).thenReturn(true);
        when(files.size(mapping)).thenReturn(size);

        assertThrows(IOException.class, () -> CharacterVoiceFiles.resolve(files, DIRECTORY, SPEAKER));
        verify(files).exists(mapping);
        verify(files).isRegularFile(mapping);
        verify(files).size(mapping);
        verifyNoMoreInteractions(files);
    }

    @Test
    void mappingAtByteLimitWorksThroughFileStoreWithoutObtainingLocalPaths() throws IOException {
        FileStore files = mockStore();
        String name = "音".repeat(340) + ".mp3";
        assertEquals(1024, name.getBytes(StandardCharsets.UTF_8).length);
        String mapping = CharacterVoiceFiles.mappingPath(DIRECTORY, SPEAKER);
        String source = DIRECTORY + "/" + name;
        when(files.exists(mapping)).thenReturn(true);
        when(files.size(mapping)).thenReturn(1024L);
        when(files.readString(mapping)).thenReturn(name);
        when(files.exists(source)).thenReturn(true);

        assertEquals(source, CharacterVoiceFiles.resolve(files, DIRECTORY, SPEAKER));
        verify(files).exists(mapping);
        verify(files).isRegularFile(mapping);
        verify(files).size(mapping);
        verify(files).readString(mapping);
        verify(files).exists(source);
        verify(files).isRegularFile(source);
        verifyNoMoreInteractions(files);
    }

    @ParameterizedTest
    @ValueSource(strings = {"size", "readString", "list", "writeRegularFile"})
    void fileStoreIoFailuresPropagateUnchanged(String operation) throws IOException {
        FileStore files = mockStore();
        String mapping = CharacterVoiceFiles.mappingPath(DIRECTORY, SPEAKER);
        IOException failure = new IOException("synthetic " + operation + " failure");
        switch (operation) {
            case "size" -> {
                when(files.exists(mapping)).thenReturn(true);
                when(files.size(mapping)).thenThrow(failure);
            }
            case "readString" -> {
                when(files.exists(mapping)).thenReturn(true);
                when(files.readString(mapping)).thenThrow(failure);
            }
            case "list" -> {
                when(files.exists(DIRECTORY)).thenReturn(true);
                when(files.list(DIRECTORY, null, false)).thenThrow(failure);
            }
            case "writeRegularFile" -> doThrow(failure).when(files).writeRegularFile(eq(mapping), any(byte[].class));
            default -> throw new AssertionError(operation);
        }

        IOException actual = assertThrows(IOException.class, () -> {
            if (operation.equals("writeRegularFile")) {
                CharacterVoiceFiles.remember(files, DIRECTORY, SPEAKER, "voice.mp3");
            } else {
                CharacterVoiceFiles.resolve(files, DIRECTORY, SPEAKER);
            }
        });
        assertSame(failure, actual);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void directoriesNamedLikeAudioAreNotResolvableSources(boolean mapped) throws IOException {
        store.createDirectories(DIRECTORY + "/voice.mp3");
        if (mapped) CharacterVoiceFiles.remember(store, DIRECTORY, "voice.wav", "voice.mp3");

        assertThrows(IOException.class, () -> CharacterVoiceFiles.resolve(store, DIRECTORY, "voice.wav"));
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void symlinksCannotResolveToAnotherBackendsAudio(boolean mapped) throws IOException {
        String outside = upload("voices/backend-b", "private.mp3", MP3);
        store.createDirectories(DIRECTORY);
        // Both ends are synthetic fixtures under root, never real audio or files outside @TempDir.
        Files.createSymbolicLink(root.resolve(DIRECTORY).resolve("voice.mp3"), root.resolve(outside));
        if (mapped) CharacterVoiceFiles.remember(store, DIRECTORY, "voice.wav", "voice.mp3");

        assertThrows(IOException.class, () -> CharacterVoiceFiles.resolve(store, DIRECTORY, "voice.wav"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"file", "dangling-file", "directory", "dangling-directory"})
    void mappingWritesRejectLinksWithoutTouchingTheirTargets(String kind) throws Exception {
        upload(DIRECTORY, "voice.mp3", MP3);
        Path mapping = root.resolve(CharacterVoiceFiles.mappingPath(DIRECTORY, SPEAKER));
        boolean directory = kind.endsWith("directory");
        boolean dangling = kind.startsWith("dangling");
        Path target = root.resolve(directory ? "other-backend-mappings" : "other-backend-mapping");
        Path targetFile = directory ? target.resolve(mapping.getFileName()) : target;
        if (!dangling) {
            Files.createDirectories(targetFile.getParent());
            Files.writeString(targetFile, "must not change");
        }
        Path link = directory ? mapping.getParent() : mapping;
        Files.createDirectories(link.getParent());
        Files.createSymbolicLink(link, target);
        assertThrows(IOException.class, () -> CharacterVoiceFiles.remember(store, DIRECTORY, SPEAKER, "voice.mp3"));
        assertTrue(Files.isSymbolicLink(link));
        if (dangling) assertFalse(Files.exists(target));
        else assertEquals("must not change", Files.readString(targetFile));
    }

    @Test
    void recordingNewSourcePrunesOnlyMappingsWhoseAudioWasRemoved() throws IOException {
        String removed = upload(DIRECTORY, "removed.mp3", MP3);
        String retained = upload(DIRECTORY, "retained.mp3", MP3);
        CharacterVoiceFiles.remember(store, DIRECTORY, "removed.wav", "removed.mp3");
        CharacterVoiceFiles.remember(store, DIRECTORY, "retained.wav", "retained.mp3");
        store.delete(removed);
        upload(DIRECTORY, "replacement.mp3", MP3);
        CharacterVoiceFiles.remember(store, DIRECTORY, "replacement.wav", "replacement.mp3");
        assertFalse(store.exists(CharacterVoiceFiles.mappingPath(DIRECTORY, "removed.wav")));
        assertEquals(retained, CharacterVoiceFiles.resolve(store, DIRECTORY, "retained.wav"));
    }

    private String upload(String directory, String name, byte[] content) throws IOException {
        store.createDirectories(directory);
        String source = directory + "/" + name;
        store.write(source, content);
        return source;
    }

    private static FileStore mockStore() {
        FileStore files = mock(FileStore.class, withSettings().mockMaker(MockMakers.SUBCLASS));
        when(files.isRegularFile(anyString())).thenReturn(true);
        return files;
    }

    private static Stream<String> unsafeNames() {
        return Stream.of(
                ".",
                "..",
                "../voice.mp3",
                "nested/voice.mp3",
                "/voice.mp3",
                "nested\\voice.mp3",
                "..\\voice.mp3",
                "C:\\voice.mp3",
                "voice:stream.mp3",
                "voice\0.mp3",
                "voice\n.mp3",
                "voice\r.mp3",
                "voice\t.mp3",
                "voice\u007f.mp3");
    }
}
