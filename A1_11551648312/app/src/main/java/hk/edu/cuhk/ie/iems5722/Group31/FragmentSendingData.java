package hk.edu.cuhk.ie.iems5722.Group31;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

public class FragmentSendingData extends Fragment {

    EditText IPedittext;
    EditText Portedittext;

    public SocketManager socketManager;

    private static final int PICK_IMAGES_CODE = 1000;
    private static final int PICK_VIDEOS_CODE = 1001;
    private static final int PICK_AUDIOS_CODE = 1002;
    private static final int PICK_FILES_CODE = 1003;

    private ArrayList<MediaFile> mediaFiles = new ArrayList<>();

    public FragmentSendingData(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(),R.layout.fragmentsendingdata,null);
        ImageButton imageButton0 = view.findViewById(R.id.imageButton0);
        ImageButton imageButton1 = view.findViewById(R.id.imageButton1);
        ImageButton imageButton2 = view.findViewById(R.id.imageButton2);
        ImageButton imageButton3 = view.findViewById(R.id.imageButton3);
        IPedittext = view.findViewById(R.id.IPedittext);
        Portedittext = view.findViewById(R.id.Portedittext);

        IPedittext.setText("IP!!IP!!");
        Portedittext.setText("PORT!!PORT!!");


        imageButton0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FilePickerActivity.class);
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
        });

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FilePickerActivity.class);
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
        });

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FilePickerActivity.class);
                intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                        .setCheckPermission(true)
                        .setShowVideos(false)
                        .setShowAudios(true)
                        .setShowFiles(true)
                        .setShowImages(false)
                        .setMaxSelection(-1)
                        .setSkipZeroSizeFiles(true)
                        .build());

                startActivityForResult(intent, PICK_AUDIOS_CODE);
            }
        });

        imageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FilePickerActivity.class);
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

        Handler handler = new Handler();

        try {
            socketManager = new SocketManager(handler);
        } catch (IOException e) {
            System.out.println("No Socket++++++++++++++++++++++++++++++++++++++++++++");
            e.printStackTrace();
        }

        TextView IPPORT = view.findViewById(R.id.IPPORT);
        try {
            IPPORT.setText("Your IP: " + getIpAdd() + ":" + socketManager.port);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((resultCode == Activity.RESULT_OK) && (data != null)) {
            final ArrayList<String> pathArrayList = new ArrayList<>();
            final ArrayList<String> filename = new ArrayList<>();
            ArrayList<MediaFile> files = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);


            if (true ){
                String DestinationIP = IPedittext.getText().toString();
               int port = Integer.parseInt(Portedittext.getText().toString());
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();

                    for (int i = 0; i < count; i++) {
                        pathArrayList.add(files.get(i).getPath());
                        filename.add(files.get(i).getName());
                    }
                    Thread sendfileThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            socketManager.SendFile(filename, pathArrayList, DestinationIP, port);
                        }
                    });
                    sendfileThread.start();
                } else {
                    pathArrayList.add(files.get(0).getPath());
                    filename.add(files.get(0).getName());
                    System.out.println(files.get(0).getName());
                    System.out.println(files.get(0).getPath());

                    Thread sendfileThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                           socketManager.SendFile(filename, pathArrayList, DestinationIP, port);
                        }
                    });
                    sendfileThread.start();
                }
                switch (requestCode) {

                    case PICK_IMAGES_CODE: {
                        Toast.makeText(getActivity(), "You have chosen IMAGE TYPE of files", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    case PICK_VIDEOS_CODE: {
                        Toast.makeText(getActivity(), "You have chosen VIDEO TYPE of files", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    case PICK_AUDIOS_CODE: {
                        Toast.makeText(getActivity(), "You have chosen AUDIO TYPE of files", Toast.LENGTH_SHORT).show();
                        break;
                    }

                    case PICK_FILES_CODE: {
                        Toast.makeText(getActivity(), "You have chosen FILES TYPE of files", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            } else Toast.makeText(getActivity(),"You have not type in the ip or port", Toast.LENGTH_SHORT);
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
