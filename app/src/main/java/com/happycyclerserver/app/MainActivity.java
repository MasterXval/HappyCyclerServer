package com.happycyclerserver.app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisementData;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends Activity {

    private static final String TAG = "aBeacon";
    private BluetoothManager mBTManager;
    private BluetoothAdapter mBTAdapter;
    private BluetoothGattServer mGattServer;
    private BluetoothLeAdvertiser mBTAdvertiser;
    private boolean mIsAdvertising = false;
    private byte[] mAlertLevel = new byte[] {
            (byte) 0x00
    };


    private BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {
        public void onServiceAdded(int status, BluetoothGattService service) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "onServiceAdded status=GATT_SUCCESS service="
                        + service.getUuid().toString());
            } else {
                Log.d(TAG, "onServiceAdded status!=GATT_SUCCESS");
            }
        };

        public void onConnectionStateChange(android.bluetooth.BluetoothDevice device, int status,
                                            int newState) {
            // Log.d(TAG, "onConnectionStateChange status=" + status + "->" + newState);
        };


        public void onCharacteristicReadRequest(android.bluetooth.BluetoothDevice device,
                                                int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            Log.d(TAG, "onCharacteristicReadRequest requestId=" + requestId + " offset=" + offset);
            if (characteristic.getUuid().equals(
                    UUID.fromString(BleUuid.CHAR_MANUFACTURER_NAME_STRING))) {
                Log.d(TAG, "CHAR_MANUFACTURER_NAME_STRING");
                characteristic.setValue("Name:Hoge");
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                        characteristic.getValue());
            } else if (characteristic.getUuid().equals(
                    UUID.fromString(BleUuid.CHAR_MODEL_NUMBER_STRING))) {
                Log.d(TAG, "CHAR_MODEL_NUMBER_STRING");
                characteristic.setValue("Model:Redo");
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                        characteristic.getValue());
            } else if (characteristic.getUuid().equals(
                    UUID.fromString(BleUuid.CHAR_SERIAL_NUMBEAR_STRING))) {
                Log.d(TAG, "CHAR_SERIAL_NUMBEAR_STRING");
                characteristic.setValue("Serial:777");
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                        characteristic.getValue());
            } else if (characteristic.getUuid().equals(
                    UUID.fromString(BleUuid.CHAR_ALERT_LEVEL))) {
                Log.d(TAG, "CHAR_ALERT_LEVEL");
                characteristic.setValue(mAlertLevel);
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                        characteristic.getValue());
            }
        };

        public void onCharacteristicWriteRequest(android.bluetooth.BluetoothDevice device,
                                                 int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite,
                                                 boolean responseNeeded, int offset, byte[] value) {
            Log.d(TAG, "onCharacteristicWriteRequest requestId=" + requestId + " preparedWrite="
                    + Boolean.toString(preparedWrite) + " responseNeeded="
                    + Boolean.toString(responseNeeded) + " offset=" + offset);
            if (characteristic.getUuid().equals(
                    UUID.fromString(BleUuid.CHAR_ALERT_LEVEL))) {
                Log.d(TAG, "CHAR_ALERT_LEVEL");
                if (value != null && value.length > 0) {
                    Log.d(TAG, "value.length=" + value.length);
                    mAlertLevel[0] = value[0];
                } else {
                    Log.d(TAG, "invalid value written");
                }
                mGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, offset,
                        null);
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        startAdvertise();
    }

    @Override
    protected void onStop() {
        super.onStop();

        stopAdvertise();
    }

    private void init() {
        // BLE check
        if (!BleUtil.isBLESupported(this)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // BT check
        mBTManager = BleUtil.getManager(this);
        if (mBTManager != null) {
            mBTAdapter = mBTManager.getAdapter();
        }
        if (mBTAdapter == null) {
            Toast.makeText(this, R.string.bt_unavailable, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void stopAdvertise() {
        if (mGattServer != null) {
            mGattServer.clearServices();
            mGattServer.close();
            mGattServer = null;
        }
        if (mBTAdvertiser != null) {
            mBTAdvertiser.stopAdvertising(mAdvCallback);
        }
        mIsAdvertising = false;
        setProgressBarIndeterminateVisibility(false);
    }

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
