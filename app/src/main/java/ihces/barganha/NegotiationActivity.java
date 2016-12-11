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
import android.widget.ListAdapter;
import android.widget.ListView;

import ihces.barganha.models.NegotiationMessage;
import ihces.barganha.rest.MessageService;
import ihces.barganha.rest.ServiceResponseListener;

public class NegotiationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String AD_ID_EXTRA_KEY = "adId";

    private int adId;
    private int lastId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_negotiation);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        adId = getIntent().getIntExtra(AD_ID_EXTRA_KEY, 42);
        lastId = 0;

        MessageService service = new MessageService();
        service.start(NegotiationActivity.this);
        service.getConversation(adId, lastId,
                new ServiceResponseListener<NegotiationMessage[]>() {
                    @Override
                    public void onResponse(NegotiationMessage[] response) {
                        final ListAdapter adapter = new MessagesAdapter(NegotiationActivity.this, response);
                        ListView listView = (ListView) findViewById(R.id.lv_messages);
                        listView.setAdapter(adapter);
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception error) {

                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(NegotiationActivity.this, SettingsActivity.class));
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
    public void onClick(View v) {

    }
}
