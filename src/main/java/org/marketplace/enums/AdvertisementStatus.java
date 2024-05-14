package org.marketplace.enums;

public enum AdvertisementStatus {
    ACTIVE(0, "Active"),
    INACTIVE(-1, "Inactive"),
    DELETED(-2, "Deleted"),
    EDITED(-3, "Edited"),
    BUYER(Integer.MAX_VALUE, "Buyer");

    private final int code;
    private final String description;

    AdvertisementStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static AdvertisementStatus fromCode(int code) {
        for (AdvertisementStatus status : AdvertisementStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return null;
    }

    public static AdvertisementStatus fromName(String name) {
        for (AdvertisementStatus status : AdvertisementStatus.values()) {
            if (status.name().equals(name)) {
                return status;
            }
        }
        return null;
    }

    public static AdvertisementStatus fromOrdinal(int ordinal) {
        for (AdvertisementStatus status : AdvertisementStatus.values()) {
            if (status.ordinal() == ordinal) {
                return status;
            }
        }
        return null;
    }
}
