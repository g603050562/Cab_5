package forlinx.hardware;

import java.io.FileDescriptor;

public class hardwareControl {


    public native static FileDescriptor openSerialPort(String path, int baudrate);

    public native static void closeSerialPort();

    public native static FileDescriptor openRs485(String path, int baudrate);

    public native static void closeRs485();

    public native static void Rs485write(String buff, int num);

    public native static int ledSetState(int ledNum, int ledState);

    public native static int readAdc(int channel);

    public native static void initCan(int baudrate);

    public native static int openCan();

    public native static int canWrite(int canId, byte[] data);

    public native static canFrame canRead(canFrame mcanFrame, int time);

    public native static void closeCan();

    static {
        System.loadLibrary("forlinxHardware");
    }

}