package cn.com.startai.kp8.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;

import static android.os.Environment.MEDIA_MOUNTED;

public class FileUtil {

    public static final String TAG = FileUtil.class.getSimpleName();

    public static final String APP_ROOT_NAME = "rp8";


    public static final String SDCARD_STORAGE_PATH = Environment
            .getExternalStorageDirectory()
            //.getDataDirectory()
            .getAbsolutePath()
            + File.separator
            + APP_ROOT_NAME;
    public static final String SDCARD_STORAGE_PATH_IMAGE = SDCARD_STORAGE_PATH
            + File.separator
            + "image";
    public static final String SDCARD_STORAGE_PATH_AUDIO = SDCARD_STORAGE_PATH
            + File.separator
            + "audio";
    public static final String SDCARD_STORAGE_PATH_FILE = SDCARD_STORAGE_PATH
            + File.separator
            + "file";
    public static final String SDCARD_STORAGE_PATH_UPDATE = SDCARD_STORAGE_PATH
            + File.separator
            + "update";
    public static final String SDCARD_STORAGE_PATH_LOG = SDCARD_STORAGE_PATH
            + File.separator
            + "log";

    public static final String SDCARD_STORAGE_PATH_CAMERA = SDCARD_STORAGE_PATH
            + File.separator
            + "camera";


    public static void initFileDir() {
        File fdir = new File(SDCARD_STORAGE_PATH);
        if (!fdir.exists()) {
            fdir.mkdirs();
        }

        File imageDir = new File(SDCARD_STORAGE_PATH_IMAGE);
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }

        File audioDir = new File(SDCARD_STORAGE_PATH_AUDIO);
        if (!audioDir.exists()) {
            audioDir.mkdirs();
        }

        File updateDir = new File(SDCARD_STORAGE_PATH_UPDATE);
        if (!updateDir.exists()) {
            updateDir.mkdirs();
        }

        File logDir = new File(SDCARD_STORAGE_PATH_LOG);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }

        File cameraDir = new File(SDCARD_STORAGE_PATH_CAMERA);
        if (!cameraDir.exists()) {
            cameraDir.mkdirs();
        }


        File fileDir = new File(SDCARD_STORAGE_PATH_FILE);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

    }


    public static String getUpdateDir() {
        return SDCARD_STORAGE_PATH_UPDATE + File.separator;
    }

    public static String getUpdatePath(String versionName) {
        return getUpdateDir() + versionName + ".apk";
    }

    public static Uri saveDataToSDCard(Context context, byte[] data,
                                       String filename) {
        return saveDataToSDCard(context, data, filename, SDCARD_STORAGE_PATH);
    }

    public static Uri saveDataToSDCard(Context context, byte[] data,
                                       String filename, String dir) {
        Uri uri = null;

        FileOutputStream fileOS = null;
        try {
            File file = new File(dir, filename);
            File fdir = new File(dir);
            fdir.mkdirs();
            // file.mkdirs();
            fileOS = new FileOutputStream(file);
            fileOS.write(data);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOS != null)
                try {
                    fileOS.close();
                } catch (IOException e) {
                    ;// do nothing
                    e.printStackTrace();
                }
        }

        return uri;
    }

    public static byte[] loadDataFromSDCard(Context context, String filename) {
        byte[] data = null;
        File file = new File(SDCARD_STORAGE_PATH, filename);

        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
            int length = is.available();
            data = new byte[length];
            is.read(data);

        } catch (Exception e) {

        } finally {
            if (is != null)
                try {
                    is.close();
                } catch (IOException e) {
                    ;// do nothing
                }
        }

        return data;
    }

    public static void deleteFileFromSDCard(Context context, String filename) {
        File file = new File(SDCARD_STORAGE_PATH, filename);
        file.delete();
    }

    public static void deleteFile(String filename) {
        try {
            File file = new File(filename);
            file.delete();
        } catch (Exception ex) {
        }
    }

    public static void deleteFile(Context context, String filename, String dir) {
        File file = new File(dir, filename);
        if (file.exists())
            file.delete();
    }

    public static Uri saveDataToSDCardAppend(Context context, byte[] data,
                                             String filename) {
        return saveDataToSDCardAppend(context, data, filename,
                SDCARD_STORAGE_PATH);
    }

    public static Uri saveDataToSDCardAppend(Context context, byte[] data,
                                             String filename, String dir) {
        Uri uri = null;

        FileOutputStream fileOS = null;
        try {
            File file = new File(dir, filename);
            File fdir = new File(dir);
            fdir.mkdirs();
            // file.mkdirs();
            fileOS = new FileOutputStream(file, true);
            fileOS.write(data);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOS != null)
                try {
                    fileOS.close();
                } catch (IOException e) {
                    ;// do nothing
                    e.printStackTrace();
                }
        }

        return uri;
    }

    public static boolean checkFileExistWithFullPath(String filename) {
        if (TextUtils.isEmpty(filename))
            return false;
        File file = new File(filename);
        return file.exists();
    }

    public static boolean checkFileExist(String filename, String dir) {
        if (TextUtils.isEmpty(filename) || TextUtils.isEmpty(dir)) {
            return false;
        }
        File file = new File(dir, filename);
        if (file.exists())
            return true;
        else
            return false;
    }

    public static boolean hasFileInSDCard(Context context, String filename, String dir) {
        File file = new File(dir, filename);
        return file.exists();
    }

    public static boolean hasFileInSDCard(Context context, String filename) {
        return hasFileInSDCard(context, filename, SDCARD_STORAGE_PATH);
    }

    public static String addFileScheme(String url) {
        String result = null;
        if (url != null && url.startsWith("/")) {
            result = "file://" + url;
        } else {
            result = url;
        }
        return result;
    }

    public static File createFile(String path) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        return file;
    }


    /**
     * 使用文件通道的方式复制文件
     *
     * @param s 源文件
     * @param t 复制到的新文件
     */
    public static boolean fileChannelCopy(File s, File t) {
        boolean result = false;
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();//得到对应的文件通道
            out = fo.getChannel();//得到对应的文件通道
            in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;

    }

    /**
     * 返回 sd有效时的imageloader缓存路径
     *
     * @param context
     * @param cacheDir
     * @return
     */
    public static File getImageLoaderIndividualCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = getImageLoaderCacheDirectory(context, true);
        File individualCacheDir = new File(appCacheDir, cacheDir);
        if (!individualCacheDir.exists()) {
            if (!individualCacheDir.mkdir()) {
                individualCacheDir = appCacheDir;
            }
        }
        return individualCacheDir;
    }


    /**
     * 返回 sd无效时的imageloader缓存路径
     *
     * @param context
     * @return
     */
    public static File createImageLoaderReserveDiskCacheDir(Context context) {
        File cacheDir = getImageLoaderCacheDirectory(context, false);
        File individualDir = new File(cacheDir, IMAGE_LOADER_INDIVIDUAL_DIR_NAME);
        if (individualDir.exists() || individualDir.mkdir()) {
            cacheDir = individualDir;
        }
        return cacheDir;
    }


    /**
     * 得到imageLoader的磁盘缓存文件
     *
     * @param context
     * @return
     */
    public static File getImageLoaderCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        String externalStorageState;
        try {
            externalStorageState = Environment.getExternalStorageState();
        } catch (NullPointerException e) { // (sh)it happens (Issue #660)
            externalStorageState = "";
        } catch (IncompatibleClassChangeError e) { // (sh)it happens too (Issue #989)
            externalStorageState = "";
        }
        if (preferExternal && MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)) {
            File dataDir = new File(new File(Environment.getExternalStorageDirectory(), APP_ROOT_NAME), "data");
            appCacheDir = new File(dataDir, "cache");
            if (!appCacheDir.exists()) {
                if (!appCacheDir.mkdirs()) {
//                    LogUtil.w(TAG, "Unable to create external cache directory");
                    return null;
                }
                try {
                    new File(appCacheDir, ".nomedia").createNewFile();
                } catch (IOException e) {
//                    LogUtil.i(TAG, "Can't create \".nomedia\" file in application external cache directory");
                }
            }
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
//            LogUtil.w(TAG, "Can't define system cache directory! '%s' will be used." + cacheDirPath);
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }


    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    public static final String IMAGE_LOADER_INDIVIDUAL_DIR_NAME = "uil-images";

    public static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 读取文本文件，返回String
     *
     * @param fileName
     */
    public static String readTextFileByLines(String fileName) {
        String result = null;
        File file = new File(fileName);
        if (file.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                StringBuilder sb = new StringBuilder();
                String tempString = null;
                // 一次读入一行，直到读入null为文件结束
                while ((tempString = reader.readLine()) != null) {
                    sb.append(tempString);
                }
                result = sb.toString();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                    }
                }
            }
        }
        return result;
    }


}
