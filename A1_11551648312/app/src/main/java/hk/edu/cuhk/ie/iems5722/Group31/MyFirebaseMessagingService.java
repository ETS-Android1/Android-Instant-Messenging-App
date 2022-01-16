package hk.edu.cuhk.ie.iems5722.Group31;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFMS";
    private static final String ip = "34.92.175.150:80";
    private static final String userid = "1155164831";

    @Override
    public void onNewToken(@NonNull String s) {
        Log.d(TAG, s);
        sendRegistrationToServer(s,userid);
    }

    private void sendRegistrationToServer(String token, String userid){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://"+ip+"/api/a4/submit_push_token";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("1","Received");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("2","No!!!!!!!!");
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> params = new HashMap<String,String>();
                params.put("user_id",userid);
                params.put("token",token);

                return params;
            }
        };

        queue.add(stringRequest);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom()); // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());// Check if message contains a notification payload.
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message notification payload: " + remoteMessage.getNotification().getBody());
        }
        sendNotification(remoteMessage.getNotification());
    }

    private void sendNotification(RemoteMessage.Notification remoteNotif) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

        Notification notif = new NotificationCompat.Builder(this, "FCMYOYOYO")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(remoteNotif.getTitle())
                .setContentText(remoteNotif.getBody())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(0,notif);

    }
}
