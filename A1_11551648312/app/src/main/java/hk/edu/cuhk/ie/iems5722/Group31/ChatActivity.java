package hk.edu.cuhk.ie.iems5722.Group31;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    String chatroom_name;
    int chatroom_id;

    private ImageButton refresh;
    private ImageButton imageButton2;
    private TextView username;
    private ImageButton send_button;
    public EditText inputData;
    private ListView chat_message;
    public ArrayList<MessageModel> messageLists;
    MessageListsAdapter adapter;
    int current_page=1;
    int total_pages=0;
    String ip = "34.92.175.150";
    String user_id = null;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    FragmentSendingData fragmentSendingData = new FragmentSendingData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        username = (TextView) findViewById(R.id.username);
        send_button = (ImageButton) findViewById(R.id.send_button);
        refresh = (ImageButton) findViewById(R.id.refresh);
        inputData = (EditText) findViewById(R.id.inputData);
        chat_message = (ListView) findViewById(R.id.chat_message);
        imageButton2 = (ImageButton) findViewById(R.id.imageButton1);

        FrameLayout layout = findViewById(R.id.datatype);
        layout.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().replace(R.id.datatype,fragmentSendingData).commit();

        final int[] count = {0};

        imageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count[0] %2==0){
                    layout.setVisibility(View.VISIBLE);
                } else layout.setVisibility(View.GONE);
                count[0]++;
            }
        });

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendmessage();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageLists.clear();
                current_page = 1;
                getmessagefrom(chatroom_id,current_page);
            }
        });

        Bundle bundle = this.getIntent().getExtras();

        chatroom_name = bundle.getString("chatroom_name");
        chatroom_id = bundle.getInt("chatroom_id", 0);
        user_id = bundle.getString("userID");
        System.out.println("============"+chatroom_name+"============"+user_id+"============");
        getmessagefrom(chatroom_id,current_page);
        username.setText(chatroom_name);
        messageLists = new ArrayList<>();
        adapter = new MessageListsAdapter(this, R.layout.chat_content, messageLists);
        chat_message.setAdapter(adapter);
        chat_message.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount > 0){
                    if(current_page<total_pages){
                        current_page++;
                        getmessagefrom(chatroom_id,current_page);
                        chat_message.setSelection(messageLists.size()-1);
                    }
                }
            }
        });

        verifyStoragePermissions(ChatActivity.this);

    }

    void sendmessage() {

        if (inputData.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "You can't send null message", Toast.LENGTH_SHORT).show();
        } else {
            String textMessage = inputData.getText().toString().trim();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
            String date = sdf.format(new Date());
            MessageModel messageModal = new MessageModel(textMessage, date, MainActivity.userName);
            messageLists.add(messageModal);
            adapter.notifyDataSetChanged();
            inputData.setText(null);

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "http://"+ip+"/api/a3/send_message";
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
                    params.put("chatroom_id",Integer.toString(chatroom_id));
                    params.put("user_id",MainActivity.userID);
                    params.put("name",MainActivity.userName);
                    params.put("message",textMessage);

                    return params;
                }
            };

            queue.add(stringRequest);
        }
    }

    void getmessagefrom (int id,int current_page){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://"+ip+"/api/a3/get_messages?chatroom_id="+ id +"&page="+current_page;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ArrayList<MessageModel> tmp = new ArrayList<>();
                System.out.println("============="+response+"============");
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    if (jsonObject.getString("status").equals("OK")){
                        try {
                            total_pages = jsonObject.getJSONObject("data").getInt("total_pages");
                            System.out.println("============="+total_pages+"============");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JSONArray jsonArray1 = null;
                        try {
                            jsonArray1 = jsonObject.getJSONObject("data").getJSONArray("messages");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (int i = jsonArray1.length()-1; i > 0; i--){
                            String message = null;
                            try {
                                message = jsonArray1.getJSONObject(i).getString("message");
                                System.out.println("============="+message+"============");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String name = null;
                            try {
                                name = jsonArray1.getJSONObject(i).getString("name");
                                System.out.println("============="+name+"============");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String message_time = null;
                            try {
                                message_time = jsonArray1.getJSONObject(i).getString("message_time");
                                System.out.println("============="+message_time+"============");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            MessageModel messageModel = new MessageModel(message,message_time,name);
                            tmp.add(messageModel);
                        }

                        messageLists.addAll(tmp);
                        sortmessagelist(messageLists);
                        adapter.notifyDataSetChanged();
                    } else System.out.println("唔撚係掛");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);
    }

    void sortmessagelist(List<MessageModel> list){
        Collections.sort(list, new Comparator<MessageModel>() {
            @Override
            public int compare(MessageModel o1, MessageModel o2) {
                //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
                Date date1 = null;
                try {
                    date1 = simpleDateFormat.parse(o1.time_now);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Date date2 = null;
                try {
                    date2 = simpleDateFormat.parse(o2.time_now);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (date1.getTime() > date2.getTime()){
                    return 1;
                } else if(date1.getTime() < date2.getTime()){
                    return -1;
                } else return 0;
            }
        });
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