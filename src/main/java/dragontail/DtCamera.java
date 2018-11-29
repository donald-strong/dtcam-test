package dragontail;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Properties;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

import efcom.DtCam;
import efcom.DtCamExtend;
import efcom.DtCamExtend.CamAfMode;
import efcom.DtCamExtend.CamROIAfMode;

public class DtCamera {
    public static final String AUTO_FIND_VIDEO = "AUTO";
    public static final int DEFAULT_WIDTH = 3840;
    public static final int DEFAULT_HEIGHT = 2160;
    public static final int DEFAULT_CHANNELS = 3;
    public static final int DEFAULT_FPS = 15;
    public static final int UNKNOWN = -666;
    public static final int [] TRANS_210 = new int[] {2, 1, 0};

    private final DtCam dtcam;
    private final DtCamExtend dtcamExtra;
    private String deviceName;
    private int width;
    private int height;
    private int channels = 3;
    private int fps;
    
    public DtCamera() {
        this.width = DEFAULT_WIDTH;
        this.height = DEFAULT_HEIGHT;
        this.channels = DEFAULT_CHANNELS;
        this.fps = DEFAULT_FPS;

        NativeLibrary.getInstance("udev");
        dtcam = Native.load("DTCam", DtCam.class);
        dtcamExtra = Native.load("DTCam", DtCamExtend.class);
    }
    
    int getInt(Properties props, String name, int defaultValue) {
        String value = props.getProperty(name);
        return (value == null) ? defaultValue : Integer.parseInt(value);
    }

    public void init(Properties props) {
        this.width = getInt(props, "width", width);
        this.height = getInt(props, "height", height);
        this.channels = getInt(props, "channels", channels);
        this.fps = getInt(props, "fps", fps);
    }

    public boolean open() throws IOException {
        System.out.format("open(AUTO, %d, %d, %d)\n" ,width, height, fps);
        if (dtcam.DTCam_Start(width, height, fps) != 0) {
            return false;
        }
        // Read the first few images;
        // * Set property doesn't work before reading images
        // * DTCam_Grab returns corrupt data at the start.
        skipImages(5);
        return true;
    }
    
    public void close() {
        System.out.println("close()");
        dtcam.DTCam_Stop();
    }
    
    public boolean isOpen() {
        return dtcam.DTCam_State() == 0;
    }

    public boolean setAutoFocusMode(CamAfMode mode) {
        int result = dtcamExtra.setAutoFocusMode(mode.value());
        return result > 0;
    }

    public static class AutoFocusROIModeAndWindowSize {
        private final CamROIAfMode roiMode;
        private final int winSize;
        AutoFocusROIModeAndWindowSize(CamROIAfMode roiMode, int winSize) {
            this.roiMode = roiMode;
            this.winSize = winSize;
        }
        public CamROIAfMode getRoiMode() {
            return roiMode;
        }
        public int getWinSize() {
            return winSize;
        }
    }
    
    public AutoFocusROIModeAndWindowSize getAutoFocusROIModeAndWindowSize() {
        IntByReference roiMode = new IntByReference(); 
        IntByReference winSize = new IntByReference();
        if (dtcamExtra.getAutoFocusROIModeAndWindowSize(roiMode, winSize) == 1) {
            CamROIAfMode mode = CamROIAfMode.toValue(roiMode.getValue());
            return new AutoFocusROIModeAndWindowSize(mode, winSize.getValue());
        } else {
            return null;
        }
    }
    
    public boolean setRoiAutoFocus(CamROIAfMode see3camAfROIMode, int xCord, int yCord, int winSize)
    {
        int result = dtcamExtra.setROIAutoFoucs(see3camAfROIMode.value(), 
                width, height, xCord, yCord, winSize);
        return result > 0;
    }

    public boolean setExposure(int exposure) {
        if (exposure < -1 || exposure > 10000) {
            exposure = -1;
        }
        int result = dtcam.DTCam_SetExposure(exposure);
        return result != -1;
    }

    public int getExposure() {
        IntByReference exposure = new IntByReference();
        int result = dtcam.DTCam_GetExposure(exposure);
        if (result == 0) {
            return exposure.getValue();
        } else {
            return UNKNOWN;
        }
    }

    public boolean setWhiteBalance(int whiteBalance) {
        if (whiteBalance != -1 && (1000 > whiteBalance || whiteBalance > 10000)) {
            whiteBalance = -1;
        }
        int result = dtcam.DTCam_SetWhiteBalance(whiteBalance);
        return result != -1;
    }

    public int getWhiteBalance() {
        IntByReference whiteBalance = new IntByReference();
        int result = dtcam.DTCam_GetWhiteBalance(whiteBalance);
        if (result == 0) {
            return whiteBalance.getValue();
        } else {
            return UNKNOWN;
        }
    }
    
    public boolean setManualFocus(int focusLength) throws IOException
    {
        if (focusLength < -1 || focusLength > 255) {
            focusLength = -1;
        }
        int result = dtcam.DTCam_SetFocus(focusLength);
        return result != -1;
    }
    
    public int getManualFocus() throws IOException {
        IntByReference focusLength = new IntByReference(); 
        int result = dtcam.DTCam_GetFocus(focusLength);
        if (result == 0) {
            return focusLength.getValue();
        } else {
            return UNKNOWN;
        }
    }
    
    public void setToDefault() {
        dtcamExtra.setToDefault();
    }

    public BufferedImage read() throws IOException {
        byte [] source = readRawBytes();
        if (source != null) {
            byte [] target = rearrange(source, TRANS_210);
            return readBufferedImage(target);
        }
        return null;
    }
    
    BufferedImage readBufferedImage(byte[] source) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        image.getRaster().setDataElements(0, 0, width, height, source);
        return image;
    }
    
    byte [] readRawBytes() throws IOException {
        PointerByReference pref = new PointerByReference();
        IntByReference iref = new IntByReference();
        if (dtcam.DTCam_Grab(pref, iref) == 0) {
            Pointer p = pref.getValue();
            return p.getByteArray(0, iref.getValue());
        }
        return null;
    }

    byte [] rearrange(byte[] source, int [] trans) {
        int len = source.length;
        byte [] target = new byte[len];
        for (int i=0; i<len; i+=3) {
            target[i+0] = source[i+trans[0]];
            target[i+1] = source[i+trans[1]];
            target[i+2] = source[i+trans[2]];
        }
        return target;
    }

    public void skipImages(int count) throws IOException {
        while (count-- > 0) {
            readRawBytes();
        }
    }
}
