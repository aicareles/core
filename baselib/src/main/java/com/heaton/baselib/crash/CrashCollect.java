package com.heaton.baselib.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.heaton.baselib.Constance;
import com.heaton.baselib.utils.AppUtils;
import com.heaton.baselib.utils.FileUtils;
import com.heaton.baselib.utils.LogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: jerry
 * date: 20-4-30
 * email: superliu0911@gmail.com
 * des: 崩溃信息收集类
 */
public class CrashCollect {
    //异常信息
    public static final String EXCEPETION_INFOS_STRING = "EXCEPETION_INFOS_STRING";
    //应用包信息
    public static final String PACKAGE_INFOS_MAP = "PACKAGE_INFOS_MAP";
    //设备数据信息
    public static final String BUILD_INFOS_MAP = "BUILD_INFOS_MAP";
    //系统常规配置信息
    public static final String SYSTEM_INFOS_MAP = "SYSTEM_INFOS_MAP";
    //手机安全配置信息
    public static final String SECURE_INFOS_MAP = "SECURE_INFOS_MAP";
    //内存情况信息
    public static final String MEMORY_INFOS_STRING = "MEMORY_INFOS_STRING";
    public static final String VERSION_NAME = "versionName";
    public static final String VERSION_CODE = "versionCode";

    private Context context;
    private static File crashFile;
    public static final String CRASH_LOG_NAME = "crash_log.txt";

    //用来存储设备信息和异常信息
    private ConcurrentHashMap<String, Object> infos = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> packageInfos = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> deviceInfos = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> systemInfos = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> secureInfos = new ConcurrentHashMap<>();
    private String exceptionInfos;

    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    CrashCollect(Context context) {
        this.context = context;
        crashFile = FileUtils.getFilePath(this.context, "log");
    }

    public static File getCrashLogFile(){
        return new File(crashFile, CRASH_LOG_NAME);
    }

    CrashInfo collectCrashInfo(Throwable ex) {
        CrashInfo crashInfo = new CrashInfo();
        crashInfo.setAppPackage(AppUtils.getPackageName(context));
        crashInfo.setAppChannel(AppUtils.getAppMetaData(context, "HEATON_CHANNEL"));
        crashInfo.setPhoneSystem(Constance.APP.PLATFORM);
        crashInfo.setPhoneBrands(AppUtils.getDeviceBrand());
        crashInfo.setPhoneModel(AppUtils.getSystemModel());
        crashInfo.setPhoneSystemVersion(AppUtils.getSystemVersion());
        crashInfo.setAppVersionName(AppUtils.getVersionName(context));
        crashInfo.setAppVersionCode(String.valueOf(AppUtils.getVersionCode(context)));
        if (ex != null){
            crashInfo.setExceptionInfo(collectExceptionInfos(ex));
        }else {
            File file = getCrashLogFile();
            if (file.exists()){
                String crashMessage = FileUtils.getFileContent(file);
                crashInfo.setExceptionInfo(crashMessage);
            }
        }
        return crashInfo;
    }

    void saveCrashInfoToFile(String exceptionInfo) {
        // 保存文件，设置文件名
        File file = new File(crashFile, CRASH_LOG_NAME);
        try {
            LogUtils.logi("CrashHandler>>>[saveCrashInfo2File]: "+file);
            FileOutputStream mFileOutputStream = new FileOutputStream(file);
            mFileOutputStream.write(exceptionInfo.getBytes());
            mFileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取设备参数信息
     *
     * @param context
     */
    public void collectInfos(Context context, Throwable ex) {
        exceptionInfos = collectExceptionInfos(ex);
        collectPackageInfos(context);
        collectBuildInfos();
        collectSystemInfos();
        collectSecureInfos();
        String memInfos = collectMemInfos();
        //将信息储存到一个总的Map中提供给上传动作回调
        infos.put(EXCEPETION_INFOS_STRING, exceptionInfos);
        infos.put(PACKAGE_INFOS_MAP, packageInfos);
        infos.put(BUILD_INFOS_MAP, deviceInfos);
        infos.put(SYSTEM_INFOS_MAP, systemInfos);
        infos.put(SECURE_INFOS_MAP, secureInfos);
        infos.put(MEMORY_INFOS_STRING, memInfos);
    }

    /**
     * 将崩溃日志信息写入本地文件
     */
    private void saveCrashInfo2File() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < infos.size(); i++) {
            stringBuilder.append(infos.values());;
        }
        // 保存文件，设置文件名
        File file = new File(crashFile, CRASH_LOG_NAME);
        try {
            LogUtils.logi("RCrashHandler>>>[saveCrashInfo2File]: "+file);
            FileOutputStream mFileOutputStream = new FileOutputStream(file);
            mFileOutputStream.write(stringBuilder.toString().getBytes());
            mFileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取捕获异常的信息
     * @param ex
     */
    private String collectExceptionInfos(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        ex.printStackTrace();
        Throwable throwable = ex.getCause();
        // 迭代栈队列把所有的异常信息写入writer中
        while (throwable != null) {
            throwable.printStackTrace(printWriter);
            // 换行 每个个异常栈之间换行
            printWriter.append("\r\n");
            throwable = throwable.getCause();
        }
        // 记得关闭
        printWriter.close();
        return writer.toString();
    }

    /**
     * 获取内存信息
     */
    private String collectMemInfos() {
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();

        ArrayList<String> commandLine = new ArrayList<>();
        commandLine.add("dumpsys");
        commandLine.add("meminfo");
        commandLine.add(Integer.toString(android.os.Process.myPid()));
        try {
            Process process = Runtime.getRuntime()
                    .exec(commandLine.toArray(new String[commandLine.size()]));
            br = new BufferedReader(new InputStreamReader(process.getInputStream()), 8192);

            while (true) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取系统安全设置信息
     */
    private void collectSecureInfos() {
        Field[] fields = Settings.Secure.class.getFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Deprecated.class)
                    && field.getType() == String.class
                    && field.getName().startsWith("WIFI_AP")) {
                try {
                    String value = Settings.Secure.getString(context.getContentResolver(), (String) field.get(null));
                    if (value != null) {
                        secureInfos.put(field.getName(), value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取系统常规设定属性
     */
    private void collectSystemInfos() {
        Field[] fields = Settings.System.class.getFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Deprecated.class)
                    && field.getType() == String.class) {
                try {
                    String value = Settings.System.getString(context.getContentResolver(), (String) field.get(null));
                    if (value != null) {
                        systemInfos.put(field.getName(), value);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从系统属性中提取设备硬件和版本信息
     */
    private void collectBuildInfos() {
        // 反射机制
        Field[] mFields = Build.class.getDeclaredFields();
        // 迭代Build的字段key-value 此处的信息主要是为了在服务器端手机各种版本手机报错的原因
        for (Field field : mFields) {
            try {
                field.setAccessible(true);
                deviceInfos.put(field.getName(), field.get("").toString());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取应用包参数信息
     */
    private void collectPackageInfos(Context context) {
        try {
            // 获得包管理器
            PackageManager mPackageManager = context.getPackageManager();
            // 得到该应用的信息，即主Activity
            PackageInfo mPackageInfo = mPackageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (mPackageInfo != null) {
                String versionName = mPackageInfo.versionName == null ? "null" : mPackageInfo.versionName;
                String versionCode = mPackageInfo.versionCode + "";
                packageInfos.put(VERSION_NAME, versionName);
                packageInfos.put(VERSION_CODE, versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将HashMap遍历转换成StringBuffer
     */
    @NonNull
    public static StringBuffer getInfoStr(ConcurrentHashMap<String, String> infos) {
        StringBuffer mStringBuffer = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            mStringBuffer.append(key + "=" + value + "\r\n");
        }
        return mStringBuffer;
    }
}
