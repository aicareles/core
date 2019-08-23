package csh.tiro.cc.fft;

/**
 * Created by chenshanhuai on 2015/12/26.
 */

//产生C/C++头文件的方法
//cd app\src\main
//javah -d jni -classpath F:\android\sdk\platforms\android-23\android.jar;..\..\build\intermediates\classes\debug csh.tiro.cc.fishblow.bitmapmatt

public class int16FFT {
    static {
        System.loadLibrary("int16fft");
    }
    public static float getPowerlevel(byte[] data)
    {
        float level = .0f;

        return level;
    }
    public static native void WindowCalc(short Win_Array[], char SE_data);
    public static native void IntFFT(short ReArray[], short ImArray[]);
    public static native void BitReverse(short BR_Array[]);

}
