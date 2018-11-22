package efcom;

import com.sun.jna.Library;
import com.sun.jna.ptr.IntByReference;

public interface DtCamExtend extends Library {
    interface EnumValue {
        int value();
    };
    
    public enum SceneModes implements EnumValue {
        SCENE_NORMAL(0x01),
        SCENE_DOCUMENT(0x0C);
        
        private final int value;
        
        SceneModes(int value) {
            this.value= value;
        }

        @Override
        public int value() {
            return value;
        }
        
        public static SceneModes toValue(int value) {
            for (SceneModes cand : SceneModes.values()) {
                if (cand.value == value) {
                    return cand;
                }
            }
            return null;
        }
    };

    public enum SpecialEffects implements EnumValue {
        EFFECT_NORMAL(0x01),        
        EFFECT_BLACK_WHITE(0x04),
        EFFECT_GREYSCALE(0x07),
        EFFECT_NEGATIVE(0x08),
        EFFECT_SKETCH(0x10);
        
        private final int value;
        
        SpecialEffects(int value) {
            this.value= value;
        }

        @Override
        public int value() {
            return value;
        }
        
        public static SpecialEffects toValue(int value) {
            for (SpecialEffects cand : SpecialEffects.values()) {
                if (cand.value == value) {
                    return cand;
                }
            }
            return null;
        }
    };
    
    public enum CamAfMode implements EnumValue {
        Continuous(0x01),
        OneShot(0x02),
        AfModeDisabled(0x03);
        
        private final int value;
        
        CamAfMode(int value) {
            this.value= value;
        }

        @Override
        public int value() {
            return value;
        }

        public static CamAfMode toValue(int value) {
            for (CamAfMode cand : CamAfMode.values()) {
                if (cand.value == value) {
                    return cand;
                }
            }
            return null;
        }
    };

    public enum CamiHDRMode implements EnumValue {
        HdrOff(0x01),
        HdrAuto(0x02),
        HdrManual(0x03);
        
        private final int value;
        
        CamiHDRMode(int value) {
            this.value= value;
        }

        @Override
        public int value() {
            return value;
        }

        public static CamiHDRMode toValue(int value) {
            for (CamiHDRMode cand : CamiHDRMode.values()) {
                if (cand.value == value) {
                    return cand;
                }
            }
            return null;
        }
    };

    public enum CamROIAfMode implements EnumValue {
        AFCentered(0x01),
        AFManual(0x02),
        AFDisabled(0x03);
        
        private final int value;
        
        CamROIAfMode(int value) {
            this.value= value;
        }

        @Override
        public int value() {
            return value;
        }

        public static CamROIAfMode toValue(int value) {
            for (CamROIAfMode cand : CamROIAfMode.values()) {
                if (cand.value == value) {
                    return cand;
                }
            }
            return null;
        }
    };

    public enum CamROIAutoExpMode implements EnumValue {
        AutoExpFace(0x00),
        AutoExpFull(0x01),
        AutoExpManual(0x02),
        AutoExpDisabled(0x03);
        
        private final int value;
        
        CamROIAutoExpMode(int value) {
            this.value= value;
        }

        @Override
        public int value() {
            return value;
        }
        
        public static CamROIAutoExpMode toValue(int value) {
            for (CamROIAutoExpMode cand : CamROIAutoExpMode.values()) {
                if (cand.value == value) {
                    return cand;
                }
            }
            return null;
        }
    };

    public enum CamStreamMode implements EnumValue {
        STREAM_MASTER(0x00),
        STREAM_TRIGGER(0x01);
        
        private final int value;
        
        CamStreamMode(int value) {
            this.value= value;
        }

        @Override
        public int value() {
            return value;
        }
        
        public static CamStreamMode toValue(int value) {
            for (CamStreamMode cand : CamStreamMode.values()) {
                if (cand.value == value) {
                    return cand;
                }
            }
            return null;
        }
    };

    int setSceneMode(int sceneMode);

    int setEffectMode(SpecialEffects specialEffect);

    int setDenoiseValue(int deNoiseVal);

    //int setAutoFocusMode(CamAfMode afMode);
    int setAutoFocusMode(int afMode);

    int setiHDRMode(CamiHDRMode iHDRMode, int iHDRValue);

    //int setROIAutoFoucs(CamROIAfMode see3camAfROIMode, int vidResolnWidth, int vidResolnHeight, int xCord, int yCord, int winSize);
    int setROIAutoFoucs(int see3camAfROIMode, int vidResolnWidth, int vidResolnHeight, int xCord, int yCord, int winSize);
    //int setROIAutoExposure(CamROIAutoExpMode see3camAutoexpROIMode, int vidResolnWidth, int vidResolnHeight, int xCord, int yCord, int winSize);
    int setROIAutoExposure(int see3camAutoexpROIMode, int vidResolnWidth, int vidResolnHeight, int xCord, int yCord, int winSize);

    int getAutoFocusROIModeAndWindowSize(IntByReference roiMode, IntByReference winSize);

    int setBurstLength(int burstLength);

    int setQFactor(int qFactor);

    int enableDisableAFRectangle(int enableRFRect);    

    int setToDefault();

    int setFlipHorzMode(int horizModeSel);
    int setFlipVertiMode(int vertiModeSel);

    int setStreamMode(CamStreamMode streamMode);

    int setFaceDetectionRect(int enableFaceDetectRect, int embedData, int overlayRect);

    int setSmileDetection(int enableSmileDetect, int embedData, int thresholdValue);

    int setExposureCompensation(int exposureCompValue);

    int setFrameRateCtrlValue(int frameRate);

    int enableDisableFaceRectangle(int enableFaceRect);

    /*
     * The following methods are implemented in c_see3cam_130.c but they do not return the expected values.
     * Each function returns 1 = success, 0 = failure, but the requested value is not returned.
     * Consequently, it is possible to set these properties but not read them back.
     * I only mention them as a warning not to use them. dstrong 9/11/2018.
     
    int getSceneMode();
    int getEffectMode();
    int getDenoiseValue();
    int getAutoFocusMode();
    int getiHDRMode();    
    int getAutoExpROIModeAndWindowSize();
    int getBurstLength();
    int getQFactor();
    int getAFRectMode();
    int getFlipMode();
    int getStreamMode();
    int getFaceDetectMode();
    int getSmileDetectMode();
    int getExposureCompensation();
    int getFrameRateCtrlValue();
      
     */
}
