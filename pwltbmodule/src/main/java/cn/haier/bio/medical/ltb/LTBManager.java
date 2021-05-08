package cn.haier.bio.medical.ltb;

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

    public void init(String path) {
        if(this.serialPort == null){
            this.serialPort = new LTBSerialPort();
            this.serialPort.init(path);
        }
    }

    public void enable() {
        if(null != this.serialPort){
            this.serialPort.enable();
        }
    }

    public void disable() {
        if(null != this.serialPort){
            this.serialPort.disable();
        }
    }

    public void release() {
        if(null != this.serialPort){
            this.serialPort.release();
            this.serialPort = null;
        }
    }

    public void changeFsync(boolean fsync) {
        if(null != this.serialPort){
            this.serialPort.changeFsync(fsync);
        }
    }

    public void changeListener(ILTBListener listener) {
        if(null != this.serialPort){
            this.serialPort.changeListener(listener);
        }
    }
}

