package cn.haier.bio.medical.ltb;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.lang.ref.WeakReference;

import cn.haier.bio.medical.ltb.entity.LTBDataEntity;
import cn.haier.bio.medical.ltb.tools.LTBTools;
import cn.qd.peiwen.pwlogger.PWLogger;
import cn.qd.peiwen.pwtools.ByteUtils;
import cn.qd.peiwen.pwtools.EmptyUtils;
import cn.qd.peiwen.serialport.PWSerialPortHelper;
import cn.qd.peiwen.serialport.PWSerialPortListener;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class LTBSerialPort implements PWSerialPortListener {
    private ByteBuf buffer;
    private HandlerThread thread;
    private LTB760AGHandler handler;
    private PWSerialPortHelper helper;

    private byte system = 0x00;
    private boolean ready = false;
    private boolean enabled = false;
    private WeakReference<ILTBListener> listener;

    public LTBSerialPort() {
    }

    public void init(String path) {
        this.createHandler();
        this.createHelper(path);
        this.createBuffer();
    }

    public void enable() {
        if (this.isInitialized() && !this.enabled) {
            this.enabled = true;
            this.helper.open();
        }
    }

    public void disable() {
        if (this.isInitialized() && this.enabled) {
            this.enabled = false;
            this.helper.close();
        }
    }

    public void release() {
        this.listener = null;
        this.destoryHandler();
        this.destoryHelper();
        this.destoryBuffer();
    }

    public void changedListener(ILTBListener listener) {
        this.listener = new WeakReference<>(listener);
    }

    private boolean isInitialized() {
        if (EmptyUtils.isEmpty(this.handler)) {
            return false;
        }
        if (EmptyUtils.isEmpty(this.helper)) {
            return false;
        }
        if (EmptyUtils.isEmpty(this.buffer)) {
            return false;
        }
        return true;
    }

    private void createHelper(String path) {
        if (EmptyUtils.isEmpty(this.helper)) {
            this.helper = new PWSerialPortHelper("LTBSerialPort");
            this.helper.setTimeout(2);
            this.helper.setPath(path);
            this.helper.setBaudrate(9600);
            this.helper.init(this);
        }
    }

    private void destoryHelper() {
        if (EmptyUtils.isNotEmpty(this.helper)) {
            this.helper.release();
            this.helper = null;
        }
    }

    private void createHandler() {
        if (EmptyUtils.isEmpty(this.thread) && EmptyUtils.isEmpty(this.handler)) {
            this.thread = new HandlerThread("LTBSerialPort");
            this.thread.start();
            this.handler = new LTB760AGHandler(this.thread.getLooper());
        }
    }

    private void destoryHandler() {
        if (EmptyUtils.isNotEmpty(this.thread)) {
            this.thread.quitSafely();
            this.thread = null;
            this.handler = null;
        }
    }

    private void createBuffer() {
        if (EmptyUtils.isEmpty(this.buffer)) {
            this.buffer = Unpooled.buffer(4);
        }
    }

    private void destoryBuffer() {
        if (EmptyUtils.isNotEmpty(this.buffer)) {
            this.buffer.release();
            this.buffer = null;
        }
    }

    private void write(byte[] data) {
        PWLogger.d("LTB Send:" + ByteUtils.bytes2HexString(data, true, ", "));
        if (this.isInitialized() && this.enabled) {
            this.helper.writeAndFlush(data);
        }
    }

    private boolean ignorePackage() {
        boolean result = false;
        if (this.system == 0x00) {
            for (byte item : LTBTools.SYSTEM_TYPES) {
                byte[] bytes = new byte[]{item, 0x10, 0x40, 0x1F};
                int index = LTBTools.indexOf(this.buffer, bytes);
                if (index != -1) {
                    result = true;
                    byte[] data = new byte[index];
                    this.buffer.readBytes(data, 0, data.length);
                    this.buffer.discardReadBytes();
                    PWLogger.d("指令丢弃:" + ByteUtils.bytes2HexString(data, true, ", "));
                    break;
                }
            }
        } else {
            byte[] bytes = new byte[]{this.system, 0x10, 0x40, 0x1F};
            int index = LTBTools.indexOf(this.buffer, bytes);
            if (index != -1) {
                result = true;
                byte[] data = new byte[index];
                this.buffer.readBytes(data, 0, data.length);
                this.buffer.discardReadBytes();
                PWLogger.d("指令丢弃:" + ByteUtils.bytes2HexString(data, true, ", "));
            }
        }
        return result;
    }


    @Override
    public void onConnected(PWSerialPortHelper helper) {
        if (!this.isInitialized() || !helper.equals(this.helper)) {
            return;
        }
        this.ready = false;
        this.buffer.clear();
        this.system = 0x00;
        LTBTools.switchReadModel();
        if (EmptyUtils.isNotEmpty(this.listener)) {
            this.listener.get().onLTBConnected();
        }
    }

    @Override
    public void onException(PWSerialPortHelper helper) {
        if (!this.isInitialized() || !helper.equals(this.helper)) {
            return;
        }
        this.ready = false;
        if (EmptyUtils.isNotEmpty(this.listener)) {
            this.listener.get().onLTBException();
        }
    }

    @Override
    public void onByteReceived(PWSerialPortHelper helper, byte[] buffer, int length) throws IOException {
        if (!this.isInitialized() || !helper.equals(this.helper)) {
            return;
        }
        this.buffer.writeBytes(buffer, 0, length);
        while (this.buffer.readableBytes() >= 2) {
            byte system = this.buffer.getByte(0);
            byte command = this.buffer.getByte(1);
            if (!LTBTools.checkSystemType(system) || !LTBTools.checkCommandType(command)) {
                if (this.ignorePackage()) {
                    continue;
                } else {
                    break;
                }
            }
            int lenth = (command == 0x10) ? 109 : 8;
            if (this.buffer.readableBytes() < lenth) {
                break;
            }
            this.buffer.markReaderIndex();
            byte[] data = new byte[lenth];
            byte model = this.buffer.getByte(2);
            this.buffer.readBytes(data, 0, lenth);
            if (!LTBTools.checkFrame(data)) {
                this.buffer.resetReaderIndex();
                //当前包不合法 丢掉正常的包头以免重复判断
                this.buffer.skipBytes(4);
                this.buffer.discardReadBytes();
                continue;
            }
            this.buffer.discardReadBytes();
            if (!this.ready) {
                this.ready = true;
                if (EmptyUtils.isNotEmpty(this.listener)) {
                    this.listener.get().onLTBReady();
                }
            }
            if (this.system == 0x00) {
                this.system = system;
                if (EmptyUtils.isNotEmpty(this.listener)) {
                    this.listener.get().onLTBSystemChanged(this.system);
                }
            }
            PWLogger.d("LTB Recv:" + ByteUtils.bytes2HexString(data, true, ", "));
            LTBTools.switchWriteModel();
            Message msg = Message.obtain();
            msg.what = command;
            if (command == 0x10) {
                msg.obj = data;
            } else {
                msg.arg1 = model & 0xFF;
            }
            this.handler.sendMessage(msg);
        }
    }


    private class LTB760AGHandler extends Handler {
        public LTB760AGHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x10: {
                    byte[] data = (byte[]) msg.obj;
                    byte[] response = LTBTools.packageStateResponse(data);
                    LTBSerialPort.this.write(response);
                    LTBTools.switchReadModel();
                    LTBDataEntity entity = LTBTools.parseLTB760AGEntity(data);
                    if (EmptyUtils.isNotEmpty(LTBSerialPort.this.listener)) {
                        LTBSerialPort.this.listener.get().onLTBStateChanged(entity);
                    }
                    break;
                }
                case 0x03: {
                    byte[] response = null;
                    if (EmptyUtils.isNotEmpty(LTBSerialPort.this.listener)) {
                        response = LTBSerialPort.this.listener.get().packageLTBResponse(msg.arg1);
                    }
                    if (EmptyUtils.isNotEmpty(response)) {
                        LTBSerialPort.this.write(response);
                    }
                    LTBTools.switchReadModel();
                    break;
                }
                default:
                    break;
            }
        }
    }
}
