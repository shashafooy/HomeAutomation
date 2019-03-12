package com.example.homeautomation;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

//Login
public class LoginActivity extends AppCompatActivity {

    private TextView mTextMessage;
    Button login;
    EditText userNameEdit,passwordEdit;
    TextView errorView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        login = findViewById(R.id.loginBtn);
        userNameEdit=findViewById(R.id.username_edit);
        passwordEdit=findViewById(R.id.password_Edit);
        errorView=findViewById(R.id.error_TextView);
        errorView.setVisibility(View.INVISIBLE);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //TODO check if username and password are in database
                if(userNameEdit.getText().toString().equals("admin") &&
                    passwordEdit.getText().toString().equals("admin")){
                    Toast.makeText(getApplicationContext(),"Logging In",Toast.LENGTH_SHORT).show();
                    //TODO connect to server
                }else{
                    errorView.setVisibility(View.VISIBLE);
                }
            }
        });


    }

}
