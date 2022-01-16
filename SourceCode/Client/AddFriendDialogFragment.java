package hk.edu.cuhk.ie.iems5722.Group31;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

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

public class AddFriendDialogFragment extends DialogFragment {

    public EditText editText;
    private ImageButton imageButton;
    private ImageButton imageButton1;
    private TextView textView;
    static String ip ="34.92.175.150:80";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.inputfriendid,container, false);

        editText = view.findViewById(R.id.userid);
        imageButton = view.findViewById(R.id.imageButton);
        imageButton1 = view.findViewById(R.id.imageButton3);
        textView = view.findViewById(R.id.textView3);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = editText.getText().toString();
                addUser(input);
            }
        });

        imageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return view;
    }

    public void addUser(String input){
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://"+ip+"/api/project/add_user";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if(jsonObject.getString("status").equals("ERROR")){
                        switch (jsonObject.getString("type")){
                            case "1":
                                Toast.makeText(getActivity(),"This user is not existed", Toast.LENGTH_SHORT).show();
                                break;
                            case "2":
                                Toast.makeText(getActivity(),"This user is your friend already", Toast.LENGTH_SHORT).show();
                        }
                    } else if(jsonObject.getString("status").equals("OK")){
                        Toast.makeText(getActivity(), "Congrats!!!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
}
