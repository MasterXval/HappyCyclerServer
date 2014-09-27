package com.happycyclerserver.app;

import android.os.ParcelUuid;

import java.util.UUID;

public class ServerUUID {

    private static final String SERVICE_STRING = "00002a19-0000-1000-8000-00805f9b34fb";
    private static final String CHAR_DIRECTION_STRING = "00002a06-0000-1000-8000-00805f9b34fb";

    public static final UUID SERVICE_UUID = UUID.fromString(SERVICE_STRING);
    public static final UUID CHAR_DIRECTION_UUID = UUID.fromString(CHAR_DIRECTION_STRING);

    public static final ParcelUuid SERVICE_PARCELUUID = ParcelUuid.fromString(SERVICE_STRING);
    public static final ParcelUuid CHAR_DIRECTION_PARCELUUID = ParcelUuid.fromString(CHAR_DIRECTION_STRING);
}
