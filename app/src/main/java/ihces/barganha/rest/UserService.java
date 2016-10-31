package ihces.barganha.rest;

import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.math.BigDecimal;

import ihces.barganha.models.User;

public class UserService extends ApiServiceBase {

    private static final String RESOURCE = "login";
    private final Gson gson = new Gson();

    public void postLogin(User user, ServiceResponseListener<String> listener) {
        if (isMock) {
            listener.onResponse("OK");
            return;
        }

        String json = gson.toJson(user, User.class);
        StringRequest request = makePostRequest(listener, json);
        queue.add(request);
    }

    public void getMyPoints(User user, ServiceResponseListener<User> listener) {
        if (isMock) {
            user.setPoints(BigDecimal.valueOf(70));
            user.setHasAds(true);
            listener.onResponse(user);
            return;
        }

        String json = gson.toJson(user, User.class);
        GsonRequest<User> request = makePostRetrieveRequest(User.class, listener, json);
        queue.add(request);
    }

    @Override
    protected String makeUrl() {
        return BASE_URL + RESOURCE;
    }

}