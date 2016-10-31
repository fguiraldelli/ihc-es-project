package ihces.barganha;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import ihces.barganha.models.Ad;
import ihces.barganha.photo.Imaging;

public class CustomAdapter extends BaseAdapter{

    private final Ad[] ads;
    private LayoutInflater adsInflater;

    public CustomAdapter(Context context, Ad[] ads) {
        this.ads = ads;
        adsInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return ads.length;
    }

    @Override
    public Object getItem(int i) {
        return this.ads[i];
    }

    @Override
    public long getItemId(int i) {
        return this.ads[i].getId();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View customView;

        if( convertView == null) {
            customView = adsInflater.inflate(R.layout.custom_row, parent, false);
        } else {
            customView = convertView;
        }

        TextView titleText = (TextView) customView.findViewById(R.id.row_title);
        TextView priceText = (TextView) customView.findViewById(R.id.row_price);
        TextView descriptionText = (TextView) customView.findViewById(R.id.row_description);
        ImageView itemImage = (ImageView) customView.findViewById(R.id.row_image);

        Ad currentAd = ads[position];

        titleText.setText(currentAd.getTitle());
        priceText.setText(currentAd.getPrice());
        descriptionText.setText(currentAd.getDescription());
        itemImage.setImageBitmap(Imaging.base64DecodeImage(currentAd.getPhotoBase64()));

        return customView;

    }


}
