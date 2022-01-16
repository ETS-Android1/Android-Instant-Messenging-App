package hk.edu.cuhk.ie.iems5722.Group31;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    private UserInfo userInfo;

    private CheckBox checkBox1;

    String ip = "34.92.175.150:80";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        checkBox1 = (CheckBox) findViewById(R.id.checkBox1);
        Button login = findViewById(R.id.login);
        Button register = findViewById(R.id.register);
        final EditText usernameEd = findViewById(R.id.username);
        final EditText passwordEd = findViewById(R.id.password);
        passwordEd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if (isChecked) {
                    passwordEd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    passwordEd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = String.valueOf(usernameEd.getText());
                String password = String.valueOf(passwordEd.getText());
                login(username,password);

                usernameEd.setText("");
                passwordEd.setText("");


            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText username = new EditText(LoginActivity.this);
                final EditText password = new EditText(LoginActivity.this);
                final View dialogView = View.inflate(LoginActivity.this, R.layout.register_pop_out, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("注册").setIcon(android.R.drawable.ic_dialog_info)
                        .setView(dialogView)
                        .setNegativeButton("Cancel", null);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = dialogView.findViewById(R.id.input_username);
                        EditText editText1 = dialogView.findViewById(R.id.input_password);
                        String username = editText.getText().toString();
                        String password = editText1.getText().toString();

                        if(!"".equals(username) && !"".equals(password)){
                            register(username,password);
                        }else{
                            Toast.makeText(LoginActivity.this,"请确认输入的账号及密码",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void login(String username,String password){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://"+ip+"/api/project/loginuser";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");

                            String userId=jsonObject.getString("id");
                            if(status.equals("OK")){

                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                intent.setType("1");
                                intent.putExtra("userName", username);
                                intent.putExtra("usedID",userId);
                                startActivity(intent);

                                Toast.makeText(LoginActivity.this,"登入成功",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(LoginActivity.this,"账号或密码错误",Toast.LENGTH_SHORT).show();
                            }
                        }  catch (JSONException e) {
                            Toast.makeText(LoginActivity.this,"账号或密码错误",Toast.LENGTH_SHORT).show();
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
                params.put("password",password);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    private void register(String username,String password){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://"+ip+"/api/project/registeruser";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    String result="";
                    @Override
                    public void onResponse(String response) {
                        System.out.println("response: "+response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("status");
                            result = status;
                            if("OK".equals(result)){
                                Toast.makeText(LoginActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(LoginActivity.this,"服务器出现了点问题，请稍后再试",Toast.LENGTH_SHORT).show();
                            }
                            System.out.println("result: "+result);

                        }  catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.getMessage());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> params = new HashMap<String,String>();
                params.put("username",username);
                params.put("password",password);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}


