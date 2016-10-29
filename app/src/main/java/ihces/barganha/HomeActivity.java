package ihces.barganha;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    AutoCompleteTextView tvSearchTerms;
    Button btSearch;
    Button btAdvertise;
    Button btMyAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_home);
        setButtonEvents();
    }

    private void setButtonEvents() {
        tvSearchTerms = (AutoCompleteTextView)findViewById(R.id.tv_search_terms);

        btSearch = (Button)findViewById(R.id.btn_search);
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerms = tvSearchTerms.getText().toString();
                if (!searchTerms.trim().isEmpty()) {
                    openSearchResultActivity(searchTerms);
                }
            }
        });

        btAdvertise = (Button)findViewById(R.id.btn_advertise);
        btAdvertise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, AdvertiseActivity.class));
            }
        });

        btMyAds = (Button)findViewById(R.id.btn_my_ads);
        btMyAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MyAdsList.class));
            }
        });
    }

    private void openSearchResultActivity(String searchTerms) {
        Intent intent = new Intent(HomeActivity.this, SearchResultsActivity.class);
        intent.putExtra(SearchResultsActivity.SEARCH_TERMS_EXTRA_KEY, searchTerms);
        startActivity(intent);
    }

}
