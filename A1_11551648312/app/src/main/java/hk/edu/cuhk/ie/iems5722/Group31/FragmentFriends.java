package hk.edu.cuhk.ie.iems5722.Group31;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentFriends#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFriends extends Fragment {

    public FriendListAdapter friendListAdapter;
    public ArrayList<FriendListModel> friendListModelArrayList;
    ListView friendListView;
    ImageButton imageButton;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentFriends() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentFriends.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentFriends newInstance(String param1, String param2) {
        FragmentFriends fragment = new FragmentFriends();
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
        View view = View.inflate(getActivity(), R.layout.fragment_friends, null);
        friendListView = (ListView) view.findViewById(R.id.friendListView);
        friendListModelArrayList = new ArrayList<>();
        friendListAdapter = new FriendListAdapter(getActivity(),
                R.layout.friendlistview,
                friendListModelArrayList);
        friendListView.setAdapter(friendListAdapter);
        imageButton = view.findViewById(R.id.addUser);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddFriendDialogFragment addFriendDialogFragment = new AddFriendDialogFragment();
                addFriendDialogFragment.show(getFragmentManager(), "AddFriendDialogFragment");
            }
        });


        getFriendList();

        // Inflate the layout for this fragment
        return view;
    }

    void getFriendList(){
        RequestQueue queue = Volley.newRequestQueue(requireActivity());
        String url = "http://"+MainActivity.ip+"/api/project/get_friends_lists?userid="+MainActivity.userID;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("status").equals("OK")){
                        JSONArray jsonArray = jsonObject.getJSONArray("friend");
                        for(int i = 0; i< jsonArray.length();i++){
                            System.out.println(jsonArray.length());
                            String text = jsonArray.getJSONObject(i).getString("username");
                            System.out.println("==========="+text+"-------------");
                            FriendListModel friendListModel = new FriendListModel(text);
                            System.out.println(friendListModel.getName());
                            friendListModelArrayList.add(friendListModel);
                        }

                        friendListAdapter.notifyDataSetChanged();
                    }

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
    }