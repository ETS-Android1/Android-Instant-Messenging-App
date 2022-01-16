package hk.edu.cuhk.ie.iems5722.Group31;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentChatrooms#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentChatrooms extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatroomListsAdapter chatroom_adapter;
    public ArrayList<ChatroomModal> chatroom_name;
    public HashMap<String,Integer> intent_chatroom;
    TextView header;
    String ip ="34.92.175.150";
    ListView chatroom_list;
    String userName = null;
    String userID = null;

    public FragmentChatrooms() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment try1try.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentChatrooms newInstance(String param1, String param2) {
        FragmentChatrooms fragment = new FragmentChatrooms();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = View.inflate(getActivity(),R.layout.fragment_chatroom,null);
        chatroom_list = (ListView)view.findViewById(R.id.chatroom_list);
        chatroom_name = new ArrayList<>();
        intent_chatroom = new HashMap<>();
        chatroom_adapter = new ChatroomListsAdapter(getActivity(),R.layout.chatroom_layout,chatroom_name);
        chatroom_list.setAdapter(chatroom_adapter);

        Bundle bundle = getArguments();
        userName = bundle.getString("userName");
        userID = bundle.getString("userID");
        System.out.println("============"+userName+"============"+userID+"============");


        chatroom_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(R.id.chatroom_item);
                String intent_cr = textView.getText().toString().trim();
                int intent_id = intent_chatroom.get(intent_cr);
                openChatroom(intent_cr, intent_id);
            }
        });

        getChatroom();
        // Inflate the layout for this fragment
        return view;
    }

    private void openChatroom(String intent_cr, int intend_id){
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("chatroom_name",intent_cr);
        intent.putExtra("chatroom_id",intend_id);
        intent.putExtra("userID", userID);
        startActivity(intent);
    }

    private void getChatroom(){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://"+ip+"/api/a3/get_chatrooms?userid="+MainActivity.userID;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONArray jsonArray = null;
                try {
                    jsonArray = jsonObject.getJSONArray("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < jsonArray.length(); i++) {
                    String name = null;
                    int id = 0;
                    try {
                        name = jsonArray.getJSONObject(i).getString("name");
                        System.out.println(name + "Fragment");
                        id = jsonArray.getJSONObject(i).getInt("id");
                        intent_chatroom.put(name,id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ChatroomModal chatroomModal = new ChatroomModal(name, id);
                    chatroom_adapter.add(chatroomModal);
                }
                chatroom_adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });

        queue.add(stringRequest);
    }
}