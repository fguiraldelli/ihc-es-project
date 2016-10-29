package ihces.barganha;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

public class SearchResultsActivity extends AppCompatActivity {

    public static final String SEARCH_TERMS_EXTRA_KEY = "searchTerms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        TextView tvSearchTerms = (TextView)findViewById(R.id.tv_search_terms);
        String searchTerms = getIntent().getStringExtra(SEARCH_TERMS_EXTRA_KEY);
        
        if (!searchTerms.trim().isEmpty()) {
            tvSearchTerms.setText("\"" + searchTerms + "\"");
        }

        String[] titles = {"Bacon", "Ham", "Tuna", "Candy", "Meatball", "Potato"};
        java.math.BigDecimal[] prices = {BigDecimal.valueOf(1.00), BigDecimal.valueOf(2.20),
                BigDecimal.valueOf(3.30), BigDecimal.valueOf(4.45), BigDecimal.valueOf(6.75), BigDecimal.valueOf(19.99)};
        String[] description = {"Desc 1",  "Desc 2", "Desc 3", "Desc 4", "Desc 5", "Desc 6"};
        ListAdapter chicosAdapter = new CustomAdapter(this, titles, description, prices);
        ListView advertiseListView = (ListView) findViewById(R.id.lv_results);
        advertiseListView.setAdapter(chicosAdapter);

        advertiseListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String food = String.valueOf(adapterView.getItemAtPosition(i));
                        Toast.makeText(SearchResultsActivity.this, food, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
