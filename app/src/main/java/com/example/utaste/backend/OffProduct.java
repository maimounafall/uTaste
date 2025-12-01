package com.example.utaste.backend;

import com.google.gson.annotations.SerializedName;

public class OffProduct {

    @SerializedName("product_name")
    public String productName;

    @SerializedName("nutriments")
    public OffNutriments nutriments;
}
