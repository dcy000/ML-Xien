package com.gcml.lib_idcard_cvr;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.huashi.bluetooth.HSBlueApi;
import com.huashi.bluetooth.HsInterface;
import com.huashi.bluetooth.IDCardInfo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;


public class BluetoothController {
    private HSBlueApi api;
    private int ret = -1;
    private boolean isConnected;
    private Thread readIdCardThread;
    private Handler handler;
    private String filepath = "";
    private static final String TAG = "BluetoothController";

    public void init(final Context context) {
        handler = new Handler(Looper.getMainLooper());
        filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wltlib";// 授权目录
        api = new HSBlueApi(context);
        ret = api.init();
        if (ret == -1) {
            api.unInit();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    init(context);
                }
            }, 3000);
        }
    }

    public void scan(CVRScan cvrInterface) {
        if (cvrInterface == null) {
            return;
        }
        this.cvrInterface = cvrInterface;
        if (api == null) {
            return;
        }
        api.setmInterface(new HsInterface() {
            @Override
            public void reslut2Devices(Map<String, List<BluetoothDevice>> map) {
                List<BluetoothDevice> noBind = map.get("notBind");
                if (noBind == null || noBind.size() == 0) {
                    return;
                }
                BluetoothDevice bluetoothDevice = noBind.get(noBind.size() - 1);
                String name = bluetoothDevice.getName();
                if (TextUtils.isEmpty(name)) {
                    return;
                }
                if (name.startsWith("CVR")) {
                    BluetoothController.this.cvrInterface.scanedDevice(bluetoothDevice);
                }
            }
        });
        api.scanf();
    }

    public void connect(String macAddress, CVRConnect cvrConnect) {
        if (cvrConnect == null) {
           return;
        }
        this.cvrConnect = cvrConnect;
        if (api == null) {
            return;
        }

        ret = api.connect(macAddress);
        if (ret == 0) {
            BluetoothController.this.cvrConnect.connect(macAddress, isConnected = true);
        } else {
            BluetoothController.this.cvrConnect.connect(macAddress, isConnected = false);
        }
    }

    public void readIdCard(CVRRead cvrRead) {
        if (cvrRead == null) {
            return;
        }
        this.cvrRead = cvrRead;
        if (api == null) {
            return;
        }
        readIdCardThread = new Thread() {
            @Override
            public void run() {
                //先唤醒设备
                api.weak();
                try {
                    sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                api.aut();
                ret = api.Authenticate(200);
                IDCardInfo ic = new IDCardInfo();
                ret = api.Read_Card(ic, 1500);
                if (ret == 1) {
                    try {
                        ret = api.Unpack(ic.getwltdata());// 照片解码
                        if (ret != 0) {// 解析头像失败
                            sendInfo(null, null, ic);
                            return;
                        }
                        FileInputStream fis = new FileInputStream(filepath + "/zp.bmp");
                        Bitmap bmp = BitmapFactory.decodeStream(fis);
                        fis.close();
                        sendInfo(bmp, filepath + "/zp.bmp", ic);
                    } catch (FileNotFoundException e) {
                        sendInfo(null, null, ic);
                    } catch (IOException e) {
                        sendInfo(null, null, ic);
                    } catch (Exception e) {
                        sendInfo(null, null, ic);
                    }

                } else {
                    if (handler == null) return;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            BluetoothController.this.cvrRead.readFail();
                        }
                    });
                }
            }
        };
        readIdCardThread.start();

    }

    private void sendInfo(final Bitmap bitmap, final String headFilePath, final IDCardInfo ic) {
        if (handler == null) return;
        handler.post(new Runnable() {
            @Override
            public void run() {
                BluetoothController.this.cvrRead.readSuccess(bitmap, headFilePath, ic);
            }
        });
    }

    public void clear() {
        handler.removeCallbacksAndMessages(null);
        handler = null;
        if (readIdCardThread != null) {
            readIdCardThread.interrupt();
        }
        readIdCardThread = null;
        cvrInterface = null;
        cvrConnect = null;
        cvrRead = null;
        if (api != null) {
            api.unInit();
        }
        api = null;

    }

    public boolean isConnected() {
        return isConnected;
    }

    private CVRScan cvrInterface;

    public interface CVRScan {
        void scanedDevice(BluetoothDevice device);
    }

    private CVRConnect cvrConnect;

    public interface CVRConnect {
        void connect(String macAddress, boolean isSuccess);
    }

    private CVRRead cvrRead;

    public interface CVRRead {
        void readSuccess(Bitmap head, String headFilePath, IDCardInfo idCardInfo);

        void readFail();
    }
}
