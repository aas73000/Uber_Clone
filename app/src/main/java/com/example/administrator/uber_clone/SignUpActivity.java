package com.example.administrator.uber_clone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.administrator.uber_clone.R;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;
import com.tripl3dev.prettystates.StateExecuterKt;
import com.tripl3dev.prettystates.StatesConfigFactory;
import com.tripl3dev.prettystates.StatesConstants;

import studio.carbonylgroup.textfieldboxes.ExtendedEditText;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private ExtendedEditText username, password, choice;
    private Button signin, signup;
    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        initalizeAllViews();
        signup.setOnClickListener(this);
        signin.setOnClickListener(this);
    }

    private void initalizeAllViews() {
        username = findViewById(R.id.signupactivityUsernasme);
        password = findViewById(R.id.signupactivityPassword);
        choice = findViewById(R.id.signupactivitySignInText);
        signin = findViewById(R.id.signupactivitySignIn);
        signup = findViewById(R.id.signupactivitySignup);
        radioGroup = findViewById(R.id.signupactivityRadioGroup);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().equals("Sign in")) {
            signup.setText("Sign in");
            item.setTitle("Sign up");
        } else {
            signup.setText("Sign up");
            item.setTitle("Sign in");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.signup_signin_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signupactivitySignIn:
                if(choice.getText().toString().equals("Driver")  || choice.getText().toString().equals("Passenger")  ){
                    signinAnonymousUser();
                }else{
                    FancyToast.makeText(SignUpActivity.this,"Type Driver or Passenger\n" +
                                    "You type:"+choice.getText().toString(), FancyToast.LENGTH_LONG,
                            FancyToast.INFO, true).show();
                    return;
                }
                break;
            case R.id.signupactivitySignup:
                if (signup.getText().equals("Sign up"))
                    if (!(username.getText().equals("") || password.getText().equals("")))
                        signupProcess();
                    else{
                        FancyToast.makeText(SignUpActivity.this, "Fill all entries", FancyToast.LENGTH_LONG,
                                FancyToast.INFO, true).show();
                        return;}
                else
                    signinProcess();
                break;
        }
    }

    /*function to complete signup process */
    private void signupProcess() {
        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(username.getText().toString());
        parseUser.setPassword(password.getText().toString());
        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(radioButtonId);
        parseUser.put("Choice", radioButton.getText().toString());
        parseUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    FancyToast.makeText(SignUpActivity.this, "Sign up successfully" + username.getText(),
                            FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                } else {
                    FancyToast.makeText(SignUpActivity.this, e.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                }

            }
        });
    }

    private void signinProcess() {
        ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null && e == null) {
                    FancyToast.makeText(SignUpActivity.this, "Sign in successfully" + user.getUsername(), FancyToast.LENGTH_LONG,
                            FancyToast.SUCCESS, true).show();
                } else {
                    FancyToast.makeText(SignUpActivity.this, e.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                }
            }
        });
    }
    private void signinAnonymousUser(){
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(user != null && e == null){
                    FancyToast.makeText(SignUpActivity.this, "Sign in anonymously successfully" ,
                            FancyToast.LENGTH_LONG, FancyToast.SUCCESS, true).show();
                    user.put("Choice",choice.getText().toString());
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null)
                                FancyToast.makeText(SignUpActivity.this, e.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                        }
                    });
                }else{
                    FancyToast.makeText(SignUpActivity.this, e.getMessage(), FancyToast.LENGTH_LONG, FancyToast.ERROR, true).show();
                }
            }
        });
    }
}
