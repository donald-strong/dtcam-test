package efcom;

import com.sun.jna.Library;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public interface DtCam extends Library {
//  int DTCam_Start(int width, int height, int fps);
    int DTCam_Start(int width, int height, int fps);
//  int DTCam_Start_Video(const char *video, int width, int height, int fps)
    int DTCam_Start_Video(String video, int width, int height, int fps);
//  int DTCam_State();
    int DTCam_State();
//  int DTCam_Grab(void **ppBuf, int *pSize);
    int DTCam_Grab(PointerByReference bufp, IntByReference lenp);
//  int DTCam_SetWhiteBalance(int whitebalance);
    int DTCam_SetWhiteBalance(int whitebalance);
//  int DTCam_GetWhiteBalance(int *whitebalance);
    int DTCam_GetWhiteBalance(IntByReference whitebalance);
//  int DTCam_SetExposure(int exposure);
    int DTCam_SetExposure(int exposure);
//  int DTCam_GetExposure(int *exposure);
    int DTCam_GetExposure(IntByReference exposure);
//    int DTCam_SetFocus(int focus);
    int DTCam_SetFocus(int focus);
//    int DTCam_GetFocus(int *focus);
    int DTCam_GetFocus(IntByReference focus);
//  int DTCam_Stop();
    int DTCam_Stop();
}
