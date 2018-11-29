package dragontail;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;

import dragontail.DtCamera.AutoFocusROIModeAndWindowSize;
import efcom.DtCamExtend.CamAfMode;

public class Main {

    DtCamera camera = new DtCamera();
    File outDir = null;

    public void init(Properties props) {
        camera.init(props);
    }

    public void setOutDir(String dirName) throws IOException {
        outDir = new File(dirName);
        if (!outDir.exists()) {
            throw new IOException("Directory does not exist: " + outDir.getAbsolutePath());
        } else if (!outDir.isDirectory()) {
            throw new IOException("Not a directory: " + outDir.getAbsolutePath());
        }
    }

    public File getOutDir() {
        return outDir;
    }

    public static void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            // Do nothing
        }
    }

    void reportImage(BufferedImage image, String filename) throws IOException {
        if (image != null) {
            File file = new File(getOutDir(), filename + ".bmp");
            System.out.println("\t" + file.getAbsolutePath());
            ImageIO.write(image, "bmp", file);
        } else {
            System.out.println("\t Image is null");
        }
    }

    public void readLoop() {
        try {
            camera.open();
            System.out.println("Is open: " + camera.isOpen());
            BufferedImage image = camera.read();
            reportImage(image, "Focus_100");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            camera.close();
        }
    }
    
    public void reportFocus() {
        AutoFocusROIModeAndWindowSize data = camera.getAutoFocusROIModeAndWindowSize();
        System.out.println("Focus ROI mode: " + data.getRoiMode());
        System.out.println("Focus length: " + data.getWinSize());
    }
    
    public int getManualFocus() throws IOException {
        return camera.getManualFocus();
    }
    
    public void setManualFocus(int focus) throws IOException {
        System.out.print("Set manual focus: " + focus);                
        if (camera.setManualFocus(focus)) {
            System.out.println(" - success");                
        } else {
            System.out.println(" - failure");                
        }
    }
    
    public void setExposure(int exposure) throws IOException {
        System.out.print("Set exposure: " + exposure);
        if (camera.setExposure(exposure)) {
            System.out.println(" - success");
        } else {
            System.out.println(" - failure");
        }
    }

    public void setWhiteBalance(int whiteBalance) throws IOException {
        System.out.print("Set White Balance: " + whiteBalance);
        if (camera.setWhiteBalance(whiteBalance)) {
            System.out.println(" - success");
        } else {
            System.out.println(" - failure");
        }
    }

    void skipImages(int count) throws IOException {
        while (count-- > 0) {
            camera.readRawBytes();
        }
    }

    public void manualFocus() {
        try {
            camera.open();

            for (int i=0; i<5; i++) {
                setManualFocus(25 + i*5);
                int focus = getManualFocus();
                System.out.println("Focus is: " + focus);

                skipImages(50);
                BufferedImage image = camera.read();
                reportImage(image, "ImageFocus_" + i);
            }

            System.out.println("Set to default");
            camera.setToDefault();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            camera.close();
        }
    }

    public void manualExposure() {
        try {
            camera.open();

            for (int i=0; i<4; i++) {
                setExposure(300 + i*100);
                int value = camera.getExposure();
                System.out.println("Exposure is: " + value);

                skipImages(50);
                BufferedImage image = camera.read();
                reportImage(image, "ImageExposure_" + i);
            }

            System.out.println("Set to default");
            camera.setToDefault();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            camera.close();
        }
    }

    public void manualWhiteBalance() {
        try {
            camera.open();

            for (int i=0; i<4; i++) {
                setWhiteBalance(3000 + i*1000);
                int value = camera.getWhiteBalance();
                System.out.println("White balance is: " + value);

                skipImages(50);
                BufferedImage image = camera.read();
                reportImage(image, "ImageWhiteBalance_" + i);
            }

            System.out.println("Set to default");
            camera.setToDefault();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            camera.close();
        }
    }

    public void colourImage() {
        try {
            camera.open();

            reportImage(camera.read(), "ImageColour");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            camera.close();
        }
    }

    public void run() {
//        manualWhiteBalance();
//        manualExposure();
        manualFocus();
        //colourImage();
    }

    public static void main(String[] args) throws IOException {
        String propertyName = args.length > 0 ? args[0] : "camera.properties";
        Properties props = new Properties();
        props.load(Main.class.getClassLoader().getResourceAsStream(propertyName));
        if (props.isEmpty()) {
            throw new IOException("Could not load: " + propertyName);
        }
        Main main = new Main();
        if (args.length > 1) {
            main.setOutDir(args[1]);
        } else {
            main.setOutDir("images");
        }
        main.init(props);
        main.run();
    }

}
