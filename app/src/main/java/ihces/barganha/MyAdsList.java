package ihces.barganha;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.gson.Gson;

import ihces.barganha.models.Ad;
import ihces.barganha.models.User;
import ihces.barganha.photo.Imaging;
import ihces.barganha.rest.AdService;
import ihces.barganha.rest.ServiceResponseListener;


public class MyAdsList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_my_ads_list);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        AdService service = new AdService();
        service.start(MyAdsList.this);
        service.getMyAds(User.getStoredLocal(MyAdsList.this).getAuthToken(),
                new ServiceResponseListener<Ad[]>() {
            @Override
            public void onResponse(Ad[] response) {
                final ListAdapter adapter = new CustomAdapter(MyAdsList.this, response);
                ListView advertiseListView = (ListView) findViewById(R.id.lv_ads);
                advertiseListView.setAdapter(adapter);

                advertiseListView.setOnItemClickListener(
                        new AdapterView.OnItemClickListener(){
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                openDetailsActivity((Ad)adapter.getItem(i));
                            }
                        }
                );

                /*Stops animated circle*/
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception error) {

            }
        });
    }

    private void openDetailsActivity(Ad ad) {
        if (ad.getFilename().length() == 0) {
            String filename = Imaging.writeImageFile(this, ad.getId(), ad.getPhotoBase64());
            ad.setPhotoBase64("");
            ad.setFilename(filename);
        }

        Intent intent = new Intent(this, MyAdDetails.class);
        Gson gson = new Gson();
        String jsAd = gson.toJson(ad, Ad.class);
        intent.putExtra(AdDetailsActivity.AD_EXTRA_KEY, jsAd);
        startActivity(intent);
    }
}
