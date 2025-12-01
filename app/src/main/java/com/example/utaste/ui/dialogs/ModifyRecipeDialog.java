package com.example.utaste.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.utaste.R;
import com.example.utaste.backend.AppDatabase;
import com.example.utaste.backend.DatabaseProvider;
import com.example.utaste.backend.RecipeEntity;
import com.example.utaste.backend.RecipeIngredientXRef;
import com.example.utaste.backend.RecipeService;
import com.example.utaste.ui.adapters.IngredientAdapter;

import java.util.List;

public class ModifyRecipeDialog {

    private final Context context;
    private final RecipeEntity recipe;
    private final RecipeService recipeService;
    private final boolean readOnly;   // <- waiter = true, chef = false

    private AlertDialog dialog;
    private IngredientAdapter ingredientAdapter;

    // Default: editable (chef)
    public ModifyRecipeDialog(Context context, RecipeEntity recipe) {
        this(context, recipe, false);
    }

    // Read-only flag passed from outside
    public ModifyRecipeDialog(Context context, RecipeEntity recipe, boolean readOnly) {
        this.context = context;
        this.recipe = recipe;
        this.readOnly = readOnly;

        AppDatabase db = DatabaseProvider.get(context);
        this.recipeService = new RecipeService(db);
    }

    public void show() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_modify_recipe, null);
        dialog = new AlertDialog.Builder(context, R.style.CustomDialog).setView(view).create();

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        EditText etRecipeName = view.findViewById(R.id.etRecipeName);
        EditText etRecipeDescription = view.findViewById(R.id.etRecipeDescription);
        ImageView imgRecipePreview = view.findViewById(R.id.imgRecipePreview);
        RecyclerView recyclerIngredients = view.findViewById(R.id.recyclerIngredients);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSave = view.findViewById(R.id.btnSave);

        // Fill info
        etRecipeName.setText(recipe.name);
        etRecipeDescription.setText(recipe.description);

        try {
            int resId = Integer.parseInt(recipe.imagePath);
            imgRecipePreview.setImageResource(resId);
        } catch (Exception e) {
            Glide.with(context)
                    .load(recipe.imagePath)
                    .placeholder(R.drawable.ic_add_photo)
                    .into(imgRecipePreview);
        }

        // Ingredients list
        recyclerIngredients.setLayoutManager(new LinearLayoutManager(context));

        new Thread(() -> {
            List<RecipeIngredientXRef> ingredients =
                    recipeService.getIngredientsForRecipe(recipe.id);

            ((Activity) context).runOnUiThread(() -> {
                ingredientAdapter = new IngredientAdapter(context, ingredients, recipe.id, readOnly);
                recyclerIngredients.setAdapter(ingredientAdapter);
            });
        }).start();

        // Common buttons
        btnBack.setOnClickListener(v -> dialog.dismiss());
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // READ-ONLY MODE (waiter)
        if (readOnly) {
            etRecipeName.setEnabled(false);
            etRecipeName.setFocusable(false);
            etRecipeName.setFocusableInTouchMode(false);

            etRecipeDescription.setEnabled(false);
            etRecipeDescription.setFocusable(false);
            etRecipeDescription.setFocusableInTouchMode(false);

            btnSave.setVisibility(View.GONE);
        }
        // EDIT MODE (chef)
        else {
            btnSave.setOnClickListener(v -> {
                String newName = etRecipeName.getText().toString().trim();
                String newDesc = etRecipeDescription.getText().toString().trim();

                if (newName.isEmpty()) {
                    Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                new Thread(() -> {
                    try {
                        recipeService.updateRecipe(recipe.id, newName, recipe.imagePath, newDesc);
                        ((Activity) context).runOnUiThread(() -> {
                            Toast.makeText(context, "Recipe updated successfully!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
                    } catch (Exception e) {
                        ((Activity) context).runOnUiThread(() ->
                                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                        );
                    }
                }).start();
            });
        }

        // Optional dialog styling (keep if you like)
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(android.view.Gravity.CENTER);
            window.setWindowAnimations(R.style.DialogFadeAnimation);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.45f;
            window.setAttributes(lp);
        }

        dialog.show();
    }
}
