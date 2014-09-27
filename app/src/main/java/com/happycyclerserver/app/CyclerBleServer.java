package com.happycyclerserver.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisementData;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.ParcelUuid;

import com.happycyclerserver.util.Help;
import com.happycyclerserver.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CyclerBleServer {

    private static final CyclerBleServer sInstance = new CyclerBleServer();
    public static final String CYCLER_GATT_SERVICE = "00002a19-0000-1000-8000-00805f9b34fb";
    public static final String DIRECTION_CHARACTERISTIC = "00002a06-0000-1000-8000-00805f9b34fb";

    private final BluetoothManager mBTManager;
    private final BluetoothAdapter mBTAdapter;
    private final BluetoothGattServer mGattServer;
    private final BluetoothLeAdvertiser mBTAdvertiser;

    public static CyclerBleServer get() {
        return sInstance;
    }

    private CyclerBleServer() {
        mBTManager = (BluetoothManager) Help.appCtx().getSystemService(Context.BLUETOOTH_SERVICE);
        mBTAdapter = mBTManager.getAdapter();
        mGattServer = mBTManager.openGattServer(this, mGattServerCallback);
        mGattServer.addService(createCyclerGattService());
        mBTAdvertiser = mBTAdapter.getBluetoothLeAdvertiser();
    }

    public void start() {
        mBTAdvertiser.startAdvertising(createAdvSettings(), createAdvData(), mAdvCallback);
    }

    public void stop() {
        mBTAdvertiser.stopAdvertising(mAdvCallback);
    }


// Cycler Service

    private static BluetoothGattService createCyclerGattService() {
        BluetoothGattService gattService = new BluetoothGattService(
                UUID.fromString(CYCLER_GATT_SERVICE),
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        gattService.addCharacteristic(getDirectionCharacteristic());
        return gattService;
    }

    private static BluetoothGattCharacteristic getDirectionCharacteristic() {
        return new BluetoothGattCharacteristic(
                UUID.fromString(DIRECTION_CHARACTERISTIC),
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PERMISSION_WRITE);
    }

// Advertisement

    private AdvertiseCallback mAdvCallback = new AdvertiseCallback() {

        @Override
        public void onSuccess(AdvertiseSettings settingsInEffect) {
            if (settingsInEffect != null) {
                Logger.v(this, "onSuccess TxPowerLv="
                        + settingsInEffect.getTxPowerLevel()
                        + " mode=" + settingsInEffect.getMode()
                        + " type=" + settingsInEffect.getType());
            } else {
                Logger.v(this, "onSuccess, settingInEffect is null");
            }
        }

        @Override
        public void onFailure(int errorCode) {
            Logger.e(this, "onFailure errorCode=" + errorCode);
        }
    };

    private static AdvertisementData createAdvData() {
        AdvertisementData.Builder builder = new AdvertisementData.Builder();
        builder.setIncludeTxPowerLevel(false);

        List<ParcelUuid> uuidList = new ArrayList<>();
        uuidList.add(ParcelUuid.fromString(BleUuid.SERVICE_DEVICE_INFORMATION));
        uuidList.add(ParcelUuid.fromString(BleUuid.SERVICE_IMMEDIATE_ALERT));
        builder.setServiceUuids(uuidList);

        return builder.build();
    }

    private static AdvertiseSettings createAdvSettings() {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);

        builder.setType(AdvertiseSettings.ADVERTISE_TYPE_CONNECTABLE);
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        return builder.build();
    }

}
