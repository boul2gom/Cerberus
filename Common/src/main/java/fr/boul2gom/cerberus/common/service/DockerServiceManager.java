package fr.boul2gom.cerberus.common.service;

import fr.boul2gom.cerberus.api.configuration.ConfigurationKey;
import fr.boul2gom.cerberus.api.configuration.IConfiguration;
import fr.boul2gom.cerberus.api.service.IServiceManager;
import fr.boul2gom.cerberus.common.support.service.DockerSupport;
import io.github.nullptr.tools.docker.DockerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DockerServiceManager implements IServiceManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(IServiceManager.class);

    private final DockerSupport support;

    public DockerServiceManager(DockerSupport support) {
        this.support = support;
    }
}
