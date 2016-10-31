package ihces.barganha;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import ihces.barganha.models.User;
import ihces.barganha.rest.ServiceResponseListener;
import ihces.barganha.rest.UserService;
import ihces.barganha.security.Tokenizer;

public class MainActivity extends AppCompatActivity {

    private static final int COLLEGE_ID_UFSCAR = 1;

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this.getApplication());
        setContentView(R.layout.activity_main);

        AccessToken token = AccessToken.getCurrentAccessToken();
        //AccessToken token = null; // test no logged user
        if (token != null && !token.isExpired()) {
            openHomeActivity();
        }

        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String authToken = Tokenizer.generateSaltHashedKey(MainActivity.this,
                        loginResult.getAccessToken().getToken());

                final User user = new User(authToken, COLLEGE_ID_UFSCAR);

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
                        LoginManager.getInstance().logOut();
                        AccessToken.setCurrentAccessToken(null);
                        Toast.makeText(MainActivity.this, getText(R.string.toast_login_error), Toast.LENGTH_LONG).show();
                        Log.e("FacebookLogin", "Couldn't POST login result to our API.", error);
                    }
                });
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
