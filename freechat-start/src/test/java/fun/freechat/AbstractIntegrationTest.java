package fun.freechat;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import fun.freechat.util.TestOllamaContainer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.milvus.MilvusContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

import static fun.freechat.service.enums.ModelProvider.OLLAMA;
import static fun.freechat.util.TestCommonUtils.defaultEmbeddingModelFor;
import static fun.freechat.util.TestCommonUtils.defaultModelFor;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "180000")
@ActiveProfiles("local")
@TestPropertySource(properties = "APP_HOME=../local-data/test")
@SuppressWarnings("unused")
public class AbstractIntegrationTest {
    static GenericContainer<?> redis;
    static MySQLContainer<?> mysql;
    static MilvusContainer milvus;
    static GenericContainer<?> tts;
    static TestOllamaContainer ollama;

    static {
        redis = new GenericContainer<>(redisImageName())
                .withExposedPorts(6379)
                .withEnv("REDIS_PASSWORD", "hello1234")
                .withCommand("redis-server", "--appendonly", "yes", "--requirepass", "hello1234")
                .waitingFor(Wait.forSuccessfulCommand("redis-cli -a hello1234 ping || exit 1"));

        mysql = (MySQLContainer<?>) new MySQLContainer(mysqlImageName())
                .withInitScript("sql/schema.sql")
                .withDatabaseName("freechat")
                .withUsername("root")
                .withPassword("hello1234")
                .withUrlParam("useUnicode", "true")
                .withUrlParam("characterEncoding", "utf-8")
                .withExposedPorts(3306)
                .withEnv("MYSQL_ROOT_PASSWORD", "hello1234")
                .waitingFor(Wait.forHealthcheck());

        milvus =  new MilvusContainer(milvusImageName());

        tts = new GenericContainer<>(ttsImageName())
                .withExposedPorts(5002)
                .waitingFor(Wait.forHttp("/ping"))
                .withStartupTimeout(Duration.ofMinutes(3));

        ollama = new TestOllamaContainer(ollamaImageName())
                .withModels(
                        ollamaModelName(Objects.requireNonNull(defaultEmbeddingModelFor(OLLAMA))),
                        ollamaModelName(Objects.requireNonNull(defaultModelFor(OLLAMA))));
    }

    @Autowired
    protected WebTestClient testClient;

    @LocalServerPort
    protected int port;

    protected String getLocalUrl(String path) {
        return "http://localhost:" + port + path;
    }

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        String appHome = System.getProperty("APP_HOME");
        Path dataPath = Path.of(Objects.requireNonNull(appHome), "data");
        try {
            deleteDirectory(dataPath);
        } catch (IOException ignored) {
            // ignored
        }

        redis.start();
        mysql.start();
        milvus.start();
        tts.start();

        registry.add("redis.datasource.url",
                () -> "redis://" + redis.getHost() + ":" + redis.getFirstMappedPort());
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("embedding.milvus.url", milvus::getEndpoint);
        registry.add("tts.baseUrl",
                () -> "http://" + tts.getHost() + ":" + tts.getFirstMappedPort());
        registry.add("disk.workdir", dataPath::toString);
    }

    private static void deleteDirectory(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
                for (Path entry : stream) {
                    deleteDirectory(entry);
                }
            }
        }

        Files.deleteIfExists(path);
    }

    public static TestOllamaContainer ollama() {
        if (!ollama.isRunning()) {
            ollama.start();
            ollama.commitToImage(localOllamaImageName());
        }
        return ollama;
    }

    private static DockerImageName redisImageName() {
        return DockerImageName.parse("redis:8.2.2");
    }

    private static DockerImageName mysqlImageName() {
        return DockerImageName.parse("mysql:8.0.36");
    }

    private static DockerImageName milvusImageName() {
        return DockerImageName.parse("milvusdb/milvus:v2.4.20");
    }

    private static DockerImageName ttsImageName() {
        return DockerImageName.parse("freechatfun/freechat-tts:cpu-latest");
    }

    private static DockerImageName ollamaImageName() {
        DockerImageName dockerImageName = DockerImageName.parse("ollama/ollama:latest");
        String localImageName = localOllamaImageName();
        DockerClient dockerClient = DockerClientFactory.instance().client();
        List<Image> images = dockerClient.listImagesCmd()
                .withReferenceFilter(localImageName)
                .exec();
        if (images.isEmpty()) {
            return dockerImageName;
        }
        return DockerImageName.parse(localImageName)
                .asCompatibleSubstituteFor("ollama/ollama:latest");
    }

    private static String localOllamaImageName() {
        String prefix = System.getenv("TESTCONTAINERS_HUB_IMAGE_NAME_PREFIX");
        return prefix == null ? "tc-freechat/ollama:latest" : prefix + "tc-freechat/ollama:latest";
    }

    private static String ollamaModelName(String modelName) {
        return modelName.split("\\|")[0];
    }
}
