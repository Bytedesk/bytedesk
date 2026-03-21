package com.bytedesk.core.ip;

public interface IpLocationProvider {

    String getName();

    boolean isAvailable();

    IpLocationResult locate(String ip);
}