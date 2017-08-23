package com.example.android1.login_signup_captcha;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;

import static android.widget.Toast.LENGTH_LONG;


public class Login extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private final AppCompatActivity activity = Login.this;




    private static final String PUBLIC_KEY = "6LcfRCUUAAAAAOqyzu26AMYOaUTNpPpLBZvTaiQb";
    private static final String PRIVATE_KEY = "6LcfRCUUAAAAANCQHKh2oYO21syCE13tXf-QMPUB";
    private String TAG = "TAG";
    static String response;
    private GoogleApiClient mGoogleApiClient;
    boolean captcha_status = false;
    Button login;
    String final_res = "";
    int flag = 0;
    CheckBox checkBox;
    private ProgressBar spinner;
    private EditText textInputEditTextEmail;
    private EditText textInputEditTextPassword;
    ImageView img;
    AnimationDrawable frameAnimation;
    private Button appCompatButtonLogin;
    private TextView textViewLinkRegister;

    private InputValidation inputValidation;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_login);




        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(SafetyNet.API)
                .addConnectionCallbacks(Login.this)
                .addOnConnectionFailedListener(Login.this)
                .setAccountName("kamalverma1207@gmail.com")
                .setGravityForPopups(3)
                .build();

        mGoogleApiClient.connect();



        img=(ImageView)findViewById(R.id.image);
        img.setVisibility(View.GONE);
        spinner=(ProgressBar)findViewById(R.id.progressBar);
        spinner.setVisibility(View.GONE);

        checkBox=(CheckBox)findViewById(R.id.box);









        final ImageView v = (ImageView) findViewById(R.id.image);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        if(!captcha_status)
                            shownCaptcha();
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL:{

                        break;
                    }
                }
                return true;
            }
        });






        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkBox.isChecked()&&!captcha_status) {
                    checkBox.setChecked(false);
                    spinner.setVisibility(View.VISIBLE);
                    Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
                    checkBox.setButtonDrawable(transparentDrawable);
                    shownCaptcha();

                }
            }
        });




        initViews();
        initListeners();
        initObjects();
    }


    private void initViews() {





        textInputEditTextEmail = (EditText) findViewById(R.id.editemail);
        textInputEditTextPassword = (EditText) findViewById(R.id.editpassword);

        appCompatButtonLogin = (Button) findViewById(R.id.login);

        textViewLinkRegister = (TextView) findViewById(R.id.signup);

    }


    private void initListeners() {
        appCompatButtonLogin.setOnClickListener(this);
        textViewLinkRegister.setOnClickListener(this);
    }


    private void initObjects() {
        databaseHelper = new DatabaseHelper(activity);
        inputValidation = new InputValidation(activity);

    }

    void shownCaptcha() {




        SafetyNet.SafetyNetApi.verifyWithRecaptcha(mGoogleApiClient, PUBLIC_KEY)
                .setResultCallback(new ResultCallback<SafetyNetApi.RecaptchaTokenResult>() {



                    @Override
                    public void onResult(SafetyNetApi.RecaptchaTokenResult result) {
                        Status status = result.getStatus();

                        if ((status != null) && status.isSuccess()) {


                            if (!result.getTokenResult().isEmpty()) {

                                ///Toast.makeText(getApplicationContext(), "success", LENGTH_LONG).show();
                                response = result.getTokenResult();
                                doGetData();
                            }


                        } else {

                            Log.e("MY_APP_TAG", "Error occurred " +
                                    "when communicating with the reCAPTCHA service.");

                            Toast.makeText(getApplicationContext(),"when communicating with the reCAPTCHA service.",LENGTH_LONG).show();

                        }
                    }
                });


    }


    private void doGetData() {



        final Thread thread = new Thread() {
            @Override
            public void run() {


                try {
                    String url = "https://www.google.com/recaptcha/api/siteverify";

                    String query = "secret=" + PRIVATE_KEY + "&response=" + response;

                    HttpClient httpClient = new DefaultHttpClient();

                    Log.e(TAG, "urlParameters--" + url + query);
                    HttpGet httpost = new HttpGet(url + "?" + query);

                    HttpResponse response;

                    response = httpClient.execute(httpost);
                    HttpEntity resEntity = response.getEntity();

                    if (resEntity != null) {
                        final String responseStr = EntityUtils.toString(resEntity).trim();
                        JSONObject json = new JSONObject(responseStr);
                        Boolean status = json.getBoolean("success");
                        if (status) {
                            flag = 1;
                            final_res = responseStr.toString();
                        } else {

                        }
                        Log.e(TAG, "responseStr---" + responseStr);


                    }
                } catch (final MalformedURLException e) {
                    showError("Error : MalformedURLException " + e);

                    e.printStackTrace();
                } catch (final IOException e) {
                    showError("Error : IOException " + e);
                    e.printStackTrace();
                } catch (Exception e) {
                    showError(e.toString());
                }

                Message msg = handler.obtainMessage();
                final Bundle b = new Bundle();
                msg.what = flag;
                handler.sendMessage(msg);
            }

        };
        thread.start();


    }

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 1) {
                captcha_status = true;
                Log.e(TAG, final_res + "rhtfghff");
                spinner.setVisibility(View.GONE);
                img.setVisibility(View.VISIBLE);
                checkBox.setChecked(true);

            } else {
                checkBox.setChecked(false);

            }
        }
    };


    void showError(final String err) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(Login.this, err, LENGTH_LONG).show();
            }
        });
    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                ////shownCaptcha();
                if(verifyFromSQLite() && checkBox.isChecked())
                {

                    Toast.makeText(getApplicationContext(),"Login Successfully",Toast.LENGTH_LONG).show();
                    Intent in=new Intent(getApplicationContext(),Welcome.class);
                    startActivity(in);
                }
                else
                {
                    finish();
                    Toast.makeText(getApplicationContext(),"please try again",Toast.LENGTH_LONG).show();
                    Intent in=new Intent(getApplicationContext(),Login.class);
                    startActivity(in);

                }


                break;
            case R.id.signup:
                // Navigate to RegisterActivity
                Intent intentRegister = new Intent(getApplicationContext(),Signup.class);
                startActivity(intentRegister);
                break;
        }
    }

    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */
    private boolean verifyFromSQLite() {
        if (!inputValidation.isInputEditTextFilled(textInputEditTextEmail, getString(R.string.error_message_email))) {

            return false;
        }
        if (!inputValidation.isInputEditTextEmail(textInputEditTextEmail,  getString(R.string.error_message_email))) {

            return false;
        }
        if (!inputValidation.isInputEditTextFilled(textInputEditTextPassword,  getString(R.string.error_message_email))) {
            return false;
        }

        if (databaseHelper.checkUser(textInputEditTextEmail.getText().toString().trim()
                , textInputEditTextPassword.getText().toString().trim())) {


            Intent accountsIntent = new Intent(activity,Login.class);
            accountsIntent.putExtra("EMAIL", textInputEditTextEmail.getText().toString().trim());
            emptyInputEditText();
            startActivity(accountsIntent);

            return  true;

        } else {
            // Snack Bar to show success message that record is wrong



            return false;
           // Snackbar.make(nestedScrollView, getString(R.string.error_valid_email_password), Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        textInputEditTextEmail.setText(null);
        textInputEditTextPassword.setText(null);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}