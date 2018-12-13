package com.evartem.remsimon.data.types.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Retrofit interface to receive any JSON response (not an object of a concrete class)
 */
public interface GeneralApi {
    @GET
    Call<ResponseBody> getHttpData(@Url String url);
}
