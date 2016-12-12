package ihces.barganha.rest;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ihces.barganha.models.Ad;

public class AdService extends ApiServiceBase {

    private static final String RESOURCE = "anuncios";
    private final Gson gson = new Gson();

    public void postAd(Ad ad, ServiceResponseListener<String> listener) {
        if (isMock) {
            listener.onResponse("OK");
            return;
        }

        JSONObject json = null;
        try {
            json = new JSONObject(gson.toJson(ad));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = makePostRequest(listener, json);
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

    public void getTrendingAds(int localId, ServiceResponseListener<Ad[]> listener) {
        if (isMock) {
            listener.onResponse(new Ad [0]);
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("id_local", String.valueOf(localId));
        JsonArrayRequest request = makeGetRequest(Ad[].class, listener, params);
        queue.add(request);
    }

    public void getEventAds(int localId, ServiceResponseListener<Ad[]> listener) {
        if (isMock) {
            listener.onResponse(new Ad [0]);
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("id_local", String.valueOf(localId));
        JsonArrayRequest request = makeGetRequest(makeUrl(BASE_URL + "eventos", params), Ad[].class, listener);
        queue.add(request);
    }

    public void getCommerceAds(int localId, ServiceResponseListener<Ad[]> listener) {
        if (isMock) {
            listener.onResponse(new Ad [0]);
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("id_local", String.valueOf(localId));
        JsonArrayRequest request = makeGetRequest(makeUrl(BASE_URL + "comercio", params), Ad[].class, listener);
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

    public void markClosed(String token, Ad ad, ServiceResponseListener<String> listener)
    {
        if (isMock) {
            listener.onResponse("OK");
            return;
        }

        JSONObject jo = new JSONObject();
        try {
            jo.put("negocio_fechado", true);
            jo.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = makePutRequest(listener, String.valueOf(ad.getId()), jo);
        queue.add(request);
    }

    @Override
    protected String makeUrl() {
        return BASE_URL + RESOURCE;
    }
}
