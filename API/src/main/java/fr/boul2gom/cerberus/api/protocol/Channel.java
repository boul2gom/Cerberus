package fr.boul2gom.cerberus.api.protocol;

import fr.boul2gom.cerberus.api.utils.EnumUtils;

public enum Channel {

    GLOBAL("cerberus-global"),
    EVENTS("cerberus-events"),

    UNKNOWN("unknown");

    private final String channel;

    Channel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return this.channel;
    }

    public boolean is(Channel channel) {
        return this == channel;
    }

    public boolean isNot(Channel channel) {
        return this != channel;
    }

    public static Channel parse(String channel) {
        return EnumUtils.parse(channel, Channel::getChannel, Channel.UNKNOWN);
    }
}
