package fr.boul2gom.cerberus.api.configuration.driver;

import fr.boul2gom.cerberus.api.utils.EnumUtils;

public enum CacheDriver {

    NONE("None"),
    REDIS("Redis"),
    MEMCACHED("Memcached");

    private final String name;

    CacheDriver(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean is(CacheDriver driver) {
        return this == driver;
    }

    public boolean isNot(CacheDriver driver) {
        return this != driver;
    }

    public static CacheDriver parse(String driver) {
        return EnumUtils.parse(driver, CacheDriver::getName, CacheDriver.NONE);
    }

    @Override
    public String toString() {
        return this.name;
    }
}
