package ihces.barganha.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.annotations.SerializedName;

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
    @SerializedName("pontosanunciante")
    private int points;
    @SerializedName("imagem")
    private String photoBase64;
    @SerializedName("id_usuario")
    private int userId;
    @SerializedName("negocio_fechado")
    private boolean isDue;
    @SerializedName("id_facebook")
    private long facebookId;
    @SerializedName("token")
    private String authToken;
    @SerializedName("tempoanuncio")
    private String adTime;
    @SerializedName("visualizacoes")
    private int views;
    @SerializedName("tipoanuncio")
    private char adType; // among [c, d, e, l] -> Compra/venda, Doação, Evento, comércio Local.
    @SerializedName("datahoraevento")
    private String eventDateTime;
    @SerializedName("diassemana")
    private String weekDays;

    private String filename = "";

    public Ad() { } // Enable Serialization

    public Ad(int id, String authToken, int userId, String title, String description, String price, long facebookId) {
        this(id, authToken, userId, title, description, price, facebookId, "", 'c', "", "");
    }

    public Ad(int id, String authToken, int userId, String title, String description, String price,
              long facebookId, String adTime, char adType, String eventDateTime, String weekDays) {
        this.id = id;
        this.authToken = authToken;
        this.userId = userId;
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

        this.photoBase64 = "";
        this.filename = "";
        this.isDue = false;
        this.facebookId = facebookId;
        this.adTime = adTime;
        this.adType = adType;
        this.eventDateTime = eventDateTime;
        this.weekDays = weekDays;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public long getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(long facebookId) {
        this.facebookId = facebookId;
    }

    public int getPointsDrawableId() {
        return User.getDrawableId(points);
    }

    public String getAdTime() {
        return adTime;
    }

    public void setAdTime(String adTime) {
        this.adTime = adTime;
    }

    public char getAdType() {
        return adType;
    }

    public void setAdType(char adType) {
        this.adType = adType;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getWeekDays() {
        return weekDays;
    }

    public void setWeekDays(String weekDays) {
        this.weekDays = weekDays;
    }
}
