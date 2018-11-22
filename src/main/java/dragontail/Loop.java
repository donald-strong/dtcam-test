package dragontail;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;

import efcom.DtCamExtend.CamAfMode;
import efcom.DtCamExtend.CamROIAfMode;

public class Loop {

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

    void reportImage(BufferedImage image, int count) throws IOException {
        System.out.print(image == null ? 'x' : '.');
        if ((count % 50) == 0) {
            File file = new File(getOutDir(), "Image_" + count + ".jpg");
            System.out.println(" " + file.getAbsolutePath());
            ImageIO.write(image, "jpg", file);
        } else {
            System.out.flush();
        }
    }

    public void readLoop() {
        int count = 0;
        try {
            camera.open();
            System.out.println("Is open: " + camera.isOpen());
            while (true) {
                BufferedImage image = camera.read();
                reportImage(image, ++count);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            camera.close();
        }
    }

    public void run() {
        int failedToOpen = 0;
        while (failedToOpen++ < 3) {
            try {
                camera.open();
                if (!camera.isOpen()) {
                    System.out.println("Camera not connected...");
                    sleep(1000);
                } else {
                    failedToOpen = 0;
                    System.out.println("Camera connected...");
                    camera.setAutoFocusMode(CamAfMode.AfModeDisabled);
                    camera.setRoiAutoFocus(CamROIAfMode.AFManual, 0, 0, 250);
                    //camera.setRoiAutoFocus(CamROIAfMode.AFDisabled, 0, 0, 250);
                    sleep(2000);
                    int count = 0;
                    int retry = 0;
                    while (retry++ < 3) {
                        BufferedImage image = camera.read();
                        if (image != null) {
                            reportImage(image, ++count);
                            retry = 0;
                        } else {
                            System.out.print('x');
                            System.err.flush();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Camera disconnected...\n");
                camera.close();
            }
            sleep(1000);
        }
    }

    public static void main(String[] args) throws IOException {
        String propertyName = args.length > 0 ? args[0] : "camera.properties";
        Properties props = new Properties();
        props.load(Loop.class.getClassLoader().getResourceAsStream(propertyName));
        if (props.isEmpty()) {
            throw new IOException("Could not load: " + propertyName);
        }
        Loop main = new Loop();
        if (args.length > 1) {
            main.setOutDir(args[1]);
        } else {
            main.setOutDir("images");
        }
        main.init(props);
        main.run();
    }

}
