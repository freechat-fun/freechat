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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.milvus.MilvusContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static fun.freechat.service.enums.ModelProvider.OLLAMA;
import static fun.freechat.util.TestCommonUtils.defaultEmbeddingModelFor;
import static fun.freechat.util.TestCommonUtils.defaultModelFor;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "60000")
@ActiveProfiles("local")
@TestPropertySource(properties = "APP_HOME=${TMPDIR}")
@Sql({"classpath:/sql/data.sql"})
@SuppressWarnings("unused")
public class AbstractIntegrationTest {
    static GenericContainer<?> redis;
    static MySQLContainer<?> mysql;
    static MilvusContainer milvus;
    static TestOllamaContainer ollama;

    static {
        redis = new GenericContainer<>(redisImageName())
                .withExposedPorts(6379)
                .withEnv("REDIS_PASSWORD", "hello1234");

        mysql = (MySQLContainer<?>) new MySQLContainer(mysqlImageName())
                .withInitScript("sql/schema.sql")
                .withDatabaseName("freechat")
                .withUsername("root")
                .withPassword("hello1234")
                .withUrlParam("useUnicode", "true")
                .withUrlParam("characterEncoding", "utf-8")
                .withExposedPorts(3306)
                .withEnv("MYSQL_ROOT_PASSWORD", "hello1234");

        milvus =  new MilvusContainer(milvusImageName());
//                .withCommand( "run", "standalone");

        ollama = new TestOllamaContainer(ollamaImageName())
                .withModels(
                        ollamaModelName(defaultEmbeddingModelFor(OLLAMA)),
                        ollamaModelName(defaultModelFor(OLLAMA)));
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
        redis.start();
        mysql.start();
        milvus.start();
        registry.add("redis.datasource.url",
                () -> "redis://" + redis.getHost() + ":" + redis.getFirstMappedPort());
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("embedding.milvus.url", milvus::getEndpoint);
    }

    public static TestOllamaContainer ollama() {
        if (!ollama.isRunning()) {
            ollama.start();
            ollama.commitToImage(localOllamaImageName());
        }
        return ollama;
    }

    // In order to unify with the k8s environment, use the bitnami images for testing.
    private static DockerImageName redisImageName() {
        return DockerImageName.parse("bitnami/redis:latest")
                .asCompatibleSubstituteFor("redis");
    }

    private static DockerImageName mysqlImageName() {
        return DockerImageName.parse("bitnami/mysql:latest")
                .asCompatibleSubstituteFor("mysql");
    }

    private static DockerImageName milvusImageName() {
//        return DockerImageName.parse("bitnami/milvus:latest")
//                .asCompatibleSubstituteFor("milvusdb/milvus");
        return DockerImageName.parse("milvusdb/milvus:latest");
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
