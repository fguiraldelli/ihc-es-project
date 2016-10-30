package ihces.barganha;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

import ihces.barganha.photo.Imaging;

public class AdvertiseActivity extends AppCompatActivity {

    private static final int RESULT_IMAGE_CHOSEN = 1;
    // Make it a field so as to allow checking user locale in the future.
    private String separator = ",";
    private ImageButton ibPhoto;
    private Uri currentPhotoUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_advertise);

        setupPriceInput();

        findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ibPhoto = (ImageButton)findViewById(R.id.bt_choose_photo);
        ibPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_IMAGE_CHOSEN);
            }
        });
    }

    private void setupPriceInput() {
        EditText etPrice = (EditText)findViewById(R.id.et_price);
        etPrice.setKeyListener(DigitsKeyListener.getInstance("0123456789" + separator));

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

    private Bitmap getBitmapFromUri(Uri uri) {
        Bitmap image = null;

        try {
            ParcelFileDescriptor descriptor = getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = descriptor.getFileDescriptor();
            descriptor.close();
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        } catch (NullPointerException | IOException e) {
            e.printStackTrace();
        }

        return image;
    }
}
