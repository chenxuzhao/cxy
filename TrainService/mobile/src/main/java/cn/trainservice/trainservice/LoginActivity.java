package cn.trainservice.trainservice;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.AbstractRequest;
import com.litesuits.http.request.StringRequest;
import com.litesuits.http.request.content.UrlEncodedFormBody;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.response.Response;
import com.litesuits.http.utils.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.trainservice.trainservice.journey.view.TicketInfo;
import cn.trainservice.trainservice.service.Chat.User;
import cn.trainservice.trainservice.view.CameraView;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {


    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */


    // UI references.
    private AutoCompleteTextView mIDCardNumberView;
    private EditText mTicketNumberView;
    private View mProgressView;
    private View mLoginFormView;
    private CameraView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mIDCardNumberView = (AutoCompleteTextView) findViewById(R.id.IDCardNumber);


        mTicketNumberView = (EditText) findViewById(R.id.ticketNumber);
        mTicketNumberView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        BootstrapButton mEmailSignInButton = (BootstrapButton) findViewById(R.id.sign_in_button);
        mEmailSignInButton.setBootstrapBrand(DefaultBootstrapBrand.INFO);

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        surfaceView = (CameraView) findViewById(R.id.surfaceView);
        surfaceView.bindActivity(this);
        surfaceView.startCamera();
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {


        // Reset errors.
        mIDCardNumberView.setError(null);
        mTicketNumberView.setError(null);

        // Store values at the time of the login attempt.
        String email = mIDCardNumberView.getText().toString();
        String password = mTicketNumberView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || isPasswordValid(password)) {
            mTicketNumberView.setError(getString(R.string.error_invalid_ticketNumber));
            focusView = mTicketNumberView;
            cancel = true;
        }
//        if (TextUtils.isEmpty(password)) {
//            mTicketNumberView.setError(getString(R.string.error_invalid_ticketNumber));
//            focusView = mTicketNumberView;
//            cancel = true;
//        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mIDCardNumberView.setError(getString(R.string.error_field_required));
            focusView = mIDCardNumberView;
            cancel = true;
        } else if (!isIDValid(email)) {
            mIDCardNumberView.setError(getString(R.string.error_invalid_id));
            focusView = mIDCardNumberView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            Log.d("data", "post");
            mProgressView.requestFocus();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    String uploadUrl = TrainServiceApplication.user_Login();
                    Log.d("dataurl",uploadUrl);
                    final StringRequest postRequest = new StringRequest(uploadUrl)
                            .setMethod(HttpMethods.Post)
                            .setHttpListener(new HttpListener<String>(true, false, true) {
                                @Override
                                public void onStart(AbstractRequest<String> request) {
                                    super.onStart(request);
                                    showProgress(true);
                                }

                                @Override
                                public void onUploading(AbstractRequest<String> request, long total, long len) {
                                    showProgress(true);
                                }

                                @Override
                                public void onEnd(Response<String> response) {
                                    showProgress(false);
                                    if (response.isConnectSuccess()) {
                                        String jsonstr = response.getResult();
                                        try {
                                            JSONObject js = new JSONObject(jsonstr);
                                            if (js.has("result")) {
                                                boolean success = js.getBoolean("result");
                                                int code = (!success ? 0 : 1);
                                                if (success) {
                                                    TrainTravel.islogin = true;
                                                    //////////////////////登陆请求///////////
                                                    Log.d("data2",js.toString());
                                                    JSONObject jso = js.getJSONObject("info");

                                                    TrainTravel.user_id =   jso.getString("user_id");
                                                    TrainTravel.user_name  =   jso.getString("user_name");
                                                    User.user_id =   TrainTravel.train_id = jso.getString("train_name");
                                                    TrainTravel.StartStation =  jso.getString("startname");
                                                    TrainTravel.current_station =   TrainTravel.StartStation_id = Integer.parseInt(jso.getString("start_station"));
                                                    TrainTravel.seatType = Integer.parseInt(jso.getString("seattype"));
                                                    TrainTravel.ticket_type = Integer.parseInt(jso.getString("ticket_type"));
                                                    TrainTravel.seat = jso.getString("seatnum");
                                                    TrainTravel.Carrige = jso.getString("carriage");
                                                    TrainTravel.EndStation =jso.getString("endname");
                                                    TrainTravel.EndStationid_id = Integer.parseInt(jso.getString("end_staton"));
                                                    TrainServiceApplication.setTickt(
                                                            new TicketInfo(LoginActivity.this));


                                                }
                                                responseLoginResult(code);
                                                Log.d("data", response.getResult());

                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        HttpUtil.showTips(LoginActivity.this, "Upload Failure",  "网络错误");
                                    }

                                }
                            });
                    LinkedList<NameValuePair> pList = new LinkedList<>();
                    pList.add(new NameValuePair("User_ID", mIDCardNumberView.getText().toString()));
                    pList.add(new NameValuePair("Train_ID", mTicketNumberView.getText().toString()));
                    postRequest.setHttpBody(new UrlEncodedFormBody(pList));
                    TrainServiceApplication.getLiteHttp(LoginActivity.this).executeAsync(postRequest);
                }
            }).start();


        }
    }

    private void responseLoginResult(final int code) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (code) {
                    case 0:
                        showProgress(false);
                        mTicketNumberView.setError(getString(R.string.error_incorrect_ticketNumber));
                        mTicketNumberView.requestFocus();
                        break;
                    case 1:
                        //
                        TrainServiceApplication.hasLogin = true;
                        finish();
                       /// startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        showProgress(false);
                        break;
                    case 2:
                        Toast.makeText(LoginActivity.this, "网络连接失败！", Toast.LENGTH_SHORT).show();
                        break;
                }
                showProgress(false);
            }
        });

    }


    private boolean isIDValid(String ID) {
        //TODO: Replace this with your own logic
        return ID.length() == 18;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return false;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade_in-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mIDCardNumberView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }
}

