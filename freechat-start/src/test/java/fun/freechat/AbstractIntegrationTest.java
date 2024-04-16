package fun.freechat;

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
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "20000")
@ActiveProfiles("local")
@TestPropertySource(properties = "APP_HOME=${TMPDIR}")
@Sql({"classpath:/sql/data.sql"})
@SuppressWarnings("unused")
public class AbstractIntegrationTest {
    static GenericContainer<?> redis;
    static MySQLContainer<?> mysql;

    static {
        redis = new GenericContainer<>(DockerImageName.parse("redis:latest"))
                .withExposedPorts(6379)
                .withEnv("REDIS_PASSWORD", "hello1234")
                .withCommand("redis-server", "--appendonly", "yes", "--requirepass", "hello1234");

        mysql = (MySQLContainer<?>) new MySQLContainer(DockerImageName.parse("mysql:latest"))
                .withInitScript("sql/schema.sql")
                .withDatabaseName("freechat")
                .withUsername("root")
                .withPassword("hello1234")
                .withUrlParam("useUnicode", "true")
                .withUrlParam("characterEncoding", "utf-8")
                .withExposedPorts(3306)
                .withEnv("MYSQL_ROOT_PASSWORD", "hello1234");
    }

    @Autowired
    protected WebTestClient testClient;

    @LocalServerPort
    protected int port;

    protected String getLocalUrl(String path) {
        return "http://localhost:" + port + path;
    }

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        redis.start();
        mysql.start();
        registry.add("redis.datasource.url",
                () -> "redis://" + redis.getHost() + ":" + redis.getFirstMappedPort());
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
    }
}
