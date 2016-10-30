package ihces.barganha.models;

import java.math.BigDecimal;

import ihces.barganha.R;

public class User {

    private String authToken;
    private int collegeId = 1;
    private BigDecimal points;
    private boolean hasAds;

    public User() {} // Enable serialization

    public User(String authToken, int collegeId) {
        this(authToken, collegeId, BigDecimal.valueOf(50), false);
    }

    public User(String authToken, int collegeId, BigDecimal points, boolean hasAds) {
        this.authToken = authToken;
        this.collegeId = collegeId;
        this.points = points;
        this.hasAds = hasAds;
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

    public BigDecimal getPoints() {
        return points;
    }

    public void setPoints(BigDecimal points) {
        this.points = points;
    }

    public boolean isHasAds() {
        return hasAds;
    }

    public void setHasAds(boolean hasAds) {
        this.hasAds = hasAds;
    }

    public static User getStoredLocal() {
        return new User();
    }

    public int getPointsDrawableId() {
        int id = R.drawable.ic_mood_neutral;

        if (hasAds) {
            if (points.compareTo(new BigDecimal(20)) < 0)
                id = R.drawable.ic_mood_angry;
            else if (points.compareTo(new BigDecimal(40)) < 0)
                id = R.drawable.ic_mood_troubled;
            else if (points.compareTo(new BigDecimal(60)) < 0)
                id = R.drawable.ic_mood_neutral;
            else if (points.compareTo(new BigDecimal(80)) < 0)
                id = R.drawable.ic_mood_content;
            else
                id = R.drawable.ic_mood_happy;
        }

        return id;
    }
}
