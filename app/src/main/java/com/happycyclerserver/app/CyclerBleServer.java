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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.ParcelUuid;

import com.happycyclerserver.util.Help;
import com.happycyclerserver.util.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CyclerBleServer {

    private static final CyclerBleServer sInstance = new CyclerBleServer();

    private final BluetoothManager mBTManager;
    private final BluetoothAdapter mBTAdapter;
    private final BluetoothGattServer mGattServer;
    private final CyclerBleServerCallback mGattServerCallback;
    private final BluetoothLeAdvertiser mBTAdvertiser;
    private BleService mBoundedService;
    private boolean mRunning = false;

    public static CyclerBleServer get() {
        return sInstance;
    }

    private CyclerBleServer() {
        mBTManager = (BluetoothManager) Help.appCtx().getSystemService(Context.BLUETOOTH_SERVICE);
        mBTAdapter = mBTManager.getAdapter();
        mGattServerCallback = new CyclerBleServerCallback();
        mGattServer = mBTManager.openGattServer(Help.appCtx(), mGattServerCallback);
        mGattServerCallback.setGattServer(mGattServer);
        mGattServer.addService(createCyclerGattService());
        mBTAdvertiser = mBTAdapter.getBluetoothLeAdvertiser();
        Help.appCtx().bindService(
                new Intent(Help.appCtx(), BleService.class),
                mConnection,
                Context.BIND_AUTO_CREATE);
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void start() {
        if (mRunning) {
            return;
        }
        mRunning = true;
        mBTAdvertiser.startAdvertising(createAdvSettings(), createAdvData(), mAdvCallback);
        if (mBoundedService != null) {
            mBoundedService.startForeground();
        }
    }

    public void stop() {
        if (!mRunning) {
            return;
        }
        mRunning = false;
        mBTAdvertiser.stopAdvertising(mAdvCallback);
        if (mBoundedService != null) {
            mBoundedService.stopForeground();
        }
    }


// Cycler Service

    private static BluetoothGattService createCyclerGattService() {
        BluetoothGattService gattService = new BluetoothGattService(
                ServerUUID.SERVICE_UUID,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        gattService.addCharacteristic(getDirectionCharacteristic());
        return gattService;
    }

    private static BluetoothGattCharacteristic getDirectionCharacteristic() {
        return new BluetoothGattCharacteristic(
                ServerUUID.CHAR_DIRECTION_UUID,
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
        uuidList.add(ServerUUID.SERVICE_PARCELUUID);
        builder.setServiceUuids(uuidList);
        return builder.build();
    }

    private static AdvertiseSettings createAdvSettings() {
        AdvertiseSettings.Builder builder = new AdvertiseSettings.Builder();
        builder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        builder.setType(AdvertiseSettings.ADVERTISE_TYPE_CONNECTABLE);
        builder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY);
        return builder.build();
    }

// Service

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Logger.v(this, "onServiceConnected");
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mBoundedService = ((BleService.LocalBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            Logger.v(this, "onServiceDisconnected");
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundedService = null;
        }
    };
}
