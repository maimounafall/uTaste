package com.example.utaste.backend;

import com.example.utaste.backend.NutritionDao;
import com.example.utaste.backend.NutritionEntity;
import com.example.utaste.backend.NutritionInfo;
import com.example.utaste.backend.OpenFoodFactsApi;
import com.example.utaste.backend.OpenFoodFactsResponse;
import com.example.utaste.backend.OffNutriments;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Sert à récupérer les infos nutritionnelles d'un barcode
 * en utilisant d'abord le cache SQLite, puis OpenFoodFacts.
 */
public class NutritionRepository implements NutritionProvider {

    private final NutritionDao nutritionDao;
    private final OpenFoodFactsApi api;

    public NutritionRepository(NutritionDao nutritionDao) {
        this.nutritionDao = nutritionDao;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://world.openfoodfacts.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.api = retrofit.create(OpenFoodFactsApi.class);
    }

    @Override
    public NutritionInfo getNutritionByBarcode(String barcode) {
        // 1. vérifier en local
        System.out.println("Before fetching nutrition for barcode = " + barcode + "");
        NutritionEntity cached = nutritionDao.findByBarcode(barcode);
        System.out.println("Find by barcode for barcode = " + barcode);
        if (cached != null) {
            return toModel(cached);
        }

        // 2. sinon appeler OpenFoodFacts
        try {
            Response<OpenFoodFactsResponse> resp = api.getProduct(barcode).execute();
            System.out.println("Opening OpenFoodFacts for barcode = " + barcode + "");
            if (!resp.isSuccessful()) {
                return null;
            }
            OpenFoodFactsResponse body = resp.body();
            System.out.println("After opening OpenFoodFacts for barcode = " + barcode + "");
            if (body == null || body.status == 0 || body.product == null || body.product.nutriments == null) {
                return null;
            }

            NutritionInfo info = mapToNutritionInfo(body.product.nutriments);
            System.out.println("getting Map to Nutrition for barcode = " + barcode);

            // 3. sauvegarder
            NutritionEntity entity = toEntity(barcode, info);
            nutritionDao.insert(entity);
            System.out.println("Nutrition fetched for barcode = " + barcode);

            return info;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private NutritionInfo mapToNutritionInfo(OffNutriments n) {
        NutritionInfo info = new NutritionInfo();
        info.energyKcalPer100g = n.energyKcal100g;
        info.carbsPer100g = n.carbohydrates100g;
        info.proteinsPer100g = n.proteins100g;
        info.fatPer100g = n.fat100g;
        info.saltPer100g = n.salt100g;
        info.fibersPer100g = n.fiber100g;
        return info;
    }

    private NutritionInfo toModel(NutritionEntity e) {
        NutritionInfo info = new NutritionInfo();
        info.energyKcalPer100g = e.energyKcalPer100g;
        info.carbsPer100g = e.carbsPer100g;
        info.proteinsPer100g = e.proteinsPer100g;
        info.fatPer100g = e.fatPer100g;
        info.saltPer100g = e.saltPer100g;
        info.fibersPer100g = e.fibersPer100g;
        return info;
    }

    private NutritionEntity toEntity(String barcode, NutritionInfo info) {
        NutritionEntity e = new NutritionEntity();
        e.barcode = barcode;
        e.energyKcalPer100g = info.energyKcalPer100g;
        e.carbsPer100g = info.carbsPer100g;
        e.proteinsPer100g = info.proteinsPer100g;
        e.fatPer100g = info.fatPer100g;
        e.saltPer100g = info.saltPer100g;
        e.fibersPer100g = info.fibersPer100g;
        e.fetchedAt = System.currentTimeMillis();
        return e;
    }
}
