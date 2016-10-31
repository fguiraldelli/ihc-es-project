package ihces.barganha;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import ihces.barganha.models.Ad;
import ihces.barganha.models.User;
import ihces.barganha.photo.Imaging;

public class AdDetailsActivity extends AppCompatActivity {

    public static final String AD_EXTRA_KEY = "adData";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_ad_details);

        String jsAd = getIntent().getStringExtra(AD_EXTRA_KEY);
        Gson gson = new Gson();
        Ad ad = gson.fromJson(jsAd, Ad.class);

        // TODO include user points info on the ad
        User user = new User("",1,2,1);

        ((TextView)findViewById(R.id.tv_title_value)).setText(ad.getTitle());
        ((TextView)findViewById(R.id.tv_description_value)).setText(ad.getDescription());
        ((TextView)findViewById(R.id.tv_price_value)).setText(ad.getPrice());
        ((ImageView)findViewById(R.id.iv_points)).setImageDrawable(getResources().getDrawable(user.getPointsDrawableId()));
        ((ImageView)findViewById(R.id.iv_photo)).setImageBitmap(Imaging.base64DecodeImage(ad.getPhotoBase64()));
    }

}
