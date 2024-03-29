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

import com.google.gson.Gson;

import ihces.barganha.models.Ad;
import ihces.barganha.models.User;
import ihces.barganha.photo.Imaging;
import ihces.barganha.rest.AdService;
import ihces.barganha.rest.ServiceResponseListener;

public class SearchResultsActivity extends AppCompatActivity {

    public static final String SEARCH_TERMS_EXTRA_KEY = "searchTerms";
    public static final String LIST_TYPE_EXTRA_KEY = "listType";

    private String searchTerms;
    private TextView tvSearchTerms;
    private ListType listType;

    public enum ListType {
        Search(0),
        Trending(1),
        Events(2),
        Commerce(3);

        private int value;

        ListType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

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
            listType = ListType.values()[getIntent().getIntExtra(LIST_TYPE_EXTRA_KEY, 0)];
        } else {
            searchTerms = savedInstanceState.getString(SEARCH_TERMS_EXTRA_KEY, "");
            listType = ListType.values()[savedInstanceState.getInt(LIST_TYPE_EXTRA_KEY, 0)];
        }

        doSearch();
    }

    private void doSearch() {
        AdService service = new AdService();
        service.start(SearchResultsActivity.this);

        if (listType.equals(ListType.Search) && !searchTerms.trim().isEmpty()) {
            tvSearchTerms.setText("\"" + searchTerms + "\"");

            service.searchAds(searchTerms, new ServiceResponseListener<Ad[]>() {
                @Override
                public void onResponse(Ad[] response) {
                    final ListAdapter adapter = new CustomAdapter(SearchResultsActivity.this, response, false);
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

                    if (response.length == 0) {
                        ((TextView) findViewById(R.id.tv_search_terms_label)).setText(R.string.tv_no_results_label);
                    }

                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception error) {

                }
            });
        } else if (listType.equals(ListType.Trending)) {
            int localId = User.getStoredLocal(SearchResultsActivity.this).getCollegeId();
            service.getTrendingAds(localId, new ServiceResponseListener<Ad[]>() {
                @Override
                public void onResponse(Ad[] response) {
                    final ListAdapter adapter = new CustomAdapter(SearchResultsActivity.this, response, false);
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

                    ((TextView) findViewById(R.id.tv_search_terms_label)).setText(R.string.tv_trending_ads_label);
                    tvSearchTerms.setVisibility(View.GONE);

                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception error) { }
            });
        } else if (listType.equals(ListType.Events)) {
            int localId = User.getStoredLocal(SearchResultsActivity.this).getCollegeId();
            service.getEventAds(localId, new ServiceResponseListener<Ad[]>() {
                @Override
                public void onResponse(Ad[] response) {
                    final ListAdapter adapter = new CustomAdapter(SearchResultsActivity.this, response, false);
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

                    ((TextView) findViewById(R.id.tv_search_terms_label)).setText(R.string.tv_event_ads_label);
                    tvSearchTerms.setVisibility(View.GONE);

                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception error) { }
            });
        } else if (listType.equals(ListType.Commerce)) {
            int localId = User.getStoredLocal(SearchResultsActivity.this).getCollegeId();
            service.getCommerceAds(localId, new ServiceResponseListener<Ad[]>() {
                @Override
                public void onResponse(Ad[] response) {
                    final ListAdapter adapter = new CustomAdapter(SearchResultsActivity.this, response, false);
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

                    ((TextView) findViewById(R.id.tv_search_terms_label)).setText(R.string.tv_commerce_ads_label);
                    tvSearchTerms.setVisibility(View.GONE);

                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                }

                @Override
                public void onError(Exception error) { }
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
        outState.putInt(LIST_TYPE_EXTRA_KEY, listType.getValue());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        searchTerms = savedInstanceState.getString(SEARCH_TERMS_EXTRA_KEY, "");
        listType = ListType.values()[savedInstanceState.getInt(LIST_TYPE_EXTRA_KEY, 0)];
        doSearch();
    }
}
