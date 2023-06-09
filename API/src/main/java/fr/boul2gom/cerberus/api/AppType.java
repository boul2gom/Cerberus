package fr.boul2gom.cerberus.api;

import fr.boul2gom.cerberus.api.utils.EnumUtils;

public enum AppType {

    MASTER_PROXY("master-proxy", false),
    MASTER_SERVER("master-server", false),
    MASTER_STANDALONE("master-standalone", false),

    PROXY("proxy", true),
    SERVER("server", true),

    UNKNOWN("unknown", false);

    private final String name;
    private final boolean heartbeat;

    AppType(String name, boolean heartbeat) {
        this.name = name;
        this.heartbeat = heartbeat;
    }

    public String getName() {
        return this.name;
    }

    public boolean isHeartbeat() {
        return this.heartbeat;
    }

    public boolean isSlave() {
        return !this.isMaster();
    }

    public boolean isMaster() {
        return this.name.startsWith("master");
    }

    public String getDisplay() {
        if (this.isMaster()) {
            return "master";
        }

        return this.name;
    }

    public static AppType parse(String name) {
        return EnumUtils.parse(name, AppType::getName, AppType.UNKNOWN);
    }
}
