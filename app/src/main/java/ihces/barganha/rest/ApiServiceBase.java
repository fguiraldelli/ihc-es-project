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

    protected abstract String makeUrl();
}
