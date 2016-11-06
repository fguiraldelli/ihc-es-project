package ihces.barganha.rest;


import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import ihces.barganha.models.User;

public class UserPointsService extends ApiServiceBase {

    private static final String RESOURCE = "avaliar";
    private final Gson gson = new Gson();

    @Override
    protected String makeUrl()  {
        return BASE_URL + RESOURCE;
    }

    public void postEvaluation(String token, int id, String evaluation,
                               ServiceResponseListener<String> listener) {
        if (isMock) {
            listener.onResponse("OK");
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("token", token);
            json.put("id", id);
            json.put("avaliacao", evaluation);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = makePostRequest(listener, json);
        queue.add(request);
    }
}
