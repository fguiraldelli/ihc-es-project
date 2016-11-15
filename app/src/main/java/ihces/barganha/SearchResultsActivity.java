package ihces.barganha;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import ihces.barganha.models.Ad;
import ihces.barganha.photo.Imaging;
import ihces.barganha.rest.AdService;
import ihces.barganha.rest.ServiceResponseListener;

public class SearchResultsActivity extends AppCompatActivity {

    public static final String SEARCH_TERMS_EXTRA_KEY = "searchTerms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_search_results);

        TextView tvSearchTerms = (TextView)findViewById(R.id.tv_search_terms);
        String searchTerms = getIntent().getStringExtra(SEARCH_TERMS_EXTRA_KEY);

        if (!searchTerms.trim().isEmpty()) {
            tvSearchTerms.setText("\"" + searchTerms + "\"");
        }

        AdService service = new AdService();
        service.start(SearchResultsActivity.this);
        service.searchAds(searchTerms, new ServiceResponseListener<Ad[]>() {
            @Override
            public void onResponse(Ad[] response) {
                final ListAdapter adapter = new CustomAdapter(SearchResultsActivity.this, response);
                ListView advertiseListView = (ListView) findViewById(R.id.lv_results);
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

        Intent intent = new Intent(SearchResultsActivity.this, AdDetailsActivity.class);
        Gson gson = new Gson();
        String jsAd = gson.toJson(ad, Ad.class);
        intent.putExtra(AdDetailsActivity.AD_EXTRA_KEY, jsAd);
        startActivity(intent);
    }
}
