package fr.boul2gom.cerberus.api.configuration.driver;

import fr.boul2gom.cerberus.api.utils.EnumUtils;

public enum ProtocolDriver {

    REDIS("Redis"),
    RABBITMQ("RabbitMQ");

    private final String name;

    ProtocolDriver(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean is(ProtocolDriver driver) {
        return this == driver;
    }

    public boolean isNot(ProtocolDriver driver) {
        return this != driver;
    }

    public static ProtocolDriver parse(String driver) {
        return EnumUtils.parse(driver, ProtocolDriver::getName, ProtocolDriver.REDIS);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
