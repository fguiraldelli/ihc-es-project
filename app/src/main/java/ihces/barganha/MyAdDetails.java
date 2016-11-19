package ihces.barganha;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import ihces.barganha.models.Ad;
import ihces.barganha.models.User;
import ihces.barganha.photo.Imaging;
import ihces.barganha.rest.AdService;
import ihces.barganha.rest.ServiceResponseListener;
import ihces.barganha.rest.UserPointsService;
import ihces.barganha.rest.UserService;

public class MyAdDetails extends AppCompatActivity {
    public static final String AD_EXTRA_KEY = "adData";

    private TextView tvTitle;
    private TextView tvDescription;
    private TextView tvPrice;
    private ImageView ivPhoto;
    private Button btMarkDue;
    private TableRow trDue;

    private AdService adService;
    private Ad ad;
    private User localUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ad_details);

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
        btMarkDue = (Button) findViewById(R.id.bt_mark_due);
        trDue = (TableRow) findViewById(R.id.tr_due);
    }

    private void setupServices() {
        adService = new AdService();
        //adService.setAsMock();
        adService.start(this);
    }

    private void loadDetails(final Ad ad) {
        tvTitle.setText(ad.getTitle());
        tvDescription.setText(ad.getDescription());
        tvPrice.setText(ad.getPrice());
        ivPhoto.setImageBitmap(Imaging.readImageFile(this, ad.getFilename()));

        setControlVisibility();
    }

    private void setControlVisibility() {
        if (Ad.isMyAdDue(this, ad) || ad.getIsDue()) {
            ad.setIsDue(true);
            trDue.setVisibility(View.VISIBLE);
            btMarkDue.setVisibility(View.GONE);
        } else {
            trDue.setVisibility(View.GONE);
            btMarkDue.setVisibility(View.VISIBLE);
        }
    }

    private void setButtonEvents() {
        btMarkDue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = localUser.getAuthToken();
                adService.markClosed(token, ad, new ServiceResponseListener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ad.setIsDue(true);
                        Ad.storeMyDueAd(MyAdDetails.this, ad);
                        setControlVisibility();
                    }

                    @Override
                    public void onError(Exception error) {
                        Log.e("MyAdDetails", "Error setting ad due", error);
                    }
                });

            }
        });
    }
}