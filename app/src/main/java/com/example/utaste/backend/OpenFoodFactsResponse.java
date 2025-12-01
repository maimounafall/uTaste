package com.example.utaste.backend;

import com.google.gson.annotations.SerializedName;

public class OpenFoodFactsResponse {

    @SerializedName("status")
    public int status;

    @SerializedName("product")
    public OffProduct product;
}
