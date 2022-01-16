package hk.edu.cuhk.ie.iems5722.Group31;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements NfcAdapter.CreateNdefMessageCallback {

    static String ip ="34.92.175.150:80";
    static String userName = null;
    static String userID = null;

    FragmentChatrooms fragmentChatrooms = new FragmentChatrooms();
    FragmentFriends fragmentFriends = new FragmentFriends();
    FragmentSettings fragmentSettings = new FragmentSettings();
    BottomNavigationView bottomNavigationView;


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

            if(this.getIntent() != null){
                Intent intent = this.getIntent();
                if(intent != null){
                    if(intent.getType()!=null){
                        if(intent.getType().equals("1")){
                            Bundle bundle = intent.getExtras();
                            userName = bundle.getString("userName");
                            userID = bundle.getString("usedID");
                        }
                    }
                }
            }



        Bundle bundle1 = new Bundle();
        bundle1.putString("userName", userName);
        bundle1.putString("userID",userID);
        fragmentChatrooms.setArguments(bundle1);

        bottomNavigationView.setSelectedItemId(R.id.chatroom);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.chatroom:
                    getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,fragmentChatrooms).commit();
                    return true;
                case R.id.friend:
                    getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,fragmentFriends).commit();
                    return true;
                case R.id.setting:
                    getSupportFragmentManager().beginTransaction().replace(R.id.flFragment,fragmentSettings).commit();
                    return true;
            }
            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.chatroom);

        createNotificationChannel();


        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
        if(nfcAdapter == null){
            Toast.makeText(this, "NFC does not support on this device.",Toast.LENGTH_SHORT).show();
        } else         nfcAdapter.setNdefPushMessageCallback(this, this);

        verifyStoragePermissions(MainActivity.this);
        //isGooglePlayServicesAvailable(MainActivity.this);
    }

//    public boolean isGooglePlayServicesAvailable(Activity activity){
//        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
//        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
//        if (status != ConnectionResult.SUCCESS){
//            if (googleApiAvailability.isUserResolvableError(status)){
//                googleApiAvailability.getErrorDialog(activity,status,9000).show();
//            } return false;
//        }
//        return true;
//    }


    @Override
    protected void onResume() {
        super.onResume();
        //isGooglePlayServicesAvailable(MainActivity.this);
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel("FCMYOYOYO",name,importance);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {

            String payload = userName + "|" + userID;
            NdefRecord mimeRecord = NdefRecord.createMime("application/hk.edu.cuhk.ie.iems5722.a2_1155164831.beam", payload.getBytes());
            NdefMessage ndefMessage = new NdefMessage(mimeRecord);
            return ndefMessage;
        }
        return null;
    }

    void processIntent(Intent intent) {
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())){
            Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null){
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i=0; i<rawMessages.length; i++){
                    messages[i] = (NdefMessage) rawMessages[i];
                }

                String line = new String(messages[0].getRecords()[0].getPayload());
                String[] split = line.split("|");
                String username = split[0].trim();
                String userID = split[1].trim();

                Toast.makeText(this, "User: "+ username + " send a friend request to you!", Toast.LENGTH_SHORT).show();
                addUser(userID);
            }
        }
    }

    public void addUser(String input){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://"+ip+"/api/project/add_user";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> params = new HashMap<String,String>();
                params.put("userid",MainActivity.userID);
                params.put("friendid",input);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
