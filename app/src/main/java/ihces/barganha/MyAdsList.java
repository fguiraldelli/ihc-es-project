package ihces.barganha;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;

import ihces.barganha.models.Ad;
import ihces.barganha.models.User;
import ihces.barganha.photo.Imaging;
import ihces.barganha.rest.AdService;
import ihces.barganha.rest.ServiceResponseListener;


public class MyAdsList extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_my_ads_list);

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
