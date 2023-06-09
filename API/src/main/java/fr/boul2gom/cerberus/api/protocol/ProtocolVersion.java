package fr.boul2gom.cerberus.api.protocol;

import fr.boul2gom.cerberus.api.utils.EnumUtils;

public enum ProtocolVersion {

    V1_0_0(1, "1.0.0", "20/05/2023"),
    UNKNOWN(-1, "Unknown", "Unknown");

    private final int id;
    private final String version;
    private final String release;

    private ProtocolVersion(int id, String version, String release) {
        this.id = id;
        this.version = version;
        this.release = release;
    }

    public int getId() {
        return this.id;
    }

    public String getVersion() {
        return this.version;
    }

    public String getRelease() {
        return this.release;
    }

    public boolean is(ProtocolVersion version) {
        return this == version;
    }

    public boolean isNot(ProtocolVersion version) {
        return this != version;
    }

    public static ProtocolVersion parse(byte id) {
        final int parsed = id & 0xFF;

        return EnumUtils.parse(parsed, ProtocolVersion::getId, ProtocolVersion.UNKNOWN);
    }

    public static ProtocolVersion parse(String version) {
        return EnumUtils.parse(version, ProtocolVersion::getVersion, ProtocolVersion.UNKNOWN);
    }

    @Override
    public String toString() {
        return this.version;
    }
}
