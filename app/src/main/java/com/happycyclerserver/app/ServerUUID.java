package com.happycyclerserver.app;

import android.os.ParcelUuid;

import java.util.UUID;

public class ServerUUID {

    private static final String SERVICE_STRING = "01010101-0101-0101-0101-010101010101";
    private static final String CHAR_DIRECTION_STRING = "01010101-0101-0101-0101-010101524742";

    public static final UUID SERVICE_UUID = UUID.fromString(SERVICE_STRING);
    public static final UUID CHAR_DIRECTION_UUID = UUID.fromString(CHAR_DIRECTION_STRING);

    public static final ParcelUuid SERVICE_PARCELUUID = ParcelUuid.fromString(SERVICE_STRING);
}
