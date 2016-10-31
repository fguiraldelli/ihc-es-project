package ihces.barganha.rest;

import android.util.Log;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ihces.barganha.models.Ad;
import ihces.barganha.models.User;

public class AdService extends ApiServiceBase {

    private static final String RESOURCE = "anuncios";

    public void postAd(Ad ad, ServiceResponseListener<String> listener) {
        if (isMock) {
            listener.onResponse("OK");
            return;
        }

        JSONObject jo = new JSONObject();
        try {
            jo.put("titulo", ad.getTitle());
            jo.put("descricao", ad.getDescription());
            jo.put("preco", ad.getPrice());
            jo.put("imagem", ad.getPhotoBase64());
            jo.put("token", ad.getAuthToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = makePostRequest(listener, jo);
        queue.add(request);
    }

    public void searchAds(String terms, ServiceResponseListener<Ad[]> listener) {
        if (isMock) {
            listener.onResponse(new Ad [0]);
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("search", terms);
        JsonArrayRequest request = makeGetRequest(Ad[].class, listener, params);
        queue.add(request);
    }

    public void getMyAds(String token, ServiceResponseListener<Ad[]> listener) {
        if (isMock) {
            listener.onResponse(new Ad [0]);
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("token", token);
        JsonArrayRequest request = makeGetRequest(Ad[].class, listener, params);
        queue.add(request);
    }

    @Override
    protected String makeUrl() {
        return BASE_URL + RESOURCE;
    }
}
