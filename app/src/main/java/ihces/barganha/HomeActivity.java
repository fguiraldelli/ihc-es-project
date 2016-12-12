package ihces.barganha;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;

import ihces.barganha.models.RecentSearch;
import ihces.barganha.models.User;
import ihces.barganha.rest.ServiceResponseListener;
import ihces.barganha.rest.UserService;

public class HomeActivity extends AppCompatActivity {

    private static final int ADVERTISE_REQUEST_CODE = 1000;

    AutoCompleteTextView tvSearchTerms;
    Button btSearch;
    Button btAdvertise;
    Button btMyAds;
    Button btRecentSearches;
    Button btCommerce;
    Button btTrendingAds;
    Button btEvents;
    TextView tvGreeting;
    UserService service;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_home_full);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        service = new UserService();
        service.start(getApplicationContext());

        user = User.getStoredLocal(this);

        setButtonEvents();
        setAvailableOptions();

        setAutoComplete();
    }

    private void setAutoComplete() {
        final AutoCompleteSearchAdapter spAdapter = new AutoCompleteSearchAdapter(this, R.layout.autocomplete_item);
        tvSearchTerms.setAdapter(spAdapter);
        tvSearchTerms.setThreshold(spAdapter.THRESHOLD);
        tvSearchTerms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Object item = adapterView.getItemAtPosition(position);
                if (item == null) return;
                String suggestion = (String) item;
                String contents = spAdapter.getOriginalText();
                int lastSpace = Math.max(0, contents.lastIndexOf(" ") + 1);
                String textToSet = contents.substring(0, lastSpace) + suggestion + " ";
                tvSearchTerms.setText(textToSet);
                tvSearchTerms.setSelection(textToSet.length());
            }
        });
    }

    private void setGreeting(boolean isNew) {
        tvGreeting = (TextView) findViewById(R.id.tv_greeting);
        String formatted;
        Resources resources = getResources();

        if (isNew) {
            formatted = String.format(resources.getString(R.string.greeting_welcome), getGenderChar(), user.getName());
        } else {
            formatted = String.format(resources.getString(R.string.greeting_hello), user.getName());
        }

        CharSequence styledText = Html.fromHtml(formatted);
        tvGreeting.setText(styledText);
    }

    private String getGenderChar() {
        if (user.isFemale()) {
            return "a";
        }
        return "o";
    }

    private void setAvailableOptions() {
        service.getMyUser(user,
                new ServiceResponseListener<User[]>() {
                    @Override
                    public void onResponse(User[] response) {
                        if (response.length == 0) {
                            LoginManager.getInstance().logOut();
                            AccessToken.setCurrentAccessToken(null);
                            User.clearLocal(HomeActivity.this);

                            Log.d("Home", "Logged Out of Facebook.");

                            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            User responseUser = response[0];

                            user.setId(responseUser.getId());
                            user.setAds(responseUser.getAds());
                            user.setPoints(responseUser.getPoints());

                            User.storeLocal(HomeActivity.this, user);

                            setGreeting(!user.isHasAds());

                            TextView tvLabelSelling = (TextView) findViewById(R.id.tv_label_selling);
                            Button btnMyAdsLocal = (Button) findViewById(R.id.btn_my_ads);
                            TextView tvPointsLabel = (TextView) findViewById(R.id.tv_points_label);
                            ImageView ivMyPoints = (ImageView) findViewById(R.id.iv_my_points);
                            Button btnRecentSearch = (Button) findViewById(R.id.bt_recent);

                            if (user.isHasAds()) {
                                btnMyAdsLocal.setVisibility(View.VISIBLE);
                                tvPointsLabel.setVisibility(View.VISIBLE);
                                ivMyPoints.setVisibility(View.VISIBLE);
                                tvLabelSelling.setVisibility(View.GONE);
                                ivMyPoints.setImageDrawable(getResources().getDrawable(user.getPointsDrawableId()));
                            } else {
                                btnMyAdsLocal.setVisibility(View.GONE);
                                tvPointsLabel.setVisibility(View.INVISIBLE);
                                ivMyPoints.setVisibility(View.INVISIBLE);
                                tvLabelSelling.setVisibility(View.VISIBLE);
                            }

                            if (RecentSearch.hasRecentSearches(HomeActivity.this)) {
                                btnRecentSearch.setVisibility(View.VISIBLE);
                            } else {
                                btnRecentSearch.setVisibility(View.GONE);
                            }
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
        btSearch.setEnabled(false);
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerms = tvSearchTerms.getText().toString().trim();
                if (!searchTerms.isEmpty()) {
                    if (searchTerms.length() >= 3) {
                        RecentSearch.storeLocal(HomeActivity.this, new RecentSearch(searchTerms));
                        openSearchResultActivity(searchTerms);
                    }
                }
            }
        });

        tvSearchTerms.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length() < 3){
                    btSearch.setEnabled(false);
                } else {
                    btSearch.setEnabled(true);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        btAdvertise = (Button)findViewById(R.id.btn_advertise);
        btAdvertise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(HomeActivity.this, AdvertiseActivity.class),
                        ADVERTISE_REQUEST_CODE);
            }
        });

        btMyAds = (Button)findViewById(R.id.btn_my_ads);
        btMyAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MyAdsList.class));
            }
        });

        btRecentSearches = (Button)findViewById(R.id.bt_recent);
        btRecentSearches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, RecentSearchesActivity.class));
            }
        });

        btCommerce = (Button)findViewById(R.id.btn_commerce);
        btCommerce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCommerceAdsActivity();
            }
        });

        btTrendingAds = (Button)findViewById(R.id.bt_trending_ads);
        btTrendingAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTrendingAdsActivity();
            }
        });

        btEvents = (Button)findViewById(R.id.bt_events);
        btEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEventAdsActivity();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADVERTISE_REQUEST_CODE && resultCode == RESULT_OK) {
            setAvailableOptions();
        }
    }

    private void openSearchResultActivity(String searchTerms) {
        Intent intent = new Intent(HomeActivity.this, SearchResultsActivity.class);
        intent.putExtra(SearchResultsActivity.SEARCH_TERMS_EXTRA_KEY, searchTerms);
        startActivity(intent);
    }

    private void openTrendingAdsActivity() {
        Intent intent = new Intent(HomeActivity.this, SearchResultsActivity.class);
        intent.putExtra(SearchResultsActivity.LIST_TYPE_EXTRA_KEY, SearchResultsActivity.ListType.Trending.getValue());
        startActivity(intent);
    }

    private void openEventAdsActivity() {
        Intent intent = new Intent(HomeActivity.this, SearchResultsActivity.class);
        intent.putExtra(SearchResultsActivity.LIST_TYPE_EXTRA_KEY, SearchResultsActivity.ListType.Events.getValue());
        startActivity(intent);
    }

    private void openCommerceAdsActivity() {
        Intent intent = new Intent(HomeActivity.this, SearchResultsActivity.class);
        intent.putExtra(SearchResultsActivity.LIST_TYPE_EXTRA_KEY, SearchResultsActivity.ListType.Commerce.getValue());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
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
