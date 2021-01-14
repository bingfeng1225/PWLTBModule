package cn.qd.peiwen.ltb;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import cn.haier.bio.medical.ltb.ILTBListener;
import cn.haier.bio.medical.ltb.LTBManager;
import cn.qd.peiwen.logger.PWLogger;
import cn.qd.peiwen.serialport.PWSerialPort;

public class MainActivity extends AppCompatActivity implements ILTBListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String path = "/dev/ttyS4";
        if ("magton".equals(Build.MODEL)) {
            path = "/dev/ttyS2";
        }
        LTBManager.getInstance().init(path);
        LTBManager.getInstance().changeListener(this);
        LTBManager.getInstance().enable();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LTBManager.getInstance().release();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:

                break;
            case R.id.button2:
                LTBManager.getInstance().enable();
                break;
            case R.id.button3:
                LTBManager.getInstance().disable();
                break;
            case R.id.button4:
                LTBManager.getInstance().release();
                break;
        }
    }

    @Override
    public void onLTBReady() {
        PWLogger.debug("LTBSerialPort Ready");
    }

    @Override
    public void onLTBConnected() {
        PWLogger.debug("LTBSerialPort Connected");
    }

    @Override
    public void onLTBSwitchWriteModel() {
        PWLogger.debug("LTBSerialPort SwitchWriteModel");
        if (!"magton".equals(Build.MODEL)) {
            PWSerialPort.writeFile("/sys/class/gpio/gpio24/value", "0");
        } else {
            PWSerialPort.writeFile("/sys/class/misc/sunxi-acc/acc/sochip_acc", "1");
        }
    }

    @Override
    public void onLTBSwitchReadModel() {
        PWLogger.debug("LTBSerialPort SwitchReadModel");
        if (!"magton".equals(Build.MODEL)) {
            PWSerialPort.writeFile("/sys/class/gpio/gpio24/value", "1");
        } else {
            PWSerialPort.writeFile("/sys/class/misc/sunxi-acc/acc/sochip_acc", "0");
        }
    }

    @Override
    public void onLTBPrint(String message) {
        PWLogger.debug("" + message);
    }

    @Override
    public void onLTBSystemChanged(int type) {
        PWLogger.debug("LTBSerialPort SystemChanged: " + type);
    }

    @Override
    public byte[] packageLTBResponse(int type) {
        byte[] buffer = null;
        if (type == 0xA4) {
            buffer = new byte[]{
                    (byte)0x24, (byte)0xFA, (byte)0x88, (byte)0xFA, (byte)0xC0, (byte)0xF9,
                    (byte)0x01, (byte)0x00, (byte)0x01, (byte)0x00, (byte)0x1E, (byte)0x00,
                    (byte)0x05, (byte)0x00, (byte)0x05, (byte)0x00, (byte)0x00, (byte)0x00,
                    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xEC, (byte)0xFA,
                    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00
            };
        } else {
            buffer = new byte[]{
                    (byte)0x1E, (byte)0x00, (byte)0x05, (byte)0x00, (byte)0x00, (byte)0x00,
                    (byte)0x00, (byte)0x00, (byte)0x23, (byte)0x00, (byte)0x5A, (byte)0x00,
                    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
                    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00
            };
        }
        return buffer;
    }


    @Override
    public void onLTBException(Throwable throwable) {
        PWLogger.error(throwable);
    }

    @Override
    public void onLTBDataChanged(byte[] entity) {
        PWLogger.debug("LTBSerialPort DataChanged");
    }
}
