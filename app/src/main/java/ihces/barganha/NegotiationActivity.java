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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import ihces.barganha.models.Ad;
import ihces.barganha.models.NegotiationMessage;
import ihces.barganha.models.User;
import ihces.barganha.rest.AdService;
import ihces.barganha.rest.MessageService;
import ihces.barganha.rest.ServiceResponseListener;

public class NegotiationActivity extends AppCompatActivity {

    public static final String AD_ID_EXTRA_KEY = "adId";
    public static final String AD_USERID_EXTRA_KEY = "adUserId";
    public static final String AD_TITLE_EXTRA_KEY = "adTitle";
    public static final String AD_PHONE_EXTRA_KEY = "adPhone";

    private int adId;
    private int adUserId;
    private int lastId;
    private User myUser;
    private MessageService service;
    private AdService adService;
    private View btSend;
    private View btMakeDeal;
    private View trDue;

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
        adUserId = getIntent().getIntExtra(AD_USERID_EXTRA_KEY, 1);
        myUser = User.getStoredLocal(this);
        lastId = 0;

        btSend = findViewById(R.id.bt_send);
        btMakeDeal = findViewById(R.id.bt_make_deal);
        trDue = findViewById(R.id.tr_due);

        setControlVisibility();

        service = new MessageService();
        service.start(NegotiationActivity.this);
        adService = new AdService();
        adService.start(NegotiationActivity.this);

        final MessagesAdapter adapter = new MessagesAdapter(NegotiationActivity.this);

        service.getConversation(adId, lastId,
                new ServiceResponseListener<NegotiationMessage[]>() {
                    @Override
                    public void onResponse(NegotiationMessage[] response) {
                        adapter.setData(response);
                        ListView listView = (ListView) findViewById(R.id.lv_messages);
                        listView.setAdapter(adapter);
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        lastId = response.length == 0 ? 0 : response[response.length-1].getId();
                    }

                    @Override
                    public void onError(Exception error) { }
                });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                String messageText = ((EditText)findViewById(R.id.et_message)).getText().toString();
                service.sendMessage(adId, myUser.getId(), messageText.trim(), new ServiceResponseListener<NegotiationMessage>() {
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

        btMakeDeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

                String token = myUser.getAuthToken();
                final Ad ad = new Ad();
                ad.setId(adId);
                ad.setUserId(adUserId);

                adService.markClosed(token, ad, new ServiceResponseListener<String>() {
                    @Override
                    public void onResponse(String response) {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        ad.setIsDue(true);
                        Ad.storeMyDueAd(NegotiationActivity.this, ad);
                        setControlVisibility();
                    }

                    @Override
                    public void onError(Exception error) {
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        Log.e("NegotiationActivity", "Error setting ad due", error);
                    }
                });
            }
        });
    }

    private void setControlVisibility() {
        Ad ad = new Ad();
        ad.setId(adId);
        setControlVisibility(ad);
    }

    private void setControlVisibility(Ad ad) {
        int visibility;

        if (Ad.isMyAdDue(this, ad) || ad.getIsDue()) {
            visibility = View.GONE;
            trDue.setVisibility(View.VISIBLE);
        } else {
            visibility = View.VISIBLE;
            trDue.setVisibility(View.GONE);
        }

        if (ad.getUserId() != myUser.getId()) {
            btMakeDeal.setVisibility(View.GONE);
        } else {
            btMakeDeal.setVisibility(visibility);
        }

        btSend.setVisibility(visibility);
        findViewById(R.id.et_message).setVisibility(visibility);
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
