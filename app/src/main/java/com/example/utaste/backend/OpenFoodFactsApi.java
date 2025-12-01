package com.example.utaste.backend;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface OpenFoodFactsApi {

    @GET("api/v0/product/{barcode}.json")
    Call<OpenFoodFactsResponse> getProduct(@Path("barcode") String barcode);
}
