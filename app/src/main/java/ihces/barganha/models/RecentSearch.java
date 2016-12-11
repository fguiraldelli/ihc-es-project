package ihces.barganha.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecentSearch {

    public static final int MINUTE_SECONDS = 60;
    public static final int HOUR_SECONDS = 60 * MINUTE_SECONDS;
    public static final int DAY_SECONDS = (HOUR_SECONDS * 24);
    public static final int MONTH_SECONDS = (DAY_SECONDS * 30);
    public static final int YEAR_SECONDS = (MONTH_SECONDS * 12);

    private static final String RECENTSEARCH_FILE_KEY = "recent.pref";
    private static final String SEARCH_PREF_KEY = "searchList";

    private String terms;
    private Date lastSearch;

    public RecentSearch(String terms) {
        this(terms, Calendar.getInstance().getTime());
    }

    public RecentSearch(String terms, Date lastSearch) {
        this.terms = terms.trim().toLowerCase();
        this.lastSearch = lastSearch;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public Date getLastSearch() {
        return lastSearch;
    }

    public void setLastSearch(Date lastSearch) {
        this.lastSearch = lastSearch;
    }

    public static void storeLocal(Context context, RecentSearch search) {
        Gson gson = new Gson();
        SharedPreferences prefs = context.getSharedPreferences(RECENTSEARCH_FILE_KEY,
                Context.MODE_PRIVATE);
        String saved = prefs.getString(SEARCH_PREF_KEY, gson.toJson(new RecentSearch[0]));

        Type listType = new TypeToken<ArrayList<RecentSearch>>(){}.getType();
        ArrayList<RecentSearch> savedTerms = gson.fromJson(saved, listType);

        savedTerms.remove(search); // clear any previous occurrence

        savedTerms.add(search); // adds new occurrence with last search date

        String json = new Gson().toJson(savedTerms);
        prefs.edit().putString(SEARCH_PREF_KEY, json).commit();
    }

    public static void clearLocal(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(RECENTSEARCH_FILE_KEY,
                Context.MODE_PRIVATE);
        prefs.edit().remove(SEARCH_PREF_KEY).commit();
    }

    public static List<RecentSearch> getStoredLocal(Context context) {
        Gson gson = new Gson();
        SharedPreferences prefs = context.getSharedPreferences(RECENTSEARCH_FILE_KEY,
                Context.MODE_PRIVATE);
        String saved = prefs.getString(SEARCH_PREF_KEY, gson.toJson(new RecentSearch[0]));

        Type listType = new TypeToken<ArrayList<RecentSearch>>(){}.getType();
        return gson.fromJson(saved, listType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RecentSearch that = (RecentSearch) o;

        return terms.equals(that.terms.trim().toLowerCase());

    }

    @Override
    public int hashCode() {
        return terms.hashCode();
    }

    public String getLastSearchFormatted() {
        StringBuilder result = new StringBuilder();

        long seconds = (Calendar.getInstance().getTimeInMillis()/1000 - this.lastSearch.getTime()/1000);

        if (seconds < 60) {
            result.append("agora");
        } else {
            long years = seconds / YEAR_SECONDS;
            seconds -= years * YEAR_SECONDS;
            long months = seconds / MONTH_SECONDS;
            seconds -= months / MONTH_SECONDS;
            long days = seconds / DAY_SECONDS;
            seconds -= days * DAY_SECONDS;
            long hours = seconds / HOUR_SECONDS;
            seconds -= hours * HOUR_SECONDS;
            long minutes = seconds / MINUTE_SECONDS;

            if (years > 0) {
                result.append(years);
                result.append("a ");
            }
            if (months > 0) {
                result.append(months);
                result.append("m ");
            }
            if (days > 0) {
                result.append(days);
                result.append("d ");
            }
            if (hours > 0) {
                result.append(hours);
                result.append("h ");
            }
            if (minutes > 0) {
                result.append(minutes);
                result.append("min");
            }
        }

        return result.toString();
    }
}
