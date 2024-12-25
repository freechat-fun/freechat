package fun.freechat.util;

import com.github.dockerjava.api.command.InspectContainerResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.ollama.OllamaContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class TestOllamaContainer extends OllamaContainer {
    private List<String> models;

    public TestOllamaContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        super.waitingFor(Wait.forHttp("/"));
    }

    public TestOllamaContainer withModels(String... models) {
        this.models = Arrays.asList(models);
        return this;
    }

    @Override
    protected void containerIsStarted(InspectContainerResponse containerInfo) {
        if (CollectionUtils.isNotEmpty(models)) {
            for (String model: models) {
                try {
                    log.info("Start pulling the '{}' model ... would take several minutes ...", model);
                    Container.ExecResult result = execInContainer("ollama", "pull", model);
                    if (result.getExitCode() != 0) {
                        throw new IOException(result.getStderr());
                    }
                    log.info("Model pulling competed!");
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException("Error pulling model", e);
                }
            }
        }
    }
}
