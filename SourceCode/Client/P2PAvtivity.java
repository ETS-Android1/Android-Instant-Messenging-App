package hk.edu.cuhk.ie.iems5722.Group31;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

public class P2PAvtivity extends AppCompatActivity {

    private TextView LocIPadd, information, question1;
    private EditText Port, DesIP;
    private Button button_pickimage, button_pickaudio, button_pickfile, button_pickvideo, readsms;
    private ProgressBar progressBar;
    int length;
    int Progressvalue;
    private SocketManager socketManager;
    private Handler handler;
    SimpleDateFormat format;
    private static final int PICK_IMAGES_CODE = 1000;
    private static final int PICK_VIDEOS_CODE = 1001;
    private static final int PICK_AUDIOS_CODE = 1002;
    private static final int PICK_FILES_CODE = 1003;
    private static final int READ_SMS_CODE = 3000;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p2p);

        LocIPadd = (TextView) findViewById(R.id.LocIPadd);
        information = (TextView) findViewById(R.id.information);
        question1 = (TextView) findViewById(R.id.question1);
        Port = (EditText) findViewById(R.id.Port);
        DesIP = (EditText) findViewById(R.id.DesIP);
        button_pickimage = (Button) findViewById(R.id.button_pickimage);
        button_pickaudio = (Button) findViewById(R.id.button_pickaudio);
        button_pickfile = (Button) findViewById(R.id.button_pickfile);
        button_pickvideo = (Button) findViewById(R.id.button_pickvideo);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        format = new SimpleDateFormat("hh:mm:ss");
        information.setMovementMethod(ScrollingMovementMethod.getInstance());

        verifyStoragePermissions(P2PAvtivity.this);





        button_pickfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(P2PAvtivity.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowFiles(true)
                        .setShowVideos(false)
                        .setShowImages(false)
                        .setMaxSelection(-1)
                        .setSkipZeroSizeFiles(true)
                        .setSuffixes("txt", "pdf", "html", "rtf", "csv", "xml",
                                "zip", "tar", "gz", "rar", "7z", "torrent",
                                "doc", "docx", "odt", "ott",
                                "ppt", "pptx", "pps",
                                "xls", "xlsx", "ods", "ots")
                        .build());
                startActivityForResult(intent, PICK_FILES_CODE);
            }
        });

        button_pickaudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(P2PAvtivity.this, FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowVideos(false)
                        .setShowAudios(true)
                        .setShowImages(false)
                        .setMaxSelection(-1)
                        .setSkipZeroSizeFiles(true)
                        .build());

                startActivityForResult(intent, PICK_AUDIOS_CODE);
            }
        });

        button_pickimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(P2PAvtivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(P2PAvtivity.this, new String[]{Manifest.permission.CAMERA}, PICK_IMAGES_CODE);
                } else {
                    ImagePicker();
                }
            }
        });

        button_pickvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(P2PAvtivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(P2PAvtivity.this, new String[]{Manifest.permission.CAMERA}, PICK_VIDEOS_CODE);
                } else {
                    VideoPicker();
                }
            }
        });


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        information.append("\n[" + format.format(new Date()) + "]" + msg.obj.toString());
                        break;
                    case 1:
                        try {
                            LocIPadd.setText("本機IP：" + getIpAdd() + " 監聽端口:" + msg.obj.toString());
                        } catch (UnknownHostException | SocketException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2: {
                        Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 3: {
                        Progressvalue += msg.arg1;

                        progressBar.setProgress(Progressvalue); //更新進程條
                        if (Progressvalue == length) {
                            information.append("\n[" + format.format(new Date()) + "]" + "己發送成功");
                        }
                        break;
                    }
                    case 5: {
                        length = (int) msg.obj;
                        progressBar.setMax((int) msg.obj);
                        break;
                    }
                }
            }
        };

        if (ContextCompat.checkSelfPermission(P2PAvtivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(P2PAvtivity.this , new String[]{Manifest.permission.INTERNET},0304);
        }

        try {
            socketManager = new SocketManager(handler);
        } catch (IOException e) {
            System.out.println("No Socket++++++++++++++++++++++++++++++++++++++++++++");
            e.printStackTrace();
        }


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }
    private void VideoPicker() {
        Intent intent = new Intent(P2PAvtivity.this, FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setShowVideos(true)
                .setShowImages(false)
                .setMaxSelection(-1)
                .setSkipZeroSizeFiles(true)
                .enableVideoCapture(true)
                .build());

        startActivityForResult(intent, PICK_VIDEOS_CODE);
    }

    private void ImagePicker() {
        Intent intent = new Intent(P2PAvtivity.this, FilePickerActivity.class);
        intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                .setCheckPermission(true)
                .setShowImages(true)
                .setShowVideos(false)
                .enableImageCapture(true)
                .setMaxSelection(-1)
                .setSkipZeroSizeFiles(true)
                .build());
        startActivityForResult(intent, PICK_IMAGES_CODE);
    }

    /*
    提取個人IP地址
    */

    /*
請求權限的反饋動作
    */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case PICK_IMAGES_CODE: {

                if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ImagePicker();
                } else {
                    Toast.makeText(this, "You do not have required permission", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case PICK_VIDEOS_CODE: {

                if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    VideoPicker();
                } else {
                    Toast.makeText(this, "You do not have required permission", Toast.LENGTH_SHORT).show();
                }
                break;
            }

            case READ_SMS_CODE: {

                if (grantResults.length >= 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "you have permission", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((resultCode == Activity.RESULT_OK) && (data != null)) {
            final ArrayList<String> pathArrayList = new ArrayList<>();
            final ArrayList<String> filename = new ArrayList<>();
            ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);

            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();

                for (int i = 0; i < count; i++) {
                    pathArrayList.add(files.get(i).getPath());
                    filename.add(files.get(i).getName());

                }
                Message.obtain(handler, 0, "正在发送至" + DesIP.getText().toString() + ":" + Integer.parseInt(Port.getText().toString())).sendToTarget();
                Thread sendfileThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        socketManager.SendFile(filename, pathArrayList, DesIP.getText().toString(), Integer.parseInt(Port.getText().toString()));
                    }
                });
                sendfileThread.start();
            } else {
                pathArrayList.add(files.get(0).getPath());
                filename.add(files.get(0).getName());

                Message.obtain(handler, 0, "正在发送至" + DesIP.getText().toString() + ":" + Integer.parseInt(Port.getText().toString())).sendToTarget();
                Thread sendfileThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        socketManager.SendFile(filename, pathArrayList, DesIP.getText().toString(), Integer.parseInt(Port.getText().toString()));
                    }
                });
                sendfileThread.start();
            }
        }

        switch (requestCode) {

            case PICK_IMAGES_CODE: {
                Toast.makeText(getApplicationContext(), "You have chosen IMAGE TYPE of files", Toast.LENGTH_SHORT).show();
                break;
            }

            case PICK_VIDEOS_CODE: {
                Toast.makeText(getApplicationContext(), "You have chosen VIDEO TYPE of files", Toast.LENGTH_SHORT).show();
                break;
            }

            case PICK_AUDIOS_CODE: {
                Toast.makeText(getApplicationContext(), "You have chosen AUDIO TYPE of files", Toast.LENGTH_SHORT).show();
                break;
            }

            case PICK_FILES_CODE: {
                Toast.makeText(getApplicationContext(), "You have chosen FILES TYPE of files", Toast.LENGTH_SHORT).show();
                break;
            }

        }

    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public static String getIpAdd() throws SocketException, UnknownHostException {
        String ip = "";
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface intf = en.nextElement();
            String name = intf.getName();
            if (!name.contains("docker") && !name.contains("lo")) {
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    //获得IP
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ipaddress = inetAddress.getHostAddress();
                        if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {

                            System.out.println(ipaddress);
                            if (!"127.0.0.1".equals(ip)) {
                                ip = ipaddress;
                            }
                        }
                    }
                }
            }
        }
        return ip;
    }
}