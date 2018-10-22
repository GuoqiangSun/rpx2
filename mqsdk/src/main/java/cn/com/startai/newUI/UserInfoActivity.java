package cn.com.startai.newUI;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.AppUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import cn.com.startai.fssdk.FSDownloadCallback;
import cn.com.startai.fssdk.FSUploadCallback;
import cn.com.startai.fssdk.StartaiDownloaderManager;
import cn.com.startai.fssdk.StartaiUploaderManager;
import cn.com.startai.fssdk.db.entity.DownloadBean;
import cn.com.startai.fssdk.db.entity.UploadBean;
import cn.com.startai.mqsdk.BaseActivity;
import cn.com.startai.mqsdk.R;
import cn.com.startai.mqsdk.network.NetworkManager;
import cn.com.startai.mqsdk.network.UserIDXml;
import cn.com.startai.mqsdk.util.TAndL;
import cn.com.startai.mqsdk.util.eventbus.E_0x8016_Resp;
import cn.com.startai.mqsdk.util.eventbus.E_0x8020_Resp;
import cn.com.startai.mqsdk.util.eventbus.E_0x8024_Resp;
import cn.com.startai.mqsdk.util.zxing.PermissionHelper;
import cn.com.startai.mqttsdk.StartAI;
import cn.com.startai.mqttsdk.base.StartaiError;
import cn.com.startai.mqttsdk.busi.entity.C_0x8020;
import cn.com.startai.mqttsdk.busi.entity.C_0x8025;
import cn.com.startai.mqttsdk.listener.IOnCallListener;
import cn.com.startai.mqttsdk.mqtt.request.MqttPublishRequest;
import cn.com.startai.newUI.login.MyImageView;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date: 2018/10/17 0017
 * Desc:
 */
public class UserInfoActivity extends BaseActivity {


    TextView mNickName;
    MyImageView mImgView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newui_user);

        String name = getIntent().getStringExtra("name");

        mNickName = findViewById(R.id.nick_name);
        if (name != null) {
            mNickName.setText(name);
        }

        final String headPicPath = getIntent().getStringExtra("headPicPath");

        mImgView = findViewById(R.id.head_pic);
        mImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectLocalPhoto();

            }
        });

        if (headPicPath != null) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    setImageURL(headPicPath);
                }
            }.start();
        }


//        StartAI.getInstance().getBaseBusiManager().getUserInfo(new IOnCallListener() {
//            @Override
//            public void onSuccess(MqttPublishRequest request) {
//
//            }
//
//            @Override
//            public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
//
//            }
//
//            @Override
//            public boolean needUISafety() {
//                return false;
//            }
//        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onGetUserInfoResult(E_0x8024_Resp resp) {
        super.onGetUserInfoResult(resp);

        Tlog.v("abc", " userInfo :onGetUserInfoResult" + String.valueOf(resp));

        if (resp.getResult() == 1) {
            if (mNickName != null) {
                String nickName = resp.getMessage().getNickName();

                Tlog.v("abc", "onGetUserInfoResult nickName " + String.valueOf(nickName));

                if (mNickName != null && nickName != null) {
                    mNickName.setText(String.valueOf(nickName));
                }
            }

            final String headPic = resp.getMessage().getHeadPic();

            Tlog.v("abc", "onGetUserInfoResult headpic:" + headPic);

            if (headPic != null) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        setImageURL(headPic);
                    }
                }.start();
            }

        }

    }

    private void setImageURL(final String path) {
        //开启一个线程用于联网

        //把传过来的路径转成URL
        try {
            URL url = null;
            url = new URL(path);
            Tlog.e(TAG, " setImageURL path:" + path);
            //获取连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //使用GET方法访问网络
            connection.setRequestMethod("GET");
            //超时时间为10秒
            connection.setConnectTimeout(10000);
            //获取返回码
            int code = connection.getResponseCode();

            Tlog.e(TAG, " setImageURL bitmap0 code" + code);

            if (code == 200) {
                InputStream inputStream = connection.getInputStream();
                //使用工厂把网络的输入流生产Bitmap
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                //利用Message把图片发给Handler

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mImgView != null) {
                            mImgView.setBitmap(bitmap);
                            mImgView.setmOuterRing(56);
                            mImgView.setColor(Color.RED);
                            mImgView.setOuterRingAlpha(50);
//                            mImgView.setImageBitmap(bitmap);
                            mImgView.invalidate();
                        }
                    }
                });
                inputStream.close();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Tlog.e(TAG, " setImageURL : ", e);
        } catch (ProtocolException e) {
            e.printStackTrace();
            Tlog.e(TAG, " setImageURL : ", e);
        } catch (IOException e) {
            e.printStackTrace();
            Tlog.e(TAG, " setImageURL : ", e);
        }

    }

    private String TAG = "abc";

    public void updateAvailable(View view) {

        final String os = "android";
        String packageName = getApplicationContext().getPackageName();
        Tlog.v(TAG, "checkIsLatestVersion() :" + packageName);
        StartAI.getInstance().getBaseBusiManager().getLatestVersion(os, packageName, new IOnCallListener() {
            @Override
            public void onSuccess(MqttPublishRequest request) {

            }

            @Override
            public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                Tlog.v(TAG, "checkIsLatestVersion()onFailed :" + startaiError.getErrorMsg());
            }

            @Override
            public boolean needUISafety() {
                return false;
            }
        });
    }

    @Override
    public void onGetLatestVersionResult(E_0x8016_Resp e_0x8016_resp) {
        super.onGetLatestVersionResult(e_0x8016_resp);

        Tlog.d(TAG, " onGetLatestVersionResult :" + String.valueOf(e_0x8016_resp));

        String downloadUrl = null;

        Context applicationContext = getApplicationContext();
        if (e_0x8016_resp.getResult() == 1) {
            boolean isLatestVersion = true;
            try {
                PackageInfo packageInfo = applicationContext.getPackageManager()
                        .getPackageInfo(applicationContext.getPackageName(), 0);


                Tlog.v(TAG, " myVersionCode:" + packageInfo.versionCode + " sVersionCode:" + e_0x8016_resp.getContentBean().getVersionCode());

                isLatestVersion = packageInfo.versionCode < e_0x8016_resp.getContentBean().getVersionCode();

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            downloadUrl = e_0x8016_resp.getContentBean().getUpdateUrl();
            Tlog.v(TAG, " onGetLatestVersionResult:  downloadUrl" + downloadUrl);

            if (!isLatestVersion) {
                updateApp(downloadUrl);
            } else {
                TAndL.T(applicationContext, " is latest version");
            }

        } else {

            if ("0x801604".equalsIgnoreCase(e_0x8016_resp.getContentBean().getErrcode())) {
                TAndL.T(applicationContext, " is latest version");
            } else {
                TAndL.T(applicationContext, "update available error " + e_0x8016_resp.getContentBean().getErrcode());
            }
        }

    }


    private void updateApp(String downloadUrl) {
        Tlog.v(TAG, "updateApp() :" + downloadUrl);

        if (downloadUrl != null) {

            //示例代码
            DownloadBean downloadBean = new DownloadBean.Builder()
                    .url(downloadUrl) //需要下载的文件
//                .fileName(fileName) //文件保存名，选填
                    .build();

            getStartaiDownloaderManager().startDownload(downloadBean, mDownloadAppListener);


        }

    }


    private final FSDownloadCallback mDownloadAppListener = new FSDownloadCallback() {
        @Override
        public void onStart(DownloadBean downloadBean) {
            Tlog.v(TAG, "FSDownloadCallback  onStart() " + downloadBean.toString());
            TAndL.T(getApplicationContext(), " download app start");
        }

        @Override
        public void onSuccess(DownloadBean downloadBean) {
            Tlog.v(TAG, "FSDownloadCallback  onSuccess() " + downloadBean.toString());

            TAndL.T(getApplicationContext(), " download app success");
            AppUtils.installApp(downloadBean.getLocalPath());
            Tlog.v(TAG, " path: " + downloadBean.getLocalPath());

        }

        @Override
        public void onFailure(DownloadBean downloadBean, int i) {
            Tlog.e(TAG, "FSDownloadCallback  onFailure() " + i);

            TAndL.T(getApplicationContext(), " download app fail:" + i);
        }

        @Override
        public void onProgress(DownloadBean downloadBean) {
            Tlog.v(TAG, "FSDownloadCallback  onProgress() " + downloadBean.toString());

            int i = downloadBean.getProgress() % 5;


            if (i >= 0 && i <= 1) {
                TAndL.T(getApplicationContext(), " download app progress:" + downloadBean.getProgress() + "%");
            }

        }

        @Override
        public void onWaiting(DownloadBean downloadBean) {
            Tlog.v(TAG, "FSDownloadCallback  onWaiting() " + downloadBean);
        }

        @Override
        public void onPause(DownloadBean downloadBean) {
            Tlog.v(TAG, "FSDownloadCallback  onPause() ");
        }
    };


    private volatile boolean downloadInit = false;

    private StartaiDownloaderManager getStartaiDownloaderManager() {
        if (!downloadInit) {
            downloadInit = true;
            StartaiDownloaderManager.getInstance().init(getApplication(), null);
        }
        return StartaiDownloaderManager.getInstance();
    }


    public void changeMyName(View view) {


        // LayoutInflater是用来找layout文件夹下的xml布局文件，并且实例化
        LayoutInflater factory = LayoutInflater.from(this);
        // 把布局文件中的控件定义在View中
        final View textEntryView = factory.inflate(R.layout.dialog_inputnickname, null);

        new AlertDialog.Builder(this)
                .setTitle(" change my name")
                .setView(textEntryView)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //按下确定键后的事件

                        EditText name = textEntryView.findViewById(R.id.et_input_name);
                        String s = name.getText().toString();

                        if ("".equalsIgnoreCase(s)) {
                            TAndL.T(getApplicationContext(), " please input");
                            return;
                        }

                        C_0x8020.Req.ContentBean contentBean = new C_0x8020.Req.ContentBean();
                        contentBean.setUserid(NetworkManager.getInstance().getMqttUserID());
                        contentBean.setNickName(s);
                        StartAI.getInstance().getBaseBusiManager().updateUserInfo(contentBean, new IOnCallListener() {
                            @Override
                            public void onSuccess(MqttPublishRequest request) {

                            }

                            @Override
                            public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                                TAndL.T(getApplicationContext(), " update name msg send fail:" + startaiError.getErrorCode());
                            }

                            @Override
                            public boolean needUISafety() {
                                return false;
                            }
                        });

                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();


    }

    public void changeMyPwd(View view) {

        // LayoutInflater是用来找layout文件夹下的xml布局文件，并且实例化
        LayoutInflater factory = LayoutInflater.from(this);
        // 把布局文件中的控件定义在View中
        final View textEntryView = factory.inflate(R.layout.dialog_inputpwd, null);

        new AlertDialog.Builder(this)
                .setTitle(" change my pwd")
                .setView(textEntryView)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //按下确定键后的事件

                        EditText mOldEdt = textEntryView.findViewById(R.id.et_input_old);
                        EditText mNewEdt = textEntryView.findViewById(R.id.et_input_new);

                        String oldPwd = mOldEdt.getText().toString();
                        String newPwd = mNewEdt.getText().toString();

                        if ("".equalsIgnoreCase(oldPwd)) {
                            TAndL.T(getApplicationContext(), " please input old pwd");
                            return;
                        }

                        if ("".equalsIgnoreCase(newPwd)) {
                            TAndL.T(getApplicationContext(), " please input new pwd");
                            return;
                        }


                        StartAI.getInstance().getBaseBusiManager()
                                .updateUserPwd(oldPwd, newPwd, new IOnCallListener() {
                                    @Override
                                    public void onSuccess(MqttPublishRequest request) {

                                    }

                                    @Override
                                    public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                                        TAndL.T(getApplicationContext(), " change pwd msg send fail:" + startaiError.getErrorCode());
                                    }

                                    @Override
                                    public boolean needUISafety() {
                                        return false;
                                    }
                                });

                    }
                }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }

    @Override
    public void onUpdatePwdResult(C_0x8025.Resp resppwd) {
        super.onUpdatePwdResult(resppwd);

        if (resppwd.getResult() == 1) {
            TAndL.T(this, " update pwd success");
        } else {
            TAndL.T(this, " update pwd fail " + resppwd.getContent().getErrmsg());
        }

    }

    @Override
    public void onUpdateUserInfoResult(E_0x8020_Resp resp) {
        super.onUpdateUserInfoResult(resp);

        if (resp.getResult() == 1) {
            TAndL.T(this, " update user info success");

            if (resp.getMessage().getNickName() != null) {
                if (mNickName != null) {
                    mNickName.setText(resp.getMessage().getNickName());
                }
            }
            final String headPic = resp.getMessage().getHeadPic();
            if (headPic != null) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        setImageURL(headPic);
                    }
                }.start();
            }

        } else {
            TAndL.T(this, " update user info fail " + resp.getMessage().getErrmsg());
        }
    }

    public void logout(View view) {
        StartAI.getInstance().getBaseBusiManager().logout();
        TAndL.T(getApplication(), " logout success");
        NetworkManager.getInstance().setLoginUserID(NetworkManager.getInstance().getRandomUserID());
        UserIDXml.getInstance(this).setUserID(null);

        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("logout", true);
        //设置返回数据
        this.setResult(RESULT_OK, intent);

        this.finish();
    }

    private void selectLocalPhoto() {
        Tlog.v(TAG, "selectLocalPhoto() ");

        Intent intent;
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");

        startActivityForResult(intent, LOCAL_PHOTO_CODE);

    }


    private File localPhotoFile;

    private static final int LOCAL_PHOTO_CODE = 0x01;
    private static final int CROP_LOCAL_PHOTO = 0x02;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LOCAL_PHOTO_CODE) {
            if (resultCode == RESULT_OK) {

                final Uri imageUri = data.getData();

                Tlog.d(TAG, " onActivityResult LOCAL_PHOTO_CODE success ");

                PermissionHelper.requestStorage(new PermissionHelper.OnPermissionGrantedListener() {
                    @Override
                    public void onPermissionGranted() {

                        localPhotoFile = crop(imageUri, CROP_LOCAL_PHOTO);
                    }
                });

            } else if (resultCode != RESULT_CANCELED) {
                TAndL.T(getApplication(), " select pic error");
            }
        } else if (requestCode == CROP_LOCAL_PHOTO) {

            if (resultCode == RESULT_OK) {
                cropSuccess(localPhotoFile);
            } else if (resultCode != RESULT_CANCELED) {
                TAndL.T(getApplication(), " crop pic error");
            }
        }


    }


    // 裁剪成功
    private void cropSuccess(File path) {

        String filePath = "";

        if (path != null && path.exists()) {
            filePath = path.getAbsolutePath();
            compressImage(filePath);
        }

        Tlog.d(TAG, " onActivityResult CROP_PHOTO_SUCCESS:" + filePath);

        if (!"".equalsIgnoreCase(filePath)) {
            //示例代码
            UploadBean uploadentity = new UploadBean.Builder()
                    .localPath(String.valueOf(filePath)) //本地文件路径
                    .build();

            getStartaiUploaderManager().startUpload(uploadentity, mLogoUploadCallBack);

        }

    }


    private final FSUploadCallback mLogoUploadCallBack = new FSUploadCallback() {
        @Override
        public void onStart(UploadBean uploadBean) {
            Tlog.v(TAG, " mLogoUploadCallBack onStart " + uploadBean.toString());
            TAndL.T(getApplicationContext(), " start upload head pic");

        }

        @Override
        public void onSuccess(UploadBean uploadBean) {
            Tlog.v(TAG, " mLogoUploadCallBack onSuccess " + uploadBean.toString());

            TAndL.T(getApplicationContext(), " head pic upload success");

            C_0x8020.Req.ContentBean contentBean = new C_0x8020.Req.ContentBean();
            contentBean.setHeadPic(uploadBean.getHttpDownloadUrl());
            contentBean.setUserid(NetworkManager.getInstance().getMqttUserID());
            StartAI.getInstance().getBaseBusiManager().updateUserInfo(contentBean, new IOnCallListener() {
                @Override
                public void onSuccess(MqttPublishRequest request) {

                }

                @Override
                public void onFailed(MqttPublishRequest request, StartaiError startaiError) {
                    TAndL.T(getApplicationContext(), " update head pic fail " + startaiError.getErrorCode());
                }

                @Override
                public boolean needUISafety() {
                    return false;
                }
            });

        }

        @Override
        public void onFailure(UploadBean uploadBean, int i) {
            Tlog.v(TAG, " mLogoUploadCallBack onFailure " + i);

            TAndL.T(getApplicationContext(), " upload head pic fail " + i);
        }

        @Override
        public void onProgress(UploadBean uploadBean) {
            Tlog.v(TAG, " mLogoUploadCallBack onProgress " + uploadBean.getProgress());
        }

        @Override
        public void onWaiting(UploadBean uploadBean) {
            Tlog.v(TAG, " mLogoUploadCallBack onWaiting ");
        }

        @Override
        public void onPause(UploadBean uploadBean) {
            Tlog.v(TAG, " mLogoUploadCallBack onPause ");
        }
    };


    private volatile boolean uploadInit;

    private StartaiUploaderManager getStartaiUploaderManager() {
        if (!uploadInit) {
            uploadInit = true;
            //初始文件上传模块
            StartaiUploaderManager.getInstance().init(getApplication(), null);
        }
        return StartaiUploaderManager.getInstance();
    }


    private void compressImage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        newOpts.inJustDecodeBounds = false;
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        while (baos.toByteArray().length > 100 * 1024) { //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
            if (options < 0) {
                break;
            }
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(srcPath);
            //不断把stream的数据写文件输出流中去
            fileOutputStream.write(baos.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getRandom() {
        return String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
    }

    // 裁剪
    private File crop(Uri imageUri, int code) {

        Intent intent = new Intent("com.android.camera.action.CROP");

        String rootPathStr = Environment.getExternalStorageDirectory() + File.separator
                + "image";

        File rootPath = new File(rootPathStr);

        if (!rootPath.exists()) {
            boolean mkdirs = rootPath.mkdirs();

            if (!mkdirs) {
                return null;
            }
        }

        File path = new File(rootPath, getRandom() + ".jpg");

        Uri outUri = Uri.fromFile(path);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        Tlog.d(TAG, " crop. output:" + (outUri != null ? outUri.getPath() : " null ")
                + " input:" + (imageUri != null ? imageUri.toString() : "null"));

        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri);

        intent.putExtra("crop", "true");

//        intent.putExtra("aspectX", aspectX);
//        intent.putExtra("aspectY", aspectX);
//        intent.putExtra("outputX", outputX);
//        intent.putExtra("outputY", outputY);

        intent.putExtra("return-data", false);
        //黑边
        intent.putExtra("scale", true);
        intent.putExtra("scaleUpIfNeeded", true);


        // 启动裁剪程序
        startActivityForResult(intent, code);

        return path;
    }


}
