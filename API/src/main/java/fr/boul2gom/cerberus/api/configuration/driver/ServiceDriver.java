package fr.boul2gom.cerberus.api.configuration.driver;

import fr.boul2gom.cerberus.api.utils.EnumUtils;

public enum ServiceDriver {

    BARE_METAL("Bare metal"),
    DOCKER("Docker"),
    KUBERNETES("Kubernetes");

    private final String name;

    ServiceDriver(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean is(ServiceDriver driver) {
        return this == driver;
    }

    public boolean isNot(ServiceDriver driver) {
        return this != driver;
    }

    public static ServiceDriver parse(String driver) {
        return EnumUtils.parse(driver, ServiceDriver::getName, ServiceDriver.BARE_METAL);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
