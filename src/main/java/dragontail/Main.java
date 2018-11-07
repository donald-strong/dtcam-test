package dragontail;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;

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
    
    public void run() {
        int count = 0;
        try {
            camera.open();
            System.out.println("Status: " + camera.getDtCam().DTCam_State());
            while (count <= 200) {
                BufferedImage image = camera.readBufferedImage();
                System.out.print(image == null ? 'x' : '.');
                if ((++count % 50) == 0) {
                    File file = new File(getOutDir(), "Image_" + count + ".jpg");
                    System.out.println(" " + file.getAbsolutePath());
                    ImageIO.write(image, "jpg", file);
                } else {
                    System.out.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            camera.close();
        }
    }
    
    public static void main(String[] args) throws IOException {
        String propertyName = args.length > 0 ? args[0] : "video0.properties";
        Properties props = new Properties();
        props.load(Main.class.getClassLoader().getResourceAsStream(propertyName));
        if (props.isEmpty()) {
            throw new IOException("Could not load: " + propertyName);
        }
        Main main = new Main();
        if (args.length > 1) {
            main.setOutDir(args[1]);
        }
        main.init(props);
        main.run();
    }

}
