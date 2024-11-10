package com.bytedesk.core.channel;

public class ChannelPermissions {

    public static final String CHANNEL_PREFIX = "CHANNEL_";
    // Channel permissions
    public static final String CHANNEL_CREATE = "hasAuthority('CHANNEL_CREATE')";
    public static final String CHANNEL_READ = "hasAuthority('CHANNEL_READ')";
    public static final String CHANNEL_UPDATE = "hasAuthority('CHANNEL_UPDATE')";
    public static final String CHANNEL_DELETE = "hasAuthority('CHANNEL_DELETE')";
    public static final String CHANNEL_EXPORT = "hasAuthority('CHANNEL_EXPORT')";
}