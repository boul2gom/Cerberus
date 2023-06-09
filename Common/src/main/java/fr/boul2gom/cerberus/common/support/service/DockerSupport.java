package fr.boul2gom.cerberus.common.support.service;

import fr.boul2gom.cerberus.api.CerberusAPI;
import fr.boul2gom.cerberus.api.configuration.ConfigurationKey;
import io.github.nullptr.tools.docker.DockerManager;

public class DockerSupport {
    
    private final DockerManager docker;
    
    public DockerSupport(CerberusAPI api) {
        final String host = api.getConfiguration().get(ConfigurationKey.DOCKER_HOST);
        final String version = api.getConfiguration().get(ConfigurationKey.DOCKER_API_VERSION);
        final String registry = api.getConfiguration().get(ConfigurationKey.DOCKER_REGISTRY_URL);
        final String username = api.getConfiguration().get(ConfigurationKey.DOCKER_REGISTRY_USERNAME);
        final String email = api.getConfiguration().get(ConfigurationKey.DOCKER_REGISTRY_EMAIL);
        final String password = api.getConfiguration().get(ConfigurationKey.DOCKER_REGISTRY_PASSWORD);

        final DockerManager.Builder builder = new DockerManager.Builder()
                .withHost(host);

        if (version != null) builder.withApiVersion(version);
        if (registry != null && username != null && email != null && password != null) {
            builder.withRegistry(registry, username, email, password);
        }

        this.docker = builder.build();
    }

    public DockerManager getDocker() {
        return this.docker;
    }
}
