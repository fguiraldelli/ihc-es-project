package ihces.barganha;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    private String searchTerms;
    private TextView tvSearchTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_search_results);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        tvSearchTerms = (TextView)findViewById(R.id.tv_search_terms);

        if (savedInstanceState == null) {
            searchTerms = getIntent().getStringExtra(SEARCH_TERMS_EXTRA_KEY);
            if (searchTerms == null) {
                searchTerms = "";
            }
        } else {
            searchTerms = savedInstanceState.getString(SEARCH_TERMS_EXTRA_KEY, "");
        }

        doSearch();
    }

    private void doSearch() {
        if (!searchTerms.trim().isEmpty()) {
            tvSearchTerms.setText("\"" + searchTerms + "\"");

            AdService service = new AdService();
            service.start(SearchResultsActivity.this);
            service.searchAds(searchTerms, new ServiceResponseListener<Ad[]>() {
                @Override
                public void onResponse(Ad[] response) {
                    final ListAdapter adapter = new CustomAdapter(SearchResultsActivity.this, response);
                    ListView advertiseListView = (ListView) findViewById(R.id.lv_results);
                    advertiseListView.setAdapter(adapter);

                    advertiseListView.setOnItemClickListener(
                            new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    openDetailsActivity((Ad) adapter.getItem(i));
                                }
                            }
                    );
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception error) {

                }
            });
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(SearchResultsActivity.this, SettingsActivity.class));
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_TERMS_EXTRA_KEY, searchTerms);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        searchTerms = savedInstanceState.getString(SEARCH_TERMS_EXTRA_KEY, "");
        doSearch();
    }
}
