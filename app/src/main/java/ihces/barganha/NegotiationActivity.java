package ihces.barganha;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ihces.barganha.models.NegotiationMessage;
import ihces.barganha.models.User;
import ihces.barganha.rest.MessageService;
import ihces.barganha.rest.ServiceResponseListener;

public class NegotiationActivity extends AppCompatActivity {

    private static final String AD_ID_EXTRA_KEY = "adId";
    private static final String AD_TITLE_EXTRA_KEY = "adTitle";

    private int adId;
    private int lastId;
    private int myUserId;
    private MessageService service;

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

        String adTitle = getIntent().getStringExtra(AD_TITLE_EXTRA_KEY);
        if (adTitle != null) {
            ((TextView) findViewById(R.id.tv_messages_ad_title)).setText(adTitle);
        }

        adId = getIntent().getIntExtra(AD_ID_EXTRA_KEY, 42);
        lastId = 0;
        myUserId = User.getStoredLocal(this).getId();

        service = new MessageService();
        service.start(NegotiationActivity.this);
        final MessagesAdapter adapter = new MessagesAdapter(NegotiationActivity.this);

        service.getConversation(adId, lastId,
                new ServiceResponseListener<NegotiationMessage[]>() {
                    @Override
                    public void onResponse(NegotiationMessage[] response) {
                        adapter.setData(response);
                        ListView listView = (ListView) findViewById(R.id.lv_messages);
                        listView.setAdapter(adapter);
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        lastId = response[response.length-1].getId();
                    }

                    @Override
                    public void onError(Exception error) { }
                });

        findViewById(R.id.bt_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                String messageText = ((EditText)findViewById(R.id.et_message)).getText().toString();
                service.sendMessage(adId, myUserId, messageText.trim(), new ServiceResponseListener<NegotiationMessage>() {
                    @Override
                    public void onResponse(NegotiationMessage response) {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        adapter.addData(response);
                    }

                    @Override
                    public void onError(Exception error) {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        setResult(RESULT_CANCELED);
                        Toast.makeText(NegotiationActivity.this, getText(R.string.toast_post_ad_error), Toast.LENGTH_LONG).show();
                        Log.e("NegotiationActivity", "Couldn't POST message to our API.", error);
                    }
                });
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
}
