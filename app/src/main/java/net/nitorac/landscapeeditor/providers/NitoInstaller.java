package net.nitorac.landscapeeditor.providers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NitoInstaller extends Handler {
    private static final String TAG = "NitoInstaller";
    private static volatile NitoInstaller mNitoInstaller;
    private Context mContext;
    private String mTempPath;
    private NitoInstaller.MODE mMode;
    private NitoInstaller.OnStateChangedListener mOnStateChangedListener;

    private NitoInstaller(Context context) {
        this.mTempPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        this.mMode = NitoInstaller.MODE.BOTH;
        this.mContext = context;
    }

    public static NitoInstaller getDefault(Context context) {
        if (mNitoInstaller == null) {
            Class var1 = NitoInstaller.class;
            synchronized (NitoInstaller.class) {
                if (mNitoInstaller == null) {
                    mNitoInstaller = new NitoInstaller(context);
                }
            }
        }

        return mNitoInstaller;
    }

    public void setOnStateChangedListener(NitoInstaller.OnStateChangedListener onStateChangedListener) {
        this.mOnStateChangedListener = onStateChangedListener;
    }

    private boolean installUseRoot(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            throw new IllegalArgumentException("Please check apk file path!");
        } else {
            boolean result = false;
            Process process = null;
            OutputStream outputStream = null;
            BufferedReader errorStream = null;

            try {
                process = Runtime.getRuntime().exec("su");
                outputStream = process.getOutputStream();
                String command = "pm install -r " + filePath + "\n";
                outputStream.write(command.getBytes());
                outputStream.flush();
                outputStream.write("exit\n".getBytes());
                outputStream.flush();
                process.waitFor();
                errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                StringBuilder msg = new StringBuilder();

                String line;
                while ((line = errorStream.readLine()) != null) {
                    msg.append(line);
                }

                Log.d("NitoInstaller", "install msg is " + msg);
                if (!msg.toString().contains("Failure")) {
                    result = true;
                }
            } catch (Exception var17) {
                Log.e("NitoInstaller", var17.getMessage(), var17);
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }

                    if (errorStream != null) {
                        errorStream.close();
                    }
                } catch (IOException var16) {
                    outputStream = null;
                    errorStream = null;
                    process.destroy();
                }

            }

            return result;
        }
    }

    private void installUseAS(String filePath) {
        Uri uri = FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".fileprovider", new File(filePath));
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        this.mContext.startActivity(intent);
        /*if (!this.isAccessibilitySettingsOn(this.mContext)) {
            this.toAccessibilityService();
            this.sendEmptyMessage(3);
        }*/

    }

    public void install(final String filePath) {
        if (!TextUtils.isEmpty(filePath) && filePath.endsWith(".apk")) {
            (new Thread(() -> {
                NitoInstaller.this.sendEmptyMessage(1);
                switch (NitoInstaller.this.mMode) {
                    case BOTH:
                        /*fefzf*/
                        break;
                    case ROOT_ONLY:
                        NitoInstaller.this.installUseRoot(filePath);
                        break;
                    case AUTO_ONLY:
                        NitoInstaller.this.installUseAS(filePath);
                }

                NitoInstaller.this.sendEmptyMessage(0);
            })).start();
        } else {
            throw new IllegalArgumentException("not a correct apk file path");
        }
    }

    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case 0:
                if (this.mOnStateChangedListener != null) {
                    this.mOnStateChangedListener.onComplete();
                }
                break;
            case 1:
                if (this.mOnStateChangedListener != null) {
                    this.mOnStateChangedListener.onStart();
                }
            case 2:
            default:
                break;
            case 3:
                if (this.mOnStateChangedListener != null) {
                    this.mOnStateChangedListener.onNeed2OpenService();
                }
        }

    }

    public void install(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file is null");
        } else {
            this.install(file.getAbsolutePath());
        }
    }

    public void installFromUrl(final String httpUrl) {
        (new Thread(new Runnable() {
            public void run() {
                NitoInstaller.this.sendEmptyMessage(1);
                File file = NitoInstaller.this.downLoadFile(httpUrl);
                NitoInstaller.this.install(file);
            }
        })).start();
    }

    private File downLoadFile(String httpUrl) {
        if (TextUtils.isEmpty(httpUrl)) {
            throw new IllegalArgumentException();
        } else {
            File file = new File(this.mTempPath);
            if (!file.exists()) {
                file.mkdirs();
            }

            file = new File(this.mTempPath + File.separator + "update.apk");
            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(httpUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                connection.connect();
                inputStream = connection.getInputStream();
                outputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                boolean var8 = false;

                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }
            } catch (Exception var17) {
                var17.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }

                    if (outputStream != null) {
                        outputStream.close();
                    }

                    if (connection != null) {
                        connection.disconnect();
                    }
                } catch (IOException var16) {
                    inputStream = null;
                    outputStream = null;
                }

            }

            return file;
        }
    }

    public static class Builder {
        private NitoInstaller.MODE mode;
        private Context context;
        private NitoInstaller.OnStateChangedListener onStateChangedListener;
        private String directory;

        public Builder(Context c) {
            this.mode = NitoInstaller.MODE.BOTH;
            this.directory = Environment.getExternalStorageDirectory().getAbsolutePath();
            this.context = c;
        }

        public NitoInstaller.Builder setMode(NitoInstaller.MODE m) {
            this.mode = m;
            return this;
        }

        public NitoInstaller.Builder setOnStateChangedListener(NitoInstaller.OnStateChangedListener o) {
            this.onStateChangedListener = o;
            return this;
        }

        public NitoInstaller.Builder setCacheDirectory(String path) {
            this.directory = path;
            return this;
        }

        public NitoInstaller build() {
            NitoInstaller NitoInstaller = new NitoInstaller(this.context);
            NitoInstaller.mMode = this.mode;
            NitoInstaller.mOnStateChangedListener = this.onStateChangedListener;
            NitoInstaller.mTempPath = this.directory;
            return NitoInstaller;
        }
    }

    public interface OnStateChangedListener {
        void onStart();

        void onComplete();

        void onNeed2OpenService();
    }

    public static enum MODE {
        ROOT_ONLY,
        AUTO_ONLY,
        BOTH;

        private MODE() {
        }
    }
}
