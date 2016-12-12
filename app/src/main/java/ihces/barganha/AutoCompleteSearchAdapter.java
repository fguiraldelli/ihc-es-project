package ihces.barganha;


import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ihces.barganha.rest.AutoCompleteService;
import ihces.barganha.rest.ServiceResponseListener;

public class AutoCompleteSearchAdapter extends ArrayAdapter<String> implements Filterable {

    public final int THRESHOLD = 3;

    private ArrayList<String> suggestions;
    private AutoCompleteService service;
    private String originalText;

    public AutoCompleteSearchAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        service = new AutoCompleteService();
        service.start(context);
        //service.setAsMock();
        suggestions = new ArrayList<>();
        originalText = "";
    }

    @Override
    public int getCount() {
        return suggestions.size();
    }

    @Override
    public String getItem(int index) {
        return suggestions.size() > index ? suggestions.get(index) : null;
    }

    @Override
    public Filter getFilter() {
        Filter myFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence terms) {

                FilterResults filterResults = new FilterResults();

                if(terms != null && terms.charAt(terms.length()-1) != ' ') {
                    originalText = terms.toString();

                    suggestions.clear();

                    String[] splitTerms = originalText.split(" ");
                    String term = splitTerms[splitTerms.length-1];

                    if (term.length() >= THRESHOLD) {

                        // http://bit.ly/1GfNDHY
                        final CountDownLatch latch = new CountDownLatch(1);

                        try {
                            service.getSuggestions(term, new ServiceResponseListener<String[]>() {
                                @Override
                                public void onResponse(String[] response) {
                                    suggestions = new ArrayList<>(Arrays.asList(response));
                                    latch.countDown();
                                }

                                @Override
                                public void onError(Exception error) {
                                    Log.e("AutoComplete", error.getMessage());
                                    latch.countDown();
                                }
                            });

                            latch.await(5, TimeUnit.SECONDS);

                            filterResults.values = suggestions;
                            filterResults.count = suggestions.size();
                        } catch (Exception e) {
                            Log.e("AutoComplete", e.getMessage());
                        }
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if(results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return myFilter;
    }

    public String getOriginalText() {
        return originalText;
    }
}
