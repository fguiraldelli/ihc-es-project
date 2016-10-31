package ihces.barganha.rest;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonObjectRequestNoExpect extends JsonObjectRequest {
    public JsonObjectRequestNoExpect(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public JsonObjectRequestNoExpect(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        if (response.statusCode == 200) {
            JSONObject jsResp = new JSONObject();
            try {
                jsResp.put("statusCode", response.statusCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return Response.success(jsResp, HttpHeaderParser.parseCacheHeaders(response));
        } else {
            return Response.error(new ParseError(new Exception("Result NOT 200 OK!")));
        }
    }
}
