package ihces.barganha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ihces.barganha.models.RecentSearch;

public class RecentSearchesAdapter extends BaseAdapter {
    List<RecentSearch> searches;
    private LayoutInflater inflater;

    public RecentSearchesAdapter(Context context, List<RecentSearch> searches) {
        this.searches = searches;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return searches.size();
    }

    @Override
    public Object getItem(int position) {
        return searches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return searches.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View customView;

        if (convertView == null) {
            customView = inflater.inflate(R.layout.recent_search_row, parent, false);
        } else {
            customView = convertView;
        }

        TextView searchText = (TextView) customView.findViewById(R.id.tv_search_terms);
        TextView timeText = (TextView) customView.findViewById(R.id.tv_time);

        RecentSearch current = searches.get(position);
        searchText.setText(current.getTerms());
        timeText.setText(current.getLastSearchFormatted());

        return customView;
    }

    public void clearData() {
        this.searches.clear();
    }
}
