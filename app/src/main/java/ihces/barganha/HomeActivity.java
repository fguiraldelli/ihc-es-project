package ihces.barganha;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import ihces.barganha.models.User;
import ihces.barganha.rest.ServiceResponseListener;
import ihces.barganha.rest.UserService;

public class HomeActivity extends AppCompatActivity {

    AutoCompleteTextView tvSearchTerms;
    Button btSearch;
    Button btAdvertise;
    Button btMyAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_home);
        setButtonEvents();
        setAvailableOptions();
    }

    private void setAvailableOptions() {
        UserService service = new UserService();
        service.start(getApplicationContext());
        //service.setAsMock();
        service.getMyUser(User.getStoredLocal(this),
                new ServiceResponseListener<User[]>() {
                    @Override
                    public void onResponse(User[] response) {
                        User user = response[0];
                        User.storeLocal(HomeActivity.this, user);

                        final TextView tvLabelSelling = (TextView)findViewById(R.id.tv_label_selling);
                        final Button btnMyAdsLocal = (Button)findViewById(R.id.btn_my_ads);
                        final TextView tvPointsLabel = (TextView)findViewById(R.id.tv_points_label);
                        final ImageView ivMyPoints = (ImageView)findViewById(R.id.iv_my_points);

                        if (user.isHasAds()) {
                            btnMyAdsLocal.setVisibility(View.VISIBLE);
                            tvPointsLabel.setVisibility(View.VISIBLE);
                            ivMyPoints.setVisibility(View.VISIBLE);
                            tvLabelSelling.setVisibility(View.INVISIBLE);
                            ivMyPoints.setImageDrawable(getResources().getDrawable(user.getPointsDrawableId()));
                        } else {
                            btnMyAdsLocal.setVisibility(View.INVISIBLE);
                            tvPointsLabel.setVisibility(View.INVISIBLE);
                            ivMyPoints.setVisibility(View.INVISIBLE);
                            tvLabelSelling.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(Exception error) {
                        Log.e("HOME", "Error getting my status", error);
                    }
                });

    }

    private void setButtonEvents() {
        tvSearchTerms = (AutoCompleteTextView)findViewById(R.id.tv_search_terms);

        btSearch = (Button)findViewById(R.id.btn_search);
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerms = tvSearchTerms.getText().toString();
                if (!searchTerms.trim().isEmpty()) {
                    openSearchResultActivity(searchTerms);
                }
            }
        });

        btAdvertise = (Button)findViewById(R.id.btn_advertise);
        btAdvertise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AdvertiseActivity.class));
            }
        });

        btMyAds = (Button)findViewById(R.id.btn_my_ads);
        btMyAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MyAdsList.class));
            }
        });

        findViewById(R.id.bt_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                AccessToken.setCurrentAccessToken(null);
                Log.d("HOME", "TESTE - Logged Out of Facebook.");
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void openSearchResultActivity(String searchTerms) {
        Intent intent = new Intent(HomeActivity.this, SearchResultsActivity.class);
        intent.putExtra(SearchResultsActivity.SEARCH_TERMS_EXTRA_KEY, searchTerms);
        startActivity(intent);
    }

}
