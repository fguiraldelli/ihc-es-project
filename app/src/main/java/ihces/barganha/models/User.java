package ihces.barganha.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import ihces.barganha.R;

public class User {

    private static final String USER_FILE_KEY = "loggedUser.pref";
    private static final String USER_PREF_KEY = "user";
    public static final String GENDER_FEMALE_FACEBOOK = "female";
    public static final String GENDER_MALE_FACEBOOK = "male";

    @SerializedName("id")
    private int id = 0;
    @SerializedName("token")
    private String authToken = "";
    @SerializedName("id_instituicao")
    private int collegeId = 1;
    @SerializedName("pontos")
    private int points = 3;
    @SerializedName("anuncios")
    private int ads = 0;
    @SerializedName("celular")
    private String cellPhone = "";
    @SerializedName("nome")
    private String name;
    @SerializedName("sexo")
    private String gender;
    @SerializedName("id_facebook")
    private long facebookId;

    public User(int id, String authToken, int collegeId, int points, int ads, String cellPhone, String name, String gender, long facebookId) {
        this.id = id;
        this.authToken = authToken;
        this.collegeId = collegeId;
        this.points = points;
        this.ads = ads;
        this.cellPhone = cellPhone;
        this.name = name;
        this.gender = gender;
        this.facebookId = facebookId;
    }

    public User() {} // Enable serialization

    public User(String authToken, int collegeId, String cellPhone, String name, String gender, long facebookId) {
        this(0, authToken, collegeId, 3, 0, cellPhone, name, gender, facebookId);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        prefs.edit().putString(USER_PREF_KEY, json).commit();
    }

    public static void clearLocal(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(USER_FILE_KEY,
                Context.MODE_PRIVATE);
        prefs.edit().remove(USER_PREF_KEY).commit();
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
            id = getDrawableId(points);
        }
        return id;
    }

    public static int getDrawableId(int points) {
        int id;
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
        return id;
    }

    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isFemale() {
        return this.gender == GENDER_FEMALE_FACEBOOK;
    }

    public long getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(long facebookId) {
        this.facebookId = facebookId;
    }
}
