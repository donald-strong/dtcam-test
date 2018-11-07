package dragontail;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.Properties;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import efcam.DtCam;

public class DtCamera {
    public static final String AUTO_FIND_VIDEO = "AUTO";
    public static final int DEFAULT_WIDTH = 3840;
    public static final int DEFAULT_HEIGHT = 2160;
    public static final int DEFAULT_CHANNELS = 3;
    public static final int DEFAULT_FPS = 15;

    private final DtCam dtcam;
    private String deviceName;
    private int width;
    private int height;
    private int channels = 3;
    private int fps;
    
    public DtCamera() {
        this.deviceName = AUTO_FIND_VIDEO;
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.channels = DEFAULT_CHANNELS;
        this.fps = DEFAULT_FPS;

        NativeLibrary.getInstance("udev");
        dtcam = Native.load("DTCam", DtCam.class);
    }
    
    public DtCamera(String deviceName) {
        this();
        this.deviceName = deviceName;
    }

    int getInt(Properties props, String name, int defaultValue) {
        String value = props.getProperty(name);
        return (value == null) ? defaultValue : Integer.parseInt(value);
    }

    public void init(Properties props) {
        this.deviceName = props.getProperty("deviceName", deviceName);
        this.width = getInt(props, "width", width);
        this.height = getInt(props, "height", height);
        this.channels = getInt(props, "channels", channels);
        this.fps = getInt(props, "fps", fps);
    }

    public void open() throws IOException {
        int count = 0;
        while (count++ < 10) {
//            if (deviceName == AUTO_FIND_VIDEO) {
                System.out.format("open(AUTO, %d, %d, %d)\n" ,width, height, fps);
                if (dtcam.DTCam_Start(width, height, fps) != 0) {
                    throw new IOException("Could not find camera");
                }
//            } else {
//                System.out.format("open(%s, %d, %d, %d)\n",deviceName ,width, height, fps);
//                if (dtcam.DTCam_Start_Video(deviceName, width, height, fps) != 0) {
//                    throw new IOException("Could not open camera: " + deviceName);
//                }
//            }
            if (dtcam.DTCam_State() == 0) {
                return;
            }
            try {
                Thread.sleep(count*100);
            } catch (InterruptedException e) {
                // Do nothing
            }
        }
        throw new IOException("Could not open camera: " + deviceName);
    }
    
    public void close() {
        System.out.println("close()");
        dtcam.DTCam_Stop();
    }
    
    public DtCam getDtCam() {
        return dtcam;
    }
    
    public BufferedImage readBufferedImage() throws IOException {
        PointerByReference pref = new PointerByReference();
        IntByReference iref = new IntByReference();
        if (dtcam.DTCam_Grab(pref, iref) == 0) {
            Pointer p = pref.getValue();
            byte[] source = p.getByteArray(0, iref.getValue());
            
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            image.getRaster().setDataElements(0, 0, width, height, source);
            return image;
        }
        return null;
    }
    
    byte [] getByteArray(BufferedImage image) throws IOException {
        DataBuffer dataBuffer = image.getRaster().getDataBuffer();
        byte[] data = ((DataBufferByte) dataBuffer).getData();
        if(data.length != width*height*channels){
            throw new IOException("Mismatching data sizes! " + "getByteArray: " + data.length + " " + 
                        width*height*channels + " " + width + "x" + height + "x" + channels);
        }
        return data;
    }
}
