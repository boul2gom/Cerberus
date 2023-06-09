package fr.boul2gom.cerberus.api.configuration.driver;

import fr.boul2gom.cerberus.api.utils.EnumUtils;

public enum DatabaseDriver {

    REDIS("Redis"),
    MONGODB("MongoDB");

    private final String name;

    DatabaseDriver(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean is(DatabaseDriver driver) {
        return this == driver;
    }

    public boolean isNot(DatabaseDriver driver) {
        return this != driver;
    }

    public static DatabaseDriver parse(String driver) {
        return EnumUtils.parse(driver, DatabaseDriver::getName, DatabaseDriver.REDIS);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
