package ihces.barganha.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import ihces.barganha.R;

public class User {

    private static final String USER_FILE_KEY = "loggedUser.pref";
    private static final String USER_PREF_KEY = "user";

    @SerializedName("token")
    private String authToken = "";
    private transient int collegeId = 1;
    @SerializedName("pontos")
    private int points = 3;
    @SerializedName("anuncios")
    private int ads = 0;
    @SerializedName("celular")
    private String cellPhone = "";

    public User() {} // Enable serialization

    public User(String authToken, int collegeId, String cellPhone) {
        this(authToken, collegeId, cellPhone, 3, 0);
    }

    public User(String authToken, int collegeId, String cellPhone, int points, int ads) {
        this.authToken = authToken;
        this.collegeId = collegeId;
        this.cellPhone = cellPhone;
        this.points = points;
        this.ads = ads;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public int getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(int collegeId) {
        this.collegeId = collegeId;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getAds() {
        return ads;
    }

    public void setAds(int ads) {
        this.ads = ads;
    }

    public boolean isHasAds() {
        return ads > 0;
    }

    public static void storeLocal(Context context, User user) {
        String json = new Gson().toJson(user);
        SharedPreferences prefs = context.getSharedPreferences(USER_FILE_KEY,
                Context.MODE_PRIVATE);
        prefs.edit().putString(USER_PREF_KEY, json).apply();
    }

    public static User getStoredLocal(Context context) {
        Gson gson = new Gson();
        SharedPreferences prefs = context.getSharedPreferences(USER_FILE_KEY,
                Context.MODE_PRIVATE);
        String json = prefs.getString(USER_PREF_KEY, gson.toJson(new User()));
        User user = gson.fromJson(json, User.class);
        return user;
    }

    public int getPointsDrawableId() {
        int id = R.drawable.ic_mood_neutral;
        if (isHasAds()) {
            switch (points) {
                case 1:
                    id = R.drawable.ic_mood_angry; break;
                case 2:
                    id = R.drawable.ic_mood_troubled; break;
                case 3:
                    id = R.drawable.ic_mood_neutral; break;
                case 4:
                    id = R.drawable.ic_mood_content; break;
                case 5:
                    id = R.drawable.ic_mood_happy; break;
                default:
                    id = R.drawable.ic_mood_neutral; break;
            }
        }
        return id;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }
}
