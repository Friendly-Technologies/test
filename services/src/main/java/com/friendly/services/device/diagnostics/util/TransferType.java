package com.friendly.services.device.diagnostics.util;

public enum TransferType {
    DOWNLOAD("Download", "Download time in seconds", "Download file size in MB", "Average download speed in Mbps"),
    UPLOAD("Upload", "Upload time in seconds", "Upload file size in MB", "Average upload speed in Mbps");

    private final String name;
    private final String time;
    private final String size;
    private final String speed;

    TransferType(String name, String time, String size, String speed) {
        this.name = name;
        this.time = time;
        this.size = size;
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getSize() {
        return size;
    }

    public String getSpeed() {
        return speed;
    }
}
