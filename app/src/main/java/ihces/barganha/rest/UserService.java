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

        JSONObject json = null;
        try {
            json = new JSONObject(gson.toJson(user));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = makePostRequest(listener, json);
        queue.add(request);
    }

    public void getMyUser(User user, ServiceResponseListener<User[]> listener) {
        getUser(user.getAuthToken(), listener);
    }

    public void getUser(String token, ServiceResponseListener<User[]> listener) {
        if (isMock) {
            User user = new User(1, token, 1, 5, 1, "55 11 5555 0000", "Persona1", User.GENDER_FEMALE_FACEBOOK, 12345789);
            listener.onResponse(new User[]{user});
            return;
        }

        Map<String, String> params = new HashMap<>();
        try {
            params.put("token", token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonArrayRequest request = makeGetRequest(User[].class, listener, params);
        queue.add(request);
    }

    public void getUser(int id, ServiceResponseListener<User> listener) {
        if (isMock) {
            User user = new User(id, "abcde12345", 1, 5, 1, "55 11 5555 0000", "Persona1", User.GENDER_FEMALE_FACEBOOK, 12345789);
            listener.onResponse(user);
            return;
        }

        String param = id + GET_EXTENSION;

        JsonObjectRequest request = makeGetRequest(User.class, listener, param);
        queue.add(request);
    }

    @Override
    protected String makeUrl() {
        return BASE_URL + RESOURCE;
    }

}