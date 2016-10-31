package ihces.barganha.rest;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ihces.barganha.models.User;

public class UserService extends ApiServiceBase {

    private static final String RESOURCE = "usuarios";
    private final Gson gson = new Gson();

    public void postLogin(User user, ServiceResponseListener<String> listener) {
        if (isMock) {
            listener.onResponse("OK");
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("token", user.getAuthToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = makePostRequest(listener, json);
        queue.add(request);
    }

    public void getMyUser(User user, ServiceResponseListener<User[]> listener) {
        if (isMock) {
            user.setPoints(5);
            user.setAds(1);
            listener.onResponse(new User[]{user});
            return;
        }

        Map<String, String> params = new HashMap<>();
        try {
            params.put("token", user.getAuthToken());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonArrayRequest request = makeGetRequest(User[].class, listener, params);
        queue.add(request);
    }

    @Override
    protected String makeUrl() {
        return BASE_URL + RESOURCE;
    }

}