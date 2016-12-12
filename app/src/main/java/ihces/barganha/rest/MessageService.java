package ihces.barganha.rest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import ihces.barganha.models.NegotiationMessage;

public class MessageService extends ApiServiceBase {

    private static final String RESOURCE = "conversas";

    public void getConversation(int adId, int lastMessageId, ServiceResponseListener<NegotiationMessage[]> listener) {
        if (isMock) {
            listener.onResponse(new NegotiationMessage[0]);
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

    public void sendMessage(int adId, int senderId, String text,
                            final ServiceResponseListener<NegotiationMessage> listener) {
        if (isMock) {
            listener.onResponse(new NegotiationMessage(10, 1, "hello message!"));
            return;
        }

        NegotiationMessage message = new NegotiationMessage(adId, senderId, text);

        JSONObject json = null;
        try {
            json = new JSONObject(gson.toJson(message));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                makeUrl(),
                json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        NegotiationMessage oResponse = gson.fromJson(response.toString(), NegotiationMessage.class);
                        listener.onResponse(oResponse);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error);
                    }
                }
        );

        queue.add(request);
    }
}
