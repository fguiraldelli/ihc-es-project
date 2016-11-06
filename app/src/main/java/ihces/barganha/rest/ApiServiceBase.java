package ihces.barganha.rest;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public abstract class ApiServiceBase {

    public static final String BASE_URL = "http://54.145.242.159:1234/";
    private static final String GET_EXTENSION = ".json";
    protected boolean isMock = false;
    protected RequestQueue queue = null;

    public void start(Context context) {
        if (this.queue == null) {
            this.queue = Volley.newRequestQueue(context);
        }
    }

    public void setAsMock() {
        isMock = true;
    }

    @NonNull
    protected JsonObjectRequest makePostRequest(final ServiceResponseListener<String> listener,
                                                final JSONObject postData) {
        return new JsonObjectRequestNoExpect(makeUrl(),
                postData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onResponse(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error);
                    }
                }
        );
    }

    @NonNull
    protected <T> JsonArrayRequest makeGetRequest(final Class<T> outClass,
                                                  final ServiceResponseListener<T> listener,
                                                  Map<String, String> params) {
        return new JsonArrayRequest(makeUrl(params),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        T obj = new Gson().fromJson(response.toString(), outClass);
                        listener.onResponse(obj);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error);
                    }
                }
        );
    }

    protected JsonObjectRequest makePutRequest(final ServiceResponseListener<String> listener,
                                               String urlParam,
                                               JSONObject putData) {
        return new JsonObjectRequestNoExpect(
                Request.Method.PUT,
                makeUrl(urlParam),
                putData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onResponse(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error);
                    }
                }
        );
    }

    private String makeUrl(String urlParam) {
        return makeUrl() + "/" + urlParam;
    }

    protected String makeUrl(Map<String, String> params) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(makeUrl() + GET_EXTENSION);
        if (params != null) {
            int i = 0;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (i++ == 0) {
                    sBuilder.append('?');
                } else {
                    sBuilder.append('&');
                }
                try {
                    sBuilder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                    sBuilder.append("=");
                    sBuilder.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        return sBuilder.toString();
    }

    protected abstract String makeUrl();
}
