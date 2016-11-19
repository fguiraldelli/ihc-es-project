package ihces.barganha;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import ihces.barganha.models.Ad;
import ihces.barganha.models.User;
import ihces.barganha.photo.Imaging;
import ihces.barganha.rest.ServiceResponseListener;
import ihces.barganha.rest.UserPointsService;
import ihces.barganha.rest.UserService;

public class AdDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String AD_EXTRA_KEY = "adData";

    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvPrice;
    private ImageView ivPhoto;
    private ImageView ivPoints;
    private Button btContact;
    private TextView tvContact;
    private LinearLayout llEvaluate;

    private UserService userService;
    private UserPointsService pointsService;
    private Ad ad;
    private User localUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_ad_details);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        findViews();
        setupServices();

        localUser = User.getStoredLocal(this);

        String jsAd = getIntent().getStringExtra(AD_EXTRA_KEY);
        Gson gson = new Gson();
        ad = gson.fromJson(jsAd, Ad.class);
        loadDetails(ad);
        setButtonEvents();
    }

    private void findViews() {
        tvTitle = (TextView) findViewById(R.id.tv_title_value);
        tvDescription = (TextView) findViewById(R.id.tv_description_value);
        tvPrice = (TextView) findViewById(R.id.tv_price_value);
        ivPhoto = (ImageView) findViewById(R.id.iv_photo);
        ivPoints = (ImageView) findViewById(R.id.iv_points);
        btContact = (Button) findViewById(R.id.bt_contact);
        tvContact = (TextView) findViewById(R.id.tv_contact);
        llEvaluate = (LinearLayout) findViewById(R.id.ll_evaluate);
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

                tvTitle.setText(ad.getTitle());
                tvDescription.setText(ad.getDescription());
                tvPrice.setText(ad.getPrice());
                ivPhoto.setImageBitmap(Imaging.readImageFile(AdDetailsActivity.this, ad.getFilename()));
                ivPoints.setImageDrawable(getResources().getDrawable(adUser.getPointsDrawableId()));

                setControlVisibility();
            }

            @Override
            public void onError(Exception error) {
                Log.e("AdDetails", "Error getting seller points", error);
            }
        });
    }

    private void setControlVisibility() {
        if (Ad.isContactingAd(this, ad)) {
            btContact.setVisibility(View.GONE);
            tvContact.setVisibility(View.VISIBLE);
            llEvaluate.setVisibility(View.VISIBLE);

            String evaluated = Ad.getEvaluationForAd(AdDetailsActivity.this, ad);

            int idEnable = 0, idDisable = 0;

            if (evaluated.equals("+")) {
                idEnable = R.id.ibt_down;
                idDisable = R.id.ibt_up;
            } else if (evaluated.equals("-")) {
                idEnable = R.id.ibt_up;
                idDisable = R.id.ibt_down;
            }

            if (!evaluated.equals("")) {
                View vEnable = findViewById(idEnable);
                View vDisable = findViewById(idDisable);

                vDisable.setEnabled(false);
                vDisable.setClickable(false);

                vEnable.setClickable(true);
                vEnable.setEnabled(true);
            }

        } else {
            btContact.setVisibility(View.VISIBLE);
            tvContact.setVisibility(View.GONE);
            llEvaluate.setVisibility(View.GONE);
        }
    }

    private void setButtonEvents() {
        findViewById(R.id.ibt_up).setOnClickListener(this);
        findViewById(R.id.ibt_down).setOnClickListener(this);
        btContact.setOnClickListener(this);
    }

    @Override
    public void onClick(final View view) {
        final int id = view.getId();

        if (id == R.id.ibt_up || id == R.id.ibt_down) {
            final String eval = (id == R.id.ibt_up) ? "+" : "-";
            pointsService.postEvaluation(localUser.getAuthToken(), ad.getId(), eval,
                    new ServiceResponseListener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Ad.setEvaluationForAd(AdDetailsActivity.this, ad, eval);

                            int otherId = (id == R.id.ibt_up) ? R.id.ibt_down : R.id.ibt_up;
                            View otherView = findViewById(otherId);

                            view.setEnabled(false);
                            view.setClickable(false);
                            otherView.setClickable(true);
                            otherView.setEnabled(true);
                            // TODO check if this user has already posted eval for the current seller.
                        }

                        @Override
                        public void onError(Exception error) {
                            Log.e("AdDetails", "Error getting seller points", error);
                        }
                    });
        } else if (id == R.id.bt_contact) {
            Ad.storeContactingAd(this, ad);
            setControlVisibility();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(AdDetailsActivity.this, SettingsActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }
}
