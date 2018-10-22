package cn.com.startai.kp8.crash;

import android.content.Context;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import cn.com.startai.kp8.util.FileUtil;

public class CrashExceptionHandle implements Thread.UncaughtExceptionHandler {


    private final static String TAG = CrashExceptionHandle.class.getSimpleName();
    public final static String KEY_JSON_ERROR_STACK = "error_stack";


    Context mContext;
    // 用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss");
    // CrashHandler实例
    private static CrashExceptionHandle instance;
    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;


    private CrashExceptionHandle() {
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }


    public synchronized static CrashExceptionHandle getIntance() {
        if (instance == null) {
            instance = new CrashExceptionHandle();
        }
        return instance;
    }


    public void init(Context ctx) {
        mContext = ctx;
    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(thread, ex);
        mDefaultHandler.uncaughtException(thread, ex);// 系统默认异常处理器
    }


    private boolean handleException(final Thread thread, final Throwable ex) {
        if (ex == null) {
            return false;
        }
        //LogUtil.e(TAG, "crash", ex);
        new Thread() {
            @Override
            public void run() {
                saveCrashLog2File(ex);
            }
        }.start();
        saveCrashLog2File(ex);
        return true;
    }


    private void saveCrashLog2File(Throwable ex) {
        String time = formatter.format(System.currentTimeMillis());
        String filename = "crash_" + time + ".log";
        JSONObject messageRootObject = new JSONObject();
        try {
            messageRootObject.put("@timestamp", time);
            messageRootObject.put("error", ex.toString());
            messageRootObject.put(KEY_JSON_ERROR_STACK, buildStackTraceFromException(ex));
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuilder builder = new StringBuilder();
        builder.append(messageRootObject.toString()).append("\n");
        write(filename, builder.toString(), false);
    }


    private String buildStackTraceFromException(Throwable ex) {
        String context = null;
        if (ex != null) {
            context = ex.toString() + "\n";
            StackTraceElement[] ste = ex.getStackTrace();
            for (int i = 0; i < ste.length; i++) {
                context += " at " + ste[i].toString() + "\n";
            }
            Throwable cex = ex.getCause();
            if (cex != null) {
                ste = cex.getStackTrace();
                context += "Cased by: " + cex.toString() + "\n";
                for (int i = 0; i < ste.length; i++) {
                    context += " at " + ste[i].toString() + "\n";
                }
            }
        }
        return context;
    }


    /**
     * 写入文件,并将文件路径写入tray
     *
     * @param fileName
     * @param content
     * @param append
     */
    private void write(String fileName, String content, boolean append) {
        File file = new File(FileUtil.SDCARD_STORAGE_PATH_LOG, fileName);
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().getParentFile().exists()) {
                file.getParentFile().getParentFile().mkdir();
            }
            file.getParentFile().mkdir();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
            }
        }
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append)));
            out.write(content);
        } catch (Exception e) {
            e.getCause();

        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                    out = null;
                }
            } catch (IOException e) {


            }
        }
    }
}