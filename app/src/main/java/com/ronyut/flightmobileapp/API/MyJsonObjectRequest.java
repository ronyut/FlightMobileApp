package com.ronyut.flightmobileapp.API;

import androidx.annotation.Nullable;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import java.io.UnsupportedEncodingException;
import org.json.JSONException;
import org.json.JSONObject;

public class MyJsonObjectRequest extends JsonRequest<JSONObject> {

    public MyJsonObjectRequest(
            int method,
            String url,
            @Nullable JSONObject jsonRequest,
            Response.Listener<JSONObject> listener,
            @Nullable Response.ErrorListener errorListener) {
        super(
                method,
                url,
                (jsonRequest == null) ? null : jsonRequest.toString(),
                listener,
                errorListener);
    }

    public MyJsonObjectRequest(
            String url,
            @Nullable JSONObject jsonRequest,
            Response.Listener<JSONObject> listener,
            @Nullable Response.ErrorListener errorListener) {
        this(
                jsonRequest == null ? Method.GET : Method.POST,
                url,
                jsonRequest,
                listener,
                errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));

            JSONObject result = null;

            if (jsonString != null && jsonString.length() > 0)
                result = new JSONObject(jsonString);

            return Response.success(result,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException | JSONException e) {
            return Response.error(new ParseError(e));
        }
    }
}
