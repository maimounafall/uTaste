package com.example.utaste.ui.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utaste.R;
import com.example.utaste.backend.AppDatabase;
import com.example.utaste.backend.DatabaseProvider;
import com.example.utaste.backend.Ingredient;
import com.example.utaste.backend.NutritionInfo;
import com.example.utaste.backend.NutritionRepository;
import com.example.utaste.backend.RecipeIngredientXRef;
import com.example.utaste.backend.RecipeService;
import com.example.utaste.ui.dialogs.NutritionDialog;

import java.util.List;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.ViewHolder> {

    private final Context context;
    private final List<RecipeIngredientXRef> list;
    private final long recipeId;
    private final RecipeService recipeService;
    private final NutritionRepository nutritionRepository;
    private final boolean readOnly;

    public IngredientAdapter(Context ctx,
                             List<RecipeIngredientXRef> ingredients,
                             long recipeId,
                             boolean readOnly) {
        this.context = ctx;
        this.list = ingredients;
        this.recipeId = recipeId;
        this.readOnly = readOnly;

        AppDatabase db = DatabaseProvider.get(ctx);
        this.recipeService = new RecipeService(db);
        this.nutritionRepository = new NutritionRepository(db.nutritionDao());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_ingredient_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        RecipeIngredientXRef link = list.get(position);

        new Thread(() -> {
            Ingredient ing = recipeService.getIngredientById(link.ingredientId);

            ((Activity) context).runOnUiThread(() -> {
                if (ing == null) {
                    h.tvName.setText("Unknown");
                    return;
                }

                h.tvName.setText(ing.name);
                h.tvQuantity.setText(String.format("%.1f%%", link.quantityPercent));

                // Nutrition always allowed
                h.btnNutrition.setOnClickListener(v -> showNutrition(ing));

                if (readOnly) {
                    // Waiter: cannot modify/delete
                    h.btnModify.setVisibility(View.GONE);
                    h.btnDelete.setVisibility(View.GONE);
                } else {
                    // Chef: full edit
                    h.btnModify.setVisibility(View.VISIBLE);
                    h.btnDelete.setVisibility(View.VISIBLE);

                    h.btnModify.setOnClickListener(v -> modifyIngredient(link, position));
                    h.btnDelete.setOnClickListener(v -> deleteIngredient(link, position));
                }
            });
        }).start();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity;
        Button btnNutrition, btnModify, btnDelete;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvNumberOfSales);
            tvQuantity = v.findViewById(R.id.tvQuantity);
            btnNutrition = v.findViewById(R.id.btnNutrition);
            btnModify = v.findViewById(R.id.btnModifyIngredient);
            btnDelete = v.findViewById(R.id.btnDeleteIngredient);
        }
    }

    private void showNutrition(Ingredient ing) {
        new Thread(() -> {
            NutritionInfo info = nutritionRepository.getNutritionByBarcode(ing.barcode);
            ((Activity) context).runOnUiThread(() -> {
                if (info == null) {
                    Toast.makeText(context, "No nutrition data", Toast.LENGTH_SHORT).show();
                } else {
                    NutritionDialog.show(context, ing.name, info);
                }
            });
        }).start();
    }

    private void modifyIngredient(RecipeIngredientXRef link, int pos) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_modify_ingredient, null);
        AlertDialog dlg = new AlertDialog.Builder(context, R.style.CustomDialog).setView(view).create();

        TextView name = view.findViewById(R.id.tvNumberOfSales);
        EditText qty = view.findViewById(R.id.etIngredientQuantity);
        Button save = view.findViewById(R.id.btnSave);
        Button cancel = view.findViewById(R.id.btnCancel);

        name.setText("Ingredient ID: " + link.ingredientId);
        qty.setText(String.valueOf(link.quantityPercent));

        cancel.setOnClickListener(v -> dlg.dismiss());

        save.setOnClickListener(v -> {
            float newP = Float.parseFloat(qty.getText().toString());
            new Thread(() -> {
                recipeService.changeIngredientPercent(recipeId, link.ingredientId, newP);
                link.quantityPercent = newP;

                ((Activity) context).runOnUiThread(() -> {
                    notifyItemChanged(pos);
                    dlg.dismiss();
                });
            }).start();
        });

        dlg.show();
    }

    private void deleteIngredient(RecipeIngredientXRef link, int pos) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_delete, null);
        AlertDialog dlg = new AlertDialog.Builder(context, R.style.CustomDialog).setView(view).create();
        TextView tvTitle = view.findViewById(R.id.tvConfirmTitle);
        TextView tvMsg = view.findViewById(R.id.tvConfirmMessage);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);

        tvTitle.setText("Delete Ingredient");
        tvMsg.setText("Remove this item from recipe?");
        btnConfirm.setText("Delete");

        btnCancel.setOnClickListener(v -> dlg.dismiss());
        btnConfirm.setOnClickListener(v -> {
            new Thread(() -> {
                recipeService.removeIngredient(recipeId, link.ingredientId);
                list.remove(pos);

                ((Activity) context).runOnUiThread(() -> {
                    notifyItemRemoved(pos);
                    dlg.dismiss();
                });
            }).start();
        });

        dlg.show();
    }
}
