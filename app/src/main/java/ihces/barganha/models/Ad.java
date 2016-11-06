package ihces.barganha.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.annotations.SerializedName;

import ihces.barganha.AdDetailsActivity;

public class Ad {

    public static final String SEPARATOR = ",";

    private static final String CONTACTING_AD_FILE_KEY = "contacting_ad.pref";
    private static final String AD_ID_PREF_KEY = "adId-";
    private static final String EVAL_AD_ID_PREF_KEY = "evaluationForAdId-";

    private static final String MY_DUE_AD_FILE_KEY = "due_ads.pref";

    private int id;
    @SerializedName("titulo")
    private String title;
    @SerializedName("descricao")
    private String description;
    @SerializedName("preco")
    private String price;
    @SerializedName("imagem")
    private String photoBase64;
    @SerializedName("token")
    private String authToken;
    @SerializedName("negocio_fechado")
    private boolean isDue;

    private String filename = "";

    public Ad() { } // Enable Serialization

    public Ad(int id, String authToken, String title, String description, String price) {
        this(id, authToken, title, description, price, "");
    }

    public Ad(int id, String authToken, String title, String description, String price, String photoBase64) {
        this.id = id;
        this.authToken = authToken;
        this.title = title;
        this.description = description;

        if (!price.contains(SEPARATOR)) { // "100"
            this.price = price + SEPARATOR + "00";
        } else if (price.endsWith(SEPARATOR)) { // "100,"
            this.price = price + "00";
        } else if (price.indexOf(SEPARATOR) < price.length() - 3) { // "100,0"
            this.price = price + "0";
        } else {
            this.price = price;
        }

        this.photoBase64 = photoBase64;
        this.filename = "";
        this.isDue = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        if (!price.contains(SEPARATOR)) { // "100"
            return price + SEPARATOR + "00";
        } else if (price.endsWith(SEPARATOR)) { // "100,"
            return price + "00";
        } else if (price.indexOf(SEPARATOR) < price.length() - 3) { // "100,0"
            return price + "0";
        } else {
            return price;
        }
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPhotoBase64() {
        return photoBase64;
    }

    public void setPhotoBase64(String photoBase64) {
        this.photoBase64 = photoBase64;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean getIsDue() {
        return isDue;
    }

    public void setIsDue(boolean isDue) {
        this.isDue = isDue;
    }

    public static void storeContactingAd(Context context, Ad ad) {
        SharedPreferences prefs = context.getSharedPreferences(
                CONTACTING_AD_FILE_KEY,
                Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean(AD_ID_PREF_KEY + ad.getId(), true)
                .apply();
    }

    public static boolean isContactingAd(Context context, Ad ad) {
        SharedPreferences prefs = context.getSharedPreferences(
                CONTACTING_AD_FILE_KEY,
                Context.MODE_PRIVATE);
        return prefs.getBoolean(AD_ID_PREF_KEY + ad.getId(), false);
    }

    public static void setEvaluationForAd(Context context, Ad ad, String evaluation) {
        SharedPreferences prefs = context.getSharedPreferences(
                CONTACTING_AD_FILE_KEY,
                Context.MODE_PRIVATE);
        prefs.edit()
                .putString(EVAL_AD_ID_PREF_KEY + ad.getId(), evaluation)
                .apply();
    }

    public static String getEvaluationForAd(Context context, Ad ad) {
        SharedPreferences prefs = context.getSharedPreferences(
                CONTACTING_AD_FILE_KEY,
                Context.MODE_PRIVATE);
        return prefs.getString(EVAL_AD_ID_PREF_KEY + ad.getId(), "");
    }

    public static void storeMyDueAd(Context context, Ad ad) {
        SharedPreferences prefs = context.getSharedPreferences(
                MY_DUE_AD_FILE_KEY,
                Context.MODE_PRIVATE);
        prefs.edit()
                .putBoolean(AD_ID_PREF_KEY + ad.getId(), true)
                .apply();
    }

    public static boolean isMyAdDue(Context context, Ad ad) {
        SharedPreferences prefs = context.getSharedPreferences(
                MY_DUE_AD_FILE_KEY,
                Context.MODE_PRIVATE);
        return prefs.getBoolean(AD_ID_PREF_KEY + ad.getId(), false);
    }
}
