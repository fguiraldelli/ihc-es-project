package ihces.barganha.rest;

import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import ihces.barganha.models.User;

public class UserService extends ApiServiceBase {

    private static final String RESOURCE = "login.json";
    private final Gson gson = new Gson();

    public void postLogin(User user, final ServiceResponseListener<String> listener) {
        if (isMock) {
            listener.onResponse("OK");
            return;
        }

        String json = gson.toJson(user, User.class);
        StringRequest request = makePostRequest(listener, json);
        queue.add(request);
    }

    @Override
    protected String makeUrl() {
        return BASE_URL + RESOURCE;
    }
}
