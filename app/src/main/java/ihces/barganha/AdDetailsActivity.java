package ihces.barganha;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import ihces.barganha.models.Ad;
import ihces.barganha.models.User;
import ihces.barganha.photo.Imaging;
import ihces.barganha.rest.AdService;
import ihces.barganha.rest.ServiceResponseListener;
import ihces.barganha.rest.UserPointsService;
import ihces.barganha.rest.UserService;

public class AdDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String AD_EXTRA_KEY = "adData";
    private UserService userService;
    private UserPointsService pointsService;
    private Ad ad;
    private User localUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_ad_details);

        localUser = User.getStoredLocal(this);

        setupServices();

        String jsAd = getIntent().getStringExtra(AD_EXTRA_KEY);
        Gson gson = new Gson();
        ad = gson.fromJson(jsAd, Ad.class);
        loadDetails(ad);
        setEvaluationButtons();
    }

    private void setupServices() {
        userService = new UserService();
        //userService.setAsMock();
        userService.start(this);

        pointsService = new UserPointsService();
        //pointsService.setAsMock();
        pointsService.start(this);
    }

    private void loadDetails(final Ad ad) {
        // TODO include user points info straight at the ad
        userService.getUser(ad.getAuthToken(), new ServiceResponseListener<User[]>() {
            @Override
            public void onResponse(User[] response) {
                User adUser = response.length > 0 ? response[0] : new User();

                ((TextView)findViewById(R.id.tv_title_value)).setText(ad.getTitle());
                ((TextView)findViewById(R.id.tv_description_value)).setText(ad.getDescription());
                ((TextView)findViewById(R.id.tv_price_value)).setText(ad.getPrice());
                ((ImageView)findViewById(R.id.iv_photo)).setImageBitmap(Imaging.base64DecodeImage(ad.getPhotoBase64()));

                ((ImageView)findViewById(R.id.iv_points)).setImageDrawable(getResources().getDrawable(adUser.getPointsDrawableId()));
            }

            @Override
            public void onError(Exception error) {
                Log.e("AdDetails", "Error getting seller points", error);
            }
        });
    }

    private void setEvaluationButtons() {
        findViewById(R.id.ibt_up).setOnClickListener(this);
        findViewById(R.id.ibt_down).setOnClickListener(this);
    }

    @Override
    public void onClick(final View v) {
        final int id = v.getId();

        if (id == R.id.ibt_up || id == R.id.ibt_down) {
            char eval = (id == R.id.ibt_up) ? '+' : '-';
            pointsService.postEvaluation(localUser.getAuthToken(), ad.getId(), eval,
                    new ServiceResponseListener<String>() {
                        @Override
                        public void onResponse(String response) {
                            int otherId = (id == R.id.ibt_up) ? R.id.ibt_down : R.id.ibt_up;
                            View otherView = findViewById(otherId);

                            v.setEnabled(false);
                            v.setClickable(false);
                            otherView.setClickable(true);
                            otherView.setEnabled(true);
                            // TODO check if this user has already posted eval for the current seller.
                        }

                        @Override
                        public void onError(Exception error) {
                            Log.e("AdDetails", "Error getting seller points", error);
                        }
                    });
        }
    }
}
