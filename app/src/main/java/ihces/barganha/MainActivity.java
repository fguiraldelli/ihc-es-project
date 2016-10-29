package ihces.barganha;

import android.content.Intent;
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
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class MainActivity extends AppCompatActivity {
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this.getApplication());
        setContentView(R.layout.activity_main);

        AccessToken token = AccessToken.getCurrentAccessToken();
        //AccessToken token = null;
        if (token != null && !token.isExpired()) {
            openHomeActivity();
        }

        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                openHomeActivity();
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
