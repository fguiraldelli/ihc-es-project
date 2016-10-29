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

public class CustomAdapter extends BaseAdapter{

    private final String[] titles;
    private final BigDecimal[] prices;
    private final String[] description;
    private LayoutInflater adsInflater;
    private DecimalFormat decimalFormatter = new DecimalFormat("#0.##");

    public CustomAdapter(Context context, String[] titles, String[] description,BigDecimal[] prices) {
        this.titles = titles;
        this.description = description;
        this.prices = prices;
        adsInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public Object getItem(int i) {
        return this.titles[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
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

        titleText.setText(titles[position]);
        priceText.setText(decimalFormatter.format(prices[position]));
        descriptionText.setText(description[position]);
        itemImage.setImageResource(R.drawable.com_facebook_profile_picture_blank_square);

        return customView;

    }


}
