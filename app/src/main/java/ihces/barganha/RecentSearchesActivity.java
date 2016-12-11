package ihces.barganha;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import ihces.barganha.models.Ad;
import ihces.barganha.models.RecentSearch;

public class RecentSearchesActivity extends AppCompatActivity {
    private RecentSearchesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_recent_searches);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        List<RecentSearch> searches = RecentSearch.getStoredLocal(this);
        adapter = new RecentSearchesAdapter(this, searches);

        if (searches.size() == 0) {
            ((TextView)findViewById(R.id.tv_recent_searches)).setText(R.string.tv_recent_searches_label_none);
        }

        ListView listView = (ListView) findViewById(R.id.lv_results);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        openSearchActivity((RecentSearch)adapter.getItem(i));
                    }
                }
        );

        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    private void openSearchActivity(RecentSearch item) {
        if (item != null && !item.getTerms().isEmpty()) {
            item.setLastSearch(Calendar.getInstance().getTime());
            RecentSearch.storeLocal(this, item);

            Intent intent = new Intent(this, SearchResultsActivity.class);
            intent.putExtra(SearchResultsActivity.SEARCH_TERMS_EXTRA_KEY, item.getTerms());
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

            case R.id.action_clear:
                RecentSearch.clearLocal(this);
                adapter.clearData();
                adapter.notifyDataSetChanged();
                ((TextView)findViewById(R.id.tv_recent_searches)).setText(R.string.tv_recent_searches_label_none);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.recent_searches_menu, menu);
        return true;
    }
}
