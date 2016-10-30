package ihces.barganha.rest;

import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import ihces.barganha.models.Ad;

public class AdService extends ApiServiceBase {

    private static final String RESOURCE = "anuncios.json";
    private final Gson gson = new Gson();

    public void postAd(Ad ad, ServiceResponseListener<String> listener) {
        if (isMock) {
            listener.onResponse("OK");
            return;
        }

        String json = gson.toJson(ad, Ad.class);
        StringRequest request = makePostRequest(listener, json);
        queue.add(request);
    }

    public void searchAds(String terms, ServiceResponseListener<Ad[]> listener) {
        if (isMock) {
            listener.onResponse(new Ad [0]);
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("search", terms);
        GsonRequest<Ad[]> request = makeGetRequest(Ad[].class, listener, params);
        queue.add(request);
    }

    @Override
    protected String makeUrl() {
        return BASE_URL + RESOURCE;
    }
}
