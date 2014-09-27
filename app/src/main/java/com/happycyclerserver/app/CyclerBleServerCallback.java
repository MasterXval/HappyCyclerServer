package com.happycyclerserver.app;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;

import com.happycyclerserver.util.Logger;

import java.util.UUID;

public class CyclerBleServerCallback extends BluetoothGattServerCallback {

    private BluetoothGattServer mGattServer;

    public void setGattServer(BluetoothGattServer gattServer) {
        mGattServer = gattServer;
    }

    public void onServiceAdded(int status, BluetoothGattService service) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Logger.v(this, "onServiceAdded status=GATT_SUCCESS service="
                    + service.getUuid().toString());
        } else {
            Logger.v(this, "onServiceAdded status!=GATT_SUCCESS");
        }
    }

    public void onConnectionStateChange(
            BluetoothDevice device, int status, int newState) {

        Logger.v(this, "onConnectionStateChange()");
        switch (status) {
            case BluetoothProfile.STATE_CONNECTED:
            case BluetoothProfile.STATE_CONNECTING:
                Logger.v(this, "connected or connecting");
                break;
            case BluetoothProfile.STATE_DISCONNECTED:
            case BluetoothProfile.STATE_DISCONNECTING:
                Logger.v(this, "deconnected or deconnecting");
                break;
            default:
                Logger.v(this, "other");
        }
    }


    public void onCharacteristicReadRequest(
            BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
        Logger.v(this, "onCharacteristicReadRequest requestId=" + requestId + " offset=" + offset);
    }

    public void onCharacteristicWriteRequest(
            BluetoothDevice device, int requestId,
            BluetoothGattCharacteristic characteristic, boolean preparedWrite,
            boolean responseNeeded, int offset, byte[] value) {

        Logger.v(this, "onCharacteristicWriteRequest requestId=" + requestId + " preparedWrite="
                + Boolean.toString(preparedWrite) + " responseNeeded="
                + Boolean.toString(responseNeeded) + " offset=" + offset);

        if (characteristic.getUuid().equals(ServerUUID.CHAR_DIRECTION_UUID)) {
            byte[] response = setDirection(value);
            mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, response);
        }
        mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, offset, null);
    }

    private byte[] setDirection(byte[] value) {
        if (value.length > 1) {
            switch (value[0]) {
                case 0:
                    Logger.v(this, "TRALALA LEFT");
                    break;
                case 1:
                    Logger.v(this, "TRALALA RIGHT");
                    break;
                default:
                    Logger.v(this, "TRALALA STOPPED");
            }
        }
        return null;
    }
}
