package cn.haier.bio.medical.ltb;


import java.util.Arrays;

import cn.haier.bio.medical.ltb.entity.LTBDataEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

class LTBTools {

    public static final byte[] SYSTEM_TYPES = {0x01,0x02,0x04,0x08};
    public static final byte[] COMMAND_TYPES = {0x10,0x03};

    public static boolean checkFrame(byte[] data) {
        byte[] crc = new byte[]{data[data.length - 2], data[data.length - 1]};
        byte[] check = computeCRC16CodeLE(data, 0, data.length - 2);
        return Arrays.equals(crc, check);
    }

    public static boolean checkSystemType(byte system) {
        for (byte item:SYSTEM_TYPES) {
            if(item == system){
                return true;
            }
        }
        return false;
    }

    public static boolean checkCommandType(byte command) {
        for (byte item:COMMAND_TYPES) {
            if(item == command){
                return true;
            }
        }
        return false;
    }

    public static byte[] packageStateResponse(byte[] data) {
        byte[] buffer = new byte[8];
        System.arraycopy(data, 0, buffer, 0, buffer.length - 2);
        byte[] crc = computeCRC16CodeLE(data, 0, buffer.length - 2);
        buffer[buffer.length - 2] = crc[0];
        buffer[buffer.length - 1] = crc[1];
        return buffer;
    }

    public static LTBDataEntity parseLTB760AGEntity(byte[] data) {
        ByteBuf buffer = Unpooled.copiedBuffer(data);
        LTBDataEntity entity = new LTBDataEntity();
        entity.setSystem(buffer.getByte(0));
        entity.setTemperature(buffer.getShortLE(7));
        entity.setAmbientTemperature(buffer.getShortLE(9));
        if(entity.getSystem() == 0x02){
            entity.setCondenser1Temperature(buffer.getShortLE(11));
            entity.setCondenser2Temperature(buffer.getShortLE(13));
        }else {
            entity.setCondenserTemperature(buffer.getShortLE(11));
            entity.setHeatExchangerTemperature(buffer.getShortLE(13));
        }
        entity.setSupplyVoltage(buffer.getShortLE(15));
        entity.setMainBatteryVoltage(buffer.getByte(17));
        entity.setDoorInputStatus1(buffer.getShortLE(19));
        entity.setDoorInputStatus2(buffer.getShortLE(21));
        entity.setBatteryChargingStatus(buffer.getShortLE(23));
        entity.setRemoteAlarmOutputStatus(buffer.getShortLE(25));
        entity.setHighTemperatureCompressorStatus(buffer.getShortLE(27));
        entity.setLowTemperatureCompressorStatus(buffer.getShortLE(29));
        entity.setHighTemperatureBlowerStatus(buffer.getShortLE(31));
        entity.setLowTemperatureBlowerStatus(buffer.getShortLE(33));
        entity.setRisePressureOutputStatus(buffer.getShortLE(35));
        entity.setDropPressureOutputStatus(buffer.getShortLE(37));
        entity.setAccapillaryHeatingWireOutputStatus(buffer.getShortLE(39));
        entity.setCabinetHeatingWireOutputStatus(buffer.getShortLE(41));
        entity.setDoorHeatingWireOutputStatus(buffer.getShortLE(43));
        entity.setBalanceHeatingWireOutputStatus(buffer.getShortLE(45));
        entity.setReservedHeatingWireStatus(buffer.getShortLE(47));
        entity.setElectromagneticLockOutputStatus(buffer.getShortLE(49));
        entity.setBuzzerOutputStatus(buffer.getShortLE(51));
        entity.setAlarmStatus1(buffer.getShortLE(53));
        entity.setAlarmStatus2(buffer.getShortLE(55));
        entity.setBackupTemperature(buffer.getShortLE(57));
        entity.setBackupStatus(buffer.getShortLE(59));
        entity.setThermocoupleTemperature1(buffer.getShortLE(61));
        entity.setThermocoupleTemperature2(buffer.getShortLE(63));
        entity.setThermocoupleTemperature3(buffer.getShortLE(65));
        entity.setThermocoupleTemperature4(buffer.getShortLE(67));
        entity.setThermocoupleTemperature5(buffer.getShortLE(69));
        entity.setThermocoupleTemperature6(buffer.getShortLE(71));
        entity.setThermocoupleTemperature7(buffer.getShortLE(73));
        entity.setThermocoupleTemperature8(buffer.getShortLE(75));
        entity.setThermocoupleTemperature9(buffer.getShortLE(77));
        entity.setThermocoupleTemperature10(buffer.getShortLE(79));
        entity.setBackupConnectionStatus(buffer.getShortLE(81));
        entity.setSettingTemperatureValue(buffer.getShortLE(83));
        entity.setHighTemperatureAlarmValue(buffer.getShortLE(85));
        entity.setLowTemperatureAlarmValue(buffer.getShortLE(87));
        buffer.release();
        return entity;
    }

    public static byte[] short2BytesLE(short value) {
        byte bytes[] = new byte[2];
        bytes[1] = (byte) (0xff & (value >> 8));
        bytes[0] = (byte) (0xff & value);
        return bytes;
    }

    public static String bytes2HexString(byte[] data) {
        return bytes2HexString(data, false);
    }

    public static String bytes2HexString(byte[] data, boolean hexFlag) {
        return bytes2HexString(data, hexFlag, null);
    }

    public static String bytes2HexString(byte[] data, boolean hexFlag, String separator) {
        if (data == null) {
            throw new IllegalArgumentException("The data can not be blank");
        }
        return bytes2HexString(data, 0, data.length, hexFlag, separator);
    }

    public static String bytes2HexString(byte[] data, int offset, int len) {
        return bytes2HexString(data, offset, len, false);
    }

    public static String bytes2HexString(byte[] data, int offset, int len, boolean hexFlag) {
        return bytes2HexString(data, offset, len, hexFlag, null);
    }

    public static String bytes2HexString(byte[] data, int offset, int len, boolean hexFlag, String separator) {
        if (data == null) {
            throw new IllegalArgumentException("The data can not be blank");
        }
        if (offset < 0 || offset > data.length - 1) {
            throw new IllegalArgumentException("The offset index out of bounds");
        }
        if (len < 0 || offset + len > data.length) {
            throw new IllegalArgumentException("The len can not be < 0 or (offset + len) index out of bounds");
        }
        String format = "%02X";
        if (hexFlag) {
            format = "0x%02X";
        }
        StringBuffer buffer = new StringBuffer();
        for (int i = offset; i < offset + len; i++) {
            buffer.append(String.format(format, data[i]));
            if (separator == null) {
                continue;
            }
            if (i != (offset + len - 1)) {
                buffer.append(separator);
            }
        }
        return buffer.toString();
    }

    public static byte[] computeCRC16CodeLE(byte[] data, int offset, int len) {
        if (data == null) {
            throw new IllegalArgumentException("The data can not be blank");
        }
        if (offset < 0 || offset > data.length - 1) {
            throw new IllegalArgumentException("The offset index out of bounds");
        }
        if (len < 0 || offset + len > data.length) {
            throw new IllegalArgumentException("The len can not be < 0 or (offset + len) index out of bounds");
        }
        int crc = 0xFFFF;
        for (int pos = offset; pos < offset + len; pos++) {
            if (data[pos] < 0) {
                crc ^= (int) data[pos] + 256; // XOR byte into least sig. byte of
                // crc
            } else {
                crc ^= (int) data[pos]; // XOR byte into least sig. byte of crc
            }
            for (int i = 8; i != 0; i--) { // Loop over each bit
                if ((crc & 0x0001) != 0) { // If the LSB is set
                    crc >>= 1; // Shift right and XOR 0xA001
                    crc ^= 0xA001;
                } else {
                    // Else LSB is not set
                    crc >>= 1; // Just shift right
                }
            }
        }
        return short2BytesLE((short)crc);
    }

    public static int indexOf(ByteBuf haystack, byte[] needle) {
        //遍历haystack的每一个字节
        for (int i = haystack.readerIndex(); i < haystack.writerIndex(); i++) {
            int needleIndex;
            int haystackIndex = i;
            /*haystack是否出现了delimiter，注意delimiter是一个ChannelBuffer（byte[]）
            例如对于haystack="ABC\r\nDEF"，needle="\r\n"
            那么当haystackIndex=3时，找到了“\r”，此时needleIndex=0
            继续执行循环，haystackIndex++，needleIndex++，
            找到了“\n”
            至此，整个needle都匹配到了
            程序然后执行到if (needleIndex == needle.capacity())，返回结果
            */
            for (needleIndex = 0; needleIndex < needle.length; needleIndex++) {
                if (haystack.getByte(haystackIndex) != needle[needleIndex]) {
                    break;
                } else {
                    haystackIndex++;
                    if (haystackIndex == haystack.writerIndex() && needleIndex != needle.length - 1) {
                        return -1;
                    }
                }
            }

            if (needleIndex == needle.length) {
                // Found the needle from the haystack!
                return i - haystack.readerIndex();
            }
        }
        return -1;
    }
}
