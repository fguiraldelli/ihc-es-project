package ihces.barganha;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

import ihces.barganha.models.Ad;
import ihces.barganha.models.User;
import ihces.barganha.photo.Imaging;
import ihces.barganha.rest.AdService;
import ihces.barganha.rest.ServiceResponseListener;
import utils.NumberTextWatcher;

public class AdvertiseActivity extends AppCompatActivity {

    private static final int RESULT_IMAGE_CHOSEN = 1;

    // Make it a field so as to allow checking user locale in the future.
    private String separator = Ad.SEPARATOR;
    private ImageButton ibPhoto;
    private EditText etTitle;
    private EditText etDescription;
    private EditText etPrice;
    private Spinner spEvent;
    private Spinner spWeekDay;
    private String[] weekdaysIds;

    private CharSequence[] eventIds;

    // Keep it around till it's time to upload the image.
    private Uri currentPhotoUri = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_advertise);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        ArrayAdapter spEventAdapter = ArrayAdapter.createFromResource(this,
                R.array.event_names, R.layout.spinner_item);
        spEventAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spEvent = (Spinner) findViewById(R.id.sp_event);
        spEvent.setAdapter(spEventAdapter);
        spEvent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1){
                    etPrice.setVisibility(View.GONE);
                    etPrice.setText(" ");
                    spWeekDay.setVisibility(view.INVISIBLE);
                }
                else if(position == 2){
                    spWeekDay.setVisibility(view.VISIBLE);
                    etPrice.setVisibility(View.VISIBLE);
                }
                else{
                    etPrice.setVisibility(View.VISIBLE);
                    spWeekDay.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                etPrice.setVisibility(View.VISIBLE);
                spWeekDay.setVisibility(View.INVISIBLE);
            }
        });
        eventIds = getResources().getTextArray(R.array.event_ids);

        ArrayAdapter spWeekDayAdapter = ArrayAdapter.createFromResource(this,
                R.array.weekday_names, R.layout.spinner_item);
        spWeekDayAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spWeekDay = (Spinner) findViewById(R.id.sp_weekday);
        spWeekDay.setAdapter(spWeekDayAdapter);
        weekdaysIds = getResources().getStringArray(R.array.weekday_ids);

        findViews();
        setupPriceInput();
        // TODO only enable save button if all fields are provided

        findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ibPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_IMAGE_CHOSEN);
            }
        });

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (etTitle.getText().length() == 0 ||
                        etDescription.getText().length() == 0 ||
                        (spEvent.getSelectedItemPosition() != 1 && etPrice.getText().length() == 0) ||
                        currentPhotoUri == null) {
                    return;
                }

                v.setEnabled(false);
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

                try {
                    User storedUser = User.getStoredLocal(AdvertiseActivity.this);

                    Ad newAd = new Ad(0,
                            storedUser.getAuthToken(),
                            storedUser.getId(),
                            etTitle.getText().toString(),
                            etDescription.getText().toString(),
                            etPrice.getText().toString(),
                            storedUser.getFacebookId(),
                            eventIds[spEvent.getSelectedItemPosition()].toString().charAt(0),
                            weekdaysIds[spEvent.getSelectedItemPosition()]
                    );

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(AdvertiseActivity.this.getContentResolver(), currentPhotoUri);
                    Bitmap scaled = Imaging.correctSizeForUploading(bitmap);
                    newAd.setPhotoBase64(Imaging.base64EncodeImage(scaled));

                    AdService service = new AdService();
                    service.start(AdvertiseActivity.this);
                    service.postAd(newAd, new ServiceResponseListener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(AdvertiseActivity.this, getText(R.string.toast_post_ad_ok), Toast.LENGTH_LONG).show();
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onError(Exception error) {
                            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                            v.setEnabled(true);
                            setResult(RESULT_CANCELED);
                            Toast.makeText(AdvertiseActivity.this, getText(R.string.toast_post_ad_error), Toast.LENGTH_LONG).show();
                            Log.e("Advertise", "Couldn't POST ad to our API.", error);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void findViews() {
        etTitle = (EditText) findViewById(R.id.et_title);
        etDescription = (EditText) findViewById(R.id.et_description);
        etPrice = (EditText)findViewById(R.id.et_price);
        spEvent = (Spinner)findViewById(R.id.sp_event);
        ibPhoto = (ImageButton)findViewById(R.id.bt_choose_photo);
    }

    private void setupPriceInput() {
        etPrice.setKeyListener(DigitsKeyListener.getInstance("0123456789" + separator));
        // etPrice.addTextChangedListener(new NumberTextWatcher(etPrice));
        etPrice.addTextChangedListener(new TextWatcher() {
            String beforeText = "";
            String finalText = "";
            boolean changing = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().length() > 0) {
                    beforeText = s.toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (changing) return;

                String sequence = s.toString();

                if (sequence.length() > 0 && count > 0) {
                    if (beforeText.contains(separator)) {
                        String newPart = s.subSequence(start, start + count).toString();
                        char sepChar = separator.charAt(0);
                        if (newPart.contains(separator)) {
                            // prevent second decimal separator
                            StringBuffer buffer = new StringBuffer(sequence);
                            int removeIndex = sequence.lastIndexOf(sepChar);
                            finalText = buffer.replace(removeIndex, removeIndex + 1, "").toString();
                        } else if (sequence.substring(sequence.indexOf(sepChar) + 1).length() > 2) {
                            // limit cents part
                            int toRemove = 1;
                            finalText = sequence.substring(0, sequence.length() - toRemove);
                        }
                        else {
                            finalText = sequence;
                        }
                    } else {
                        finalText = sequence;
                    }
                } else {
                    finalText = sequence;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (changing) {
                    // prevent infinite loop
                    changing = false;
                    beforeText = "";
                    finalText = "";
                } else if (beforeText.length() > 0 &&
                        !finalText.equals(s.toString())) {
                    changing = true;
                    s.replace(0, s.length(), finalText);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == RESULT_IMAGE_CHOSEN) {
            try {
                Uri imageUri = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                bitmap = Imaging.correctSize(bitmap, 200);
                ibPhoto.setImageBitmap(bitmap);
                currentPhotoUri = imageUri;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(AdvertiseActivity.this, SettingsActivity.class));
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
