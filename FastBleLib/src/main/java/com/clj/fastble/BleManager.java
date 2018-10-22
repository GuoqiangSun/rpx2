package com.clj.fastble;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.clj.fastble.bluetooth.BleBluetooth;
import com.clj.fastble.conn.BleCharacterCallback;
import com.clj.fastble.conn.BleGattCallback;
import com.clj.fastble.conn.BleRssiCallback;
import com.clj.fastble.conn.BleScanCallback;
import com.clj.fastble.data.ScanResult;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.exception.BlueToothNotEnableException;
import com.clj.fastble.exception.NotFoundDeviceException;
import com.clj.fastble.exception.hanlder.DefaultBleExceptionHandler;
import com.clj.fastble.scan.BleScanRuleConfig;

import java.util.UUID;

public class BleManager {

    private Context mContext;
    private BleBluetooth mBleBluetooth;
    private BleScanRuleConfig mScanRuleConfig;
    private DefaultBleExceptionHandler mBleExceptionHandler;

    public BleManager(Context context) {
        this.mContext = context;

        if (isSupportBle()) {
            if (mBleBluetooth == null) {
                mBleBluetooth = new BleBluetooth(mContext);
            }
        }

        mBleExceptionHandler = new DefaultBleExceptionHandler();
    }

    /**
     * handle Exception Information
     */
    public void handleException(BleException exception) {
        mBleExceptionHandler.handleException(exception);
    }

    /**
     * Configuring scan and connection properties
     *
     * @param scanRuleConfig
     */
    public void initScanRule(BleScanRuleConfig scanRuleConfig) {
        this.mScanRuleConfig = scanRuleConfig;
    }

    /**
     * get the ScanRuleConfig
     *
     * @return
     */
    public BleScanRuleConfig getScanRuleConfig() {
        return mScanRuleConfig;
    }

    /**
     * scan device around
     *
     * @param callback
     * @return
     */
    public boolean scan(BleScanCallback callback) {
        if (!isBlueEnable()) {
            handleException(new BlueToothNotEnableException());
            return false;
        }

        UUID[] serviceUuids = mScanRuleConfig.getServiceUuids();
        String[] deviceNames = mScanRuleConfig.getDeviceNames();
        String deviceMac = mScanRuleConfig.getDeviceMac();
        long timeOut = mScanRuleConfig.getTimeOut();

        return mBleBluetooth.scan(serviceUuids, deviceNames, deviceMac, false, timeOut, callback);
    }

    /**
     * connect a known device
     *
     * @param scanResult
     * @param callback
     */
    public void connect(ScanResult scanResult, BleGattCallback callback) {
        if (!isBlueEnable()) {
            handleException(new BlueToothNotEnableException());
            return;
        }

        if (scanResult == null || scanResult.getDevice() == null) {
            if (callback != null) {
                callback.onConnectError(new NotFoundDeviceException());
            }
        } else {
            if (callback != null) {
                callback.onFoundDevice(scanResult);
            }
            boolean autoConnect = mScanRuleConfig.isAutoConnect();
            mBleBluetooth.connect(scanResult, autoConnect, callback);
        }
    }

    /**
     * scan device then connect
     *
     * @param callback
     */
    public void scanAndConnect(BleGattCallback callback) {
        if (!isBlueEnable()) {
            handleException(new BlueToothNotEnableException());
            return;
        }

        UUID[] serviceUuids = mScanRuleConfig.getServiceUuids();
        String[] deviceNames = mScanRuleConfig.getDeviceNames();
        String deviceMac = mScanRuleConfig.getDeviceMac();
        boolean autoConnect = mScanRuleConfig.isAutoConnect();
        boolean fuzzy = mScanRuleConfig.isFuzzy();
        long timeOut = mScanRuleConfig.getTimeOut();

        mBleBluetooth.scanAndConnect(serviceUuids, deviceNames, deviceMac, fuzzy, autoConnect, timeOut, callback);
    }

    /**
     * cancel scan
     */
    public void cancelScan() {
        mBleBluetooth.stopLeScan();
    }

    /**
     * notify
     *
     * @param uuid_service
     * @param uuid_notify
     * @param callback
     * @return
     */
    public boolean notify(String uuid_service,
                          String uuid_notify,
                          BleCharacterCallback callback) {
        return mBleBluetooth.newBleConnector()
                .withUUIDString(uuid_service, uuid_notify, null)
                .enableCharacteristicNotify(callback, uuid_notify);
    }

    /**
     * indicate
     *
     * @param uuid_service
     * @param uuid_indicate
     * @param callback
     * @return
     */
    public boolean indicate(String uuid_service,
                            String uuid_indicate,
                            BleCharacterCallback callback) {
        return mBleBluetooth.newBleConnector()
                .withUUIDString(uuid_service, uuid_indicate, null)
                .enableCharacteristicIndicate(callback, uuid_indicate);
    }

    /**
     * stop notify, remove callback
     *
     * @param uuid_service
     * @param uuid_notify
     * @return
     */
    public boolean stopNotify(String uuid_service, String uuid_notify) {
        boolean success = mBleBluetooth.newBleConnector()
                .withUUIDString(uuid_service, uuid_notify, null)
                .disableCharacteristicNotify();
        if (success) {
            mBleBluetooth.removeGattCallback(uuid_notify);
        }
        return success;
    }

    /**
     * stop indicate, remove callback
     *
     * @param uuid_service
     * @param uuid_indicate
     * @return
     */
    public boolean stopIndicate(String uuid_service, String uuid_indicate) {
        boolean success = mBleBluetooth.newBleConnector()
                .withUUIDString(uuid_service, uuid_indicate, null)
                .disableCharacteristicIndicate();
        if (success) {
            mBleBluetooth.removeGattCallback(uuid_indicate);
        }
        return success;
    }

    /**
     * write
     *
     * @param uuid_service
     * @param uuid_write
     * @param data
     * @param callback
     * @return
     */
    public boolean write(String uuid_service,
                               String uuid_write,
                               byte[] data,
                               BleCharacterCallback callback) {
        return mBleBluetooth.newBleConnector()
                .withUUIDString(uuid_service, uuid_write, null)
                .writeCharacteristic(data, callback, uuid_write);
    }

    /**
     * read
     *
     * @param uuid_service
     * @param uuid_read
     * @param callback
     * @return
     */
    public boolean read(String uuid_service,
                              String uuid_read,
                              BleCharacterCallback callback) {
        return mBleBluetooth.newBleConnector()
                .withUUIDString(uuid_service, uuid_read, null)
                .readCharacteristic(callback, uuid_read);
    }

    /**
     * read Rssi
     *
     * @param callback
     * @return
     */
    public boolean readRssi(BleRssiCallback callback) {
        return mBleBluetooth.newBleConnector()
                .readRemoteRssi(callback);
    }


    /**
     * refresh Device Cache
     */
    public void refreshDeviceCache() {
        mBleBluetooth.refreshDeviceCache();
    }

    /**
     * close gatt
     */
    public void closeBluetoothGatt() {
        if (mBleBluetooth != null) {
            mBleBluetooth.clearCallback();
            try {
                mBleBluetooth.closeBluetoothGatt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * is support ble?
     *
     * @return
     */
    public boolean isSupportBle() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
                && mContext.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * open bluetooth
     */
    public void enableBluetooth() {
        if (mBleBluetooth != null) {
            mBleBluetooth.enableBluetoothIfDisabled();
        }
    }

    /**
     * close bluetooth
     */
    public void disableBluetooth() {
        if (mBleBluetooth != null) {
            mBleBluetooth.disableBluetooth();
        }
    }

    public boolean isBlueEnable() {
        return mBleBluetooth != null && mBleBluetooth.isBlueEnable();
    }

    public boolean isInScanning() {
        return mBleBluetooth.isInScanning();
    }

    public boolean isConnectingOrConnected() {
        return mBleBluetooth.isConnectingOrConnected();
    }

    public boolean isConnected() {
        return mBleBluetooth.isConnected();
    }

    public boolean isServiceDiscovered() {
        return mBleBluetooth.isServiceDiscovered();
    }

    /**
     * remove callback form a character
     */
    public void stopListenCharacterCallback(String uuid) {
        mBleBluetooth.removeGattCallback(uuid);
    }

    /**
     * remove callback for gatt connect
     */
    public void stopListenConnectCallback() {
        mBleBluetooth.removeConnectGattCallback();
    }

}
