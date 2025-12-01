package com.example.utaste;

import com.example.utaste.InMemoryDbFactory;
import com.example.utaste.backend.AppDatabase;
import com.example.utaste.backend.Ingredient;
import com.example.utaste.backend.NutritionInfo;
import com.example.utaste.backend.RecipeIngredientXRef;
import com.example.utaste.backend.RecipeService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(org.robolectric.RobolectricTestRunner.class)
@Config(sdk = 33)
public class RecipeServiceTest {

    private AppDatabase db;
    private RecipeService service;

    @Before
    public void setup() {
        db = InMemoryDbFactory.create();
        service = new RecipeService(db, new FakeNutritionProvider());
    }

    // 1. Create recipe
    @Test
    public void createRecipe_shouldInsert() {
        long id = service.createRecipe("Salade", "", "Fresh", 1L);
        assertTrue(id > 0);
    }

    // 2. Duplicate recipe names not allowed
    @Test(expected = IllegalStateException.class)
    public void createRecipe_duplicateName_shouldFail() {
        service.createRecipe("Salade", "", "", 1L);
        service.createRecipe("Salade", "", "", 1L);
    }

    // 3. Add ingredient to recipe using barcode
    @Test
    public void addIngredientFromBarcode_shouldWork() {
        long recipeId = service.createRecipe("Soup", "", "", 1);

        Ingredient ing = new Ingredient();
        ing.name = "Tomato";
        ing.barcode = "TOMA";
        ing.quantity = 500; // stock
        ing.unit = "g";
        long ingredientId = db.ingredientDao().insert(ing);

        service.addIngredientFromBarcode(recipeId, "TOMA", 50f, 200f);

        List<RecipeIngredientXRef> list = service.getIngredientsForRecipe(recipeId);
        assertEquals(1, list.size());
        assertEquals(50f, list.get(0).quantityPercent, 0.1f);
    }

    // 4. Adding ingredient with unknown barcode fails
    @Test(expected = IllegalArgumentException.class)
    public void addIngredient_unknownBarcode_shouldFail() {
        long recipeId = service.createRecipe("Test", "", "", 1);
        service.addIngredientFromBarcode(recipeId, "UNKNOWN", 50f, 200f);
    }

    // 5. Invalid percent fails
    @Test(expected = IllegalArgumentException.class)
    public void addIngredient_invalidPercent_shouldFail() {
        long recipeId = service.createRecipe("Test", "", "", 1);

        Ingredient ing = new Ingredient();
        ing.name = "Carrot";
        ing.barcode = "CAR";
        ing.quantity = 100;
        ing.unit = "g";
        db.ingredientDao().insert(ing);

        service.addIngredientFromBarcode(recipeId, "CAR", 0f, 100f);
    }

    // 6. Stock is decremented after adding ingredient
    @Test
    public void addIngredient_shouldDecreaseStock() {
        long recipeId = service.createRecipe("Pasta", "", "", 1);

        Ingredient ing = new Ingredient();
        ing.name = "Onion";
        ing.barcode = "ONI";
        ing.quantity = 100f;
        ing.unit = "g";
        long id = db.ingredientDao().insert(ing);

        service.addIngredientFromBarcode(recipeId, "ONI", 50f, 100f); // uses 50g

        Ingredient updated = db.ingredientDao().findById(id);
        assertEquals(50f, updated.quantity, 0.1f);
    }

    // 7. getIngredientNutrition returns Fake provider values
    @Test
    public void getIngredientNutrition_shouldReturnFakeValues() {
        NutritionInfo info = service.getIngredientNutrition("ANY");

        assertNotNull(info);
        assertEquals(100f, info.energyKcalPer100g, 0.1f);
        assertEquals(10f, info.carbsPer100g, 0.1f);
    }

    // 8. computeRecipeNutrition with 1 ingredient
    @Test
    public void computeRecipeNutrition_singleIngredient() {
        long recipeId = service.createRecipe("Dish", "", "", 1);

        Ingredient ing = new Ingredient();
        ing.name = "Apple";
        ing.barcode = "APP";
        ing.quantity = 200;
        ing.unit = "g";
        long id = db.ingredientDao().insert(ing);

        service.addIngredientFromBarcode(recipeId, "APP", 100f, 100f);

        NutritionInfo info = service.computeRecipeNutrition(recipeId);

        assertNotNull(info);
        assertEquals(100f, info.energyKcalPer100g, 0.1f);
    }

    // 9. computeRecipeNutrition with 2 ingredients & weighted average
    @Test
    public void computeRecipeNutrition_twoIngredients() {
        long recipeId = service.createRecipe("Mix", "", "", 1);

        Ingredient a = new Ingredient();
        a.name = "A";
        a.barcode = "A";
        a.quantity = 200;
        a.unit = "g";
        db.ingredientDao().insert(a);

        Ingredient b = new Ingredient();
        b.name = "B";
        b.barcode = "B";
        b.quantity = 200;
        b.unit = "g";
        db.ingredientDao().insert(b);

        service.addIngredientFromBarcode(recipeId, "A", 50f, 100f);
        service.addIngredientFromBarcode(recipeId, "B", 50f, 100f);

        NutritionInfo info = service.computeRecipeNutrition(recipeId);

        assertEquals(100f, info.energyKcalPer100g, 0.1f); // because both A & B = 100f
    }

    // 10. computeRecipeNutrition returns null if recipe empty
    @Test
    public void computeRecipeNutrition_emptyRecipe_returnsNull() {
        long recipeId = service.createRecipe("Empty", "", "", 1);

        NutritionInfo info = service.computeRecipeNutrition(recipeId);
        assertNull(info);
    }
}
