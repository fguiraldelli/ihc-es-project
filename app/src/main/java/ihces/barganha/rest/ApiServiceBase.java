package ihces.barganha.rest;

import android.content.Context;
import android.support.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public abstract class ApiServiceBase {

    public static final String BASE_URL = "http://54.145.242.159:1234/";
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
    protected StringRequest makePostRequest(final ServiceResponseListener<String> listener,
                                            final String postData) {
        return new StringRequest(Request.Method.POST,
                    makeUrl(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            listener.onResponse(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            listener.onError(error);
                        }
                    }
            ) {
                @Override
                public byte[] getBody() throws AuthFailureError {
                    return postData.getBytes();
                }
            };
    }

    @NonNull
    protected <T> GsonRequest<T> makePostRetrieveRequest(Class<T> outClass,
            final ServiceResponseListener<T> listener,
            final String outJson) {

        return new GsonRequest<T>(makeUrl(),
                outClass,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        listener.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError(error);
                    }
                }
        ) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return outJson.getBytes();
            }
        };
    }

    @NonNull
    protected <T> GsonRequest<T> makeGetRequest(Class<T> outClass,
                                                final ServiceResponseListener<T> listener,
                                                Map<String, String> params) {
        return new GsonRequest<>(makeUrl(params),
                outClass,
                new Response.Listener<T>() {
                    @Override
                    public void onResponse(T response) {
                        listener.onResponse(response);
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

    protected String makeUrl(Map<String, String> params) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(makeUrl());
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
