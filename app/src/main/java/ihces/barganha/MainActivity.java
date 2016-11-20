package ihces.barganha;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import ihces.barganha.models.User;
import ihces.barganha.rest.ServiceResponseListener;
import ihces.barganha.rest.UserService;
import ihces.barganha.security.Tokenizer;

public class MainActivity extends AppCompatActivity {

    private static final int COLLEGE_ID_UFSCAR = 1;

    CallbackManager callbackManager;
    private EditText etCellNumber;
    private Spinner spCollege;
    private int[] collegeIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this.getApplication());
        setContentView(R.layout.activity_main);

        ArrayAdapter spAdapter = ArrayAdapter.createFromResource(this,
                R.array.college_names, R.layout.spinner_item);
        spAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spCollege = (Spinner) findViewById(R.id.sp_college);
        spCollege.setAdapter(spAdapter);
        collegeIds = getResources().getIntArray(R.array.college_ids);

        AccessToken token = AccessToken.getCurrentAccessToken();
        //AccessToken token = null; // test no logged user

        if (token != null && !token.isExpired()) {
            openHomeActivity();
        }

        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();

        etCellNumber = (EditText) findViewById(R.id.et_cell_number);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                fetchUserData(loginResult);
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, getText(R.string.toast_login_required), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(MainActivity.this, getText(R.string.toast_login_error), Toast.LENGTH_LONG).show();
                Log.e("FacebookLogin", "Couldn't get login result.", exception);
            }
        });
    }

    private void postLogin(final User user) {
        UserService service = new UserService();
        service.start(getApplicationContext());
        //service.setAsMock(); // DEBUG LOCAL
        service.postLogin(user, new ServiceResponseListener<String>() {
            @Override
            public void onResponse(String response) {
                User.storeLocal(MainActivity.this, user);
                openHomeActivity();
            }

            @Override
            public void onError(Exception error) {
                onLoginError();
                Log.e("FacebookLogin", "Couldn't POST login result to our API.", error);
            }
        });
    }

    private void onLoginError() {
        LoginManager.getInstance().logOut();
        AccessToken.setCurrentAccessToken(null);
        User.clearLocal(this);
        Toast.makeText(this, getText(R.string.toast_login_error), Toast.LENGTH_LONG).show();
    }

    private void fetchUserData(LoginResult loginResult) {
        AccessToken accessToken = loginResult.getAccessToken();
        final String authToken = accessToken.getToken();
        // = Tokenizer.generateSaltHashedKey(MainActivity.this, accessToken.getToken());

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (response.getError() != null) {
                            onLoginError();
                            Log.e("GraphRequest", "Error requesting data from Facebook API.");
                        } else {
                            try {
                                String name = object.getString("first_name");
                                String gender = object.getString("gender");
                                long facebookId = object.getLong("id");

                                String cellNumber = etCellNumber.getText().toString();
                                int collegeId = collegeIds[spCollege.getSelectedItemPosition()];
                                User user = new User(authToken, collegeId, cellNumber, name, gender, facebookId);

                                postLogin(user);
                            } catch (JSONException jex) {
                                onLoginError();
                                Log.e("GraphRequest", "Error reading data from Facebook API.", jex);
                            }
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void openHomeActivity() {
        Intent goToHomeIntent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(goToHomeIntent);
        finish();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
