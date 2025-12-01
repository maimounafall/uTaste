package com.example.utaste.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utaste.R;
import com.example.utaste.backend.RecipeEntity;
import com.example.utaste.backend.RecipeService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeViewHolder> {

    private final Context context;
    private final List<RecipeEntity> recipes;
    private final RecipeService recipeService;
    private final Runnable onDataChanged;

    private final boolean isWaiterMode;
    private final boolean isSalesReportMode;
    private final boolean isSelectionMode;
    private final boolean isManageRecipesMode;

    private boolean multiSelectEnabled = false;
    private final Set<Long> selectedIds = new HashSet<>();

    private Runnable onSelectionChanged;

    public interface RecipeActionListener {
        void onAddIngredient(long recipeId);
    }

    private RecipeActionListener listener;

    public void setListener(RecipeActionListener l) {
        this.listener = l;
    }

    public RecipeAdapter(
            Context context,
            List<RecipeEntity> recipes,
            RecipeService recipeService,
            Runnable onDataChanged,
            boolean isWaiterMode,
            boolean isSalesReportMode,
            boolean isSelectionMode,
            boolean isManageRecipesMode
    ) {
        this.context = context;
        this.recipes = recipes;
        this.recipeService = recipeService;
        this.onDataChanged = onDataChanged;

        this.isWaiterMode = isWaiterMode;
        this.isSalesReportMode = isSalesReportMode;
        this.isSelectionMode = isSelectionMode;
        this.isManageRecipesMode = isManageRecipesMode;
    }


    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_recipe_card, parent, false);
        return new RecipeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        RecipeEntity recipe = recipes.get(position);

        boolean readOnly = isWaiterMode || isSelectionMode;

        holder.bind(recipe, recipeService, onDataChanged, readOnly, listener);

        // ---- MULTI-SELECT MODE FOR MANAGE RECIPES ----
        if (isManageRecipesMode) {

            if (multiSelectEnabled) {
                // Disable actions
                holder.btnModify.setOnClickListener(null);
                holder.btnAddIngredient.setOnClickListener(null);
                holder.btnNutrition.setOnClickListener(null);
                holder.btnReviews.setOnClickListener(null);

                holder.itemView.setOnClickListener(v -> {
                    long id = recipe.id;

                    if (selectedIds.contains(id)) selectedIds.remove(id);
                    else selectedIds.add(id);

                    notifyItemChanged(holder.getAdapterPosition());

                    if (onSelectionChanged != null)
                        onSelectionChanged.run();
                });

                holder.cardRoot.setBackgroundResource(
                        selectedIds.contains(recipe.id)
                                ? R.drawable.card_selected
                                : R.drawable.offwhite_rounded
                );

            } else {
                holder.cardRoot.setBackgroundResource(R.drawable.offwhite_rounded);
                holder.itemView.setOnClickListener(v -> {});
            }

            return;
        }


        // ---- SELECTION MODE (for RecordSaleActivity) ----
        if (isSelectionMode) {
            holder.btnModify.setVisibility(View.GONE);
            holder.btnAddIngredient.setVisibility(View.GONE);
            holder.btnNutrition.setVisibility(View.GONE);
            holder.btnReviews.setVisibility(View.GONE);
            holder.salesReportBadge.setVisibility(View.GONE);

            holder.itemView.setOnClickListener(v -> {
                Intent data = new Intent();
                data.putExtra("recipeId", recipe.id);
                ((Activity) context).setResult(Activity.RESULT_OK, data);
                ((Activity) context).finish();
            });
            return;
        }


        // ---- WAITER MODE ----
        if (isWaiterMode) {
            holder.btnAddIngredient.setVisibility(View.GONE);
            holder.containerAddIngredient.setVisibility(View.GONE);
            holder.btnReviews.setVisibility(View.GONE);
            holder.salesReportBadge.setVisibility(View.GONE);
        }


        // ---- SALES REPORT MODE ----
        if (isSalesReportMode) {
            holder.btnModify.setVisibility(View.GONE);
            holder.btnAddIngredient.setVisibility(View.GONE);
            holder.btnNutrition.setVisibility(View.GONE);
            holder.containerAddIngredient.setVisibility(View.GONE);
            holder.containerModify.setVisibility(View.GONE);

            holder.btnReviews.setVisibility(View.VISIBLE);
            holder.salesReportBadge.setVisibility(View.VISIBLE);
            holder.tvSalesCount.setText(String.valueOf(recipe.salesCount));
            holder.tvAverageRating.setText(String.format("%.1f", recipe.averageRating));

            if (multiSelectEnabled) {
                holder.itemView.setOnClickListener(v -> {
                    long id = recipe.id;

                    if (selectedIds.contains(id)) selectedIds.remove(id);
                    else selectedIds.add(id);

                    notifyItemChanged(position);

                    if (onSelectionChanged != null)
                        onSelectionChanged.run();
                });

                holder.cardRoot.setBackgroundResource(
                        selectedIds.contains(recipe.id)
                                ? R.drawable.card_selected
                                : R.drawable.offwhite_rounded
                );
            }

            return;
        }
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }


    public void enableMultiSelect(boolean enable) {
        this.multiSelectEnabled = enable;
        selectedIds.clear();
        notifyDataSetChanged();
    }

    public boolean isMultiSelectEnabled() {
        return multiSelectEnabled;
    }

    public Set<Long> getSelectedIds() {
        return selectedIds;
    }

    public void setOnSelectionChanged(Runnable r) {
        this.onSelectionChanged = r;
    }
}
