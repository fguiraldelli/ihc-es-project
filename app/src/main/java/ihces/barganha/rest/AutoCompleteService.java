package ihces.barganha.rest;

import com.android.volley.toolbox.JsonArrayRequest;

import java.util.HashMap;
import java.util.Map;

public class AutoCompleteService extends ApiServiceBase {

    private static final String RESOURCE = "completar";

    public void getSuggestions(String term, ServiceResponseListener<String[]> listener) {
        if (isMock) {
            listener.onResponse(new String[] { term + "-1", term + "-2", term + "-3" });
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("termo", term);
        JsonArrayRequest request = makeGetRequest(String[].class, listener, params);
        queue.add(request);
    }

    @Override
    protected String makeUrl() {
        return BASE_URL + RESOURCE;
    }
}
