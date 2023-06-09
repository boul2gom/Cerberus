package fr.boul2gom.cerberus.common.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

public class LoggerUtils {

    public static void disable() {
        Configurator.setAllLevels("org.mongodb.driver.cluster", Level.OFF);
        Configurator.setAllLevels("org.mongodb.driver.client", Level.OFF);
        Configurator.setAllLevels("io.lettuce.core", Level.OFF);
        Configurator.setAllLevels("io.netty", Level.OFF);
        Configurator.setAllLevels("reactor", Level.OFF);
    }
}
