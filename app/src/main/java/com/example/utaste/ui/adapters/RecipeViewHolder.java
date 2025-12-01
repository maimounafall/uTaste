package com.example.utaste.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.utaste.R;
import com.example.utaste.backend.NutritionInfo;
import com.example.utaste.backend.RecipeEntity;
import com.example.utaste.backend.RecipeService;
import com.example.utaste.ui.dialogs.ModifyRecipeDialog;
import com.example.utaste.ui.dialogs.NutritionDialog;
import com.example.utaste.ui.dialogs.SalesReviewDialog;

public class RecipeViewHolder extends RecyclerView.ViewHolder {

    ImageView imgRecipe;
    TextView tvRecipeName;

    View btnModify, btnAddIngredient, btnNutrition, btnReviews;
    View containerModify, containerAddIngredient;
    View salesReportBadge;
    TextView tvSalesCount, tvAverageRating;
    View cardRoot;

    public RecipeViewHolder(@NonNull View v) {
        super(v);

        cardRoot = v.findViewById(R.id.cardRoot);
        imgRecipe = v.findViewById(R.id.imgRecipe);
        tvRecipeName = v.findViewById(R.id.tvRecipeName);

        btnModify = v.findViewById(R.id.btnModify);
        btnAddIngredient = v.findViewById(R.id.btnAddIngredient);
        btnNutrition = v.findViewById(R.id.btnNutrition);
        btnReviews = v.findViewById(R.id.btnReviews);

        containerAddIngredient = v.findViewById(R.id.containerAddIngredient);
        containerModify = v.findViewById(R.id.containerModify);

        salesReportBadge = v.findViewById(R.id.salesReportBadge);
        tvSalesCount = v.findViewById(R.id.tvSalesCount);
        tvAverageRating = v.findViewById(R.id.tvAverageRating);
    }


    public void bind(
            RecipeEntity recipe,
            RecipeService recipeService,
            Runnable onDataChanged,
            boolean readOnly,
            RecipeAdapter.RecipeActionListener listener
    ) {
        Context context = itemView.getContext();

        tvRecipeName.setText(recipe.name);

        try {
            int resId = Integer.parseInt(recipe.imagePath);
            imgRecipe.setImageResource(resId);
        } catch (Exception e) {
            Glide.with(context)
                    .load(recipe.imagePath)
                    .placeholder(R.drawable.ic_add_photo)
                    .into(imgRecipe);
        }

        btnModify.setOnClickListener(v -> {
            ModifyRecipeDialog dlg = new ModifyRecipeDialog(context, recipe, readOnly);
            dlg.show();
        });

        btnAddIngredient.setOnClickListener(v -> {
            if (listener != null)
                listener.onAddIngredient(recipe.id);
        });

        btnNutrition.setOnClickListener(v -> {
            new Thread(() -> {
                NutritionInfo info = recipeService.computeRecipeNutrition(recipe.id);
                ((Activity) context).runOnUiThread(() -> {
                    if (info == null) {
                        Toast.makeText(context, "No nutrition data available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    NutritionDialog.show(context, recipe.name, info);
                });
            }).start();
        });

        btnReviews.setOnClickListener(v ->
                SalesReviewDialog.show(context, recipe.id)
        );
    }
}
