package hk.edu.cuhk.ie.iems5722.Group31;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import hk.edu.cuhk.ie.iems5722.Group31.R;
//import hk.edu.cuhk.ie.iems5722.a2_1155164831.RegisterRequest;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSettings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSettings extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String username = "userName";
    private static final String ARG_PARAM2 = "param2";
    String ip = "34.92.175.150:80";
    // TODO: Rename and change types of parameters

    private String mParam2;
    public FragmentSettings() {

    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSettings.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSettings newInstance(String param1, String param2) {
        FragmentSettings fragment = new FragmentSettings();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (getArguments() != null) {
            System.out.println("getargument:"+getArguments());
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //String username="";
        //System.out.println("bundle username: "+username);
        View view = View.inflate(getActivity(),R.layout.fragment_settings,null);
        Button edit_pwd = view.findViewById(R.id.edit_pwd);
        Button P2P = view.findViewById(R.id.P2P);
        Button daynight=view.findViewById(R.id.daynight);
        TextView username = view.findViewById(R.id.username);

        P2P.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(getActivity(), P2PAvtivity.class);
                startActivity(intent);
            }
        });
        daynight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int mode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                if(mode == Configuration.UI_MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else if(mode == Configuration.UI_MODE_NIGHT_NO) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
            }
        });
        edit_pwd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                final View dialogView = View.inflate(getActivity(), R.layout.edit_pop_out, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("修改密码").setIcon(android.R.drawable.ic_dialog_info)
                        .setView(dialogView)
                        .setNegativeButton("Cancel", null);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener (){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        EditText editText2=dialogView.findViewById(R.id.input_username);
                        EditText editText=dialogView.findViewById(R.id.input_oldpwd);
                        EditText editText1=dialogView.findViewById(R.id.input_newpwd);

                        String old_pwd = editText.getText().toString();
                        String new_pwd = editText1.getText().toString();
                        String username=editText2.getText().toString();
                        editpwd(username,old_pwd,new_pwd);

                    }
                });
                builder.show();
            }
        });
        return view;
    }

    private void editpwd(String username,String old_pwd,String new_pwd){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://"+ip+"/api/project/editpwd";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");


                            if("OK".equals(status)){
                                Toast.makeText(getActivity(),"修改成功",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getActivity(),"密码错误",Toast.LENGTH_SHORT).show();
                            }
                        }  catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getMessage());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map <String, String> params = new HashMap<String,String>();
                params.put("username",username);
                params.put("password",old_pwd);
                params.put("new_pwd",new_pwd);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}


