package cn.haier.bio.medical.ltb;

import cn.qd.peiwen.pwtools.EmptyUtils;

/***
 * 超低温变频、T系列、双系统主控板通讯
 *
 */
public class LTBManager {
    private LTBSerialPort serialPort;
    private static LTBManager manager;

    public static LTBManager getInstance() {
        if (manager == null) {
            synchronized (LTBManager.class) {
                if (manager == null)
                    manager = new LTBManager();
            }
        }
        return manager;
    }

    private LTBManager() {

    }

    public void init(ILTBListener listener) {
        if(EmptyUtils.isEmpty(this.serialPort)){
            this.serialPort = new LTBSerialPort();
            this.serialPort.init(listener);
        }
    }

    public void enable() {
        if(EmptyUtils.isNotEmpty(this.serialPort)){
            this.serialPort.enable();
        }
    }

    public void disable() {
        if(EmptyUtils.isNotEmpty(this.serialPort)){
            this.serialPort.disable();
        }
    }

    public void release() {
        if(EmptyUtils.isNotEmpty(this.serialPort)){
            this.serialPort.disable();
            this.serialPort = null;
        }
    }
}

