package ihces.barganha.rest;

import com.android.volley.toolbox.JsonArrayRequest;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import ihces.barganha.models.NegotiationMessage;

/**
 * Created by fop on 11/12/16.
 */

public class MessageService extends ApiServiceBase {

    private static final String RESOURCE = "conversas";
    private final Gson gson = new Gson();


    public void getConversation(int adId, int lastMessageId, ServiceResponseListener<NegotiationMessage[]> listener) {
        if (isMock) {
            listener.onResponse(new NegotiationMessage [0]);
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(lastMessageId));
        params.put("id_anuncio", String.valueOf(adId));
        JsonArrayRequest request = makeGetRequest(NegotiationMessage[].class, listener, params);
        queue.add(request);
    }

    @Override
    protected String makeUrl() {
        return BASE_URL + RESOURCE;
    }
}
