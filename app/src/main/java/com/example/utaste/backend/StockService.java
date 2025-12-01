package com.example.utaste.backend;

import com.example.utaste.backend.IngredientDao;
import com.example.utaste.backend.Ingredient;

import java.util.List;

public class StockService {

    private final IngredientDao ingredientDao;

    public StockService(AppDatabase db) {
        this.ingredientDao = db.ingredientDao();
    }

    public long addIngredientToStock(String name, String barcode, float quantity, String unit) {
        Ingredient ing = new Ingredient();
        ing.name = name;
        ing.barcode = barcode;
        ing.quantity = quantity;
        ing.unit = unit;
        return ingredientDao.insert(ing);
    }

    public List<Ingredient> listStock() {
        return ingredientDao.findAll();
    }

    public void updateQuantity(long ingredientId, float newQuantity) {
        Ingredient ing = ingredientDao.findById(ingredientId);
        if (ing != null) {
            ing.quantity = newQuantity;
            ingredientDao.update(ing);
        }
    }
    public Ingredient findByName(String name) {
        List<Ingredient> all = ingredientDao.findAll();
        for (Ingredient ing : all) {
            if (ing.name.equalsIgnoreCase(name.trim())) {
                return ing;
            }
        }
        return null;
    }
    public void deleteIngredient(long ingredientId) {
        ingredientDao.deleteById(ingredientId);
    }
}
