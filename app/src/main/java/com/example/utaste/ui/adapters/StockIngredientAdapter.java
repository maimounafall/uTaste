package com.example.utaste.ui.adapters;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.example.utaste.backend.StockService;

import java.util.List;

public class StockIngredientAdapter extends RecyclerView.Adapter<StockIngredientAdapter.ViewHolder> {

    private final List<Ingredient> list;
    private final boolean selectMode;
    private final OnSelectListener onSelect;
    private final OnNutritionListener onNutrition;

    private final Activity activity;
    private final StockService stockService;

    public interface OnSelectListener { void onSelect(Ingredient ing); }
    public interface OnNutritionListener { void onNutrition(Ingredient ing); }

    public StockIngredientAdapter(
            List<Ingredient> list,
            boolean selectMode,
            OnSelectListener onSelect,
            OnNutritionListener onNutrition,
            Activity activity
    ) {
        this.list = list;
        this.selectMode = selectMode;
        this.onSelect = onSelect;
        this.onNutrition = onNutrition;
        this.activity = activity;

        AppDatabase db = DatabaseProvider.get(activity);
        this.stockService = new StockService(db);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Ingredient ing = list.get(pos);

        h.tvName.setText(ing.name);
        String unit = (ing.unit != null && !ing.unit.isEmpty()) ? ing.unit : "g";
        h.tvQuantity.setText(ing.quantity + " " + unit);
        // --- Nutrition ---
        h.btnNutrition.setOnClickListener(v -> onNutrition.onNutrition(ing));

        // --- MODE SELECTION ---
        if (selectMode) {
            h.btnModify.setVisibility(View.GONE);
            h.btnDelete.setVisibility(View.GONE);
            h.itemView.setOnClickListener(v -> onSelect.onSelect(ing));
            return;
        }

        // --- MODE NORMAL ---
        h.btnModify.setVisibility(View.VISIBLE);
        h.btnDelete.setVisibility(View.VISIBLE);

        // Modify
        h.btnModify.setOnClickListener(v -> openModifyDialog(ing, pos));

        // Delete
        h.btnDelete.setOnClickListener(v -> openDeleteDialog(ing, pos));
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

    // ---------- MODIFY ----------
    private void openModifyDialog(Ingredient ing, int pos) {
        View dv = activity.getLayoutInflater().inflate(R.layout.dialog_modify_ingredient, null);
        AlertDialog dialog = new AlertDialog.Builder(activity, R.style.CustomDialog)
                .setView(dv)
                .create();

        TextView tvName = dv.findViewById(R.id.tvNumberOfSales);
        EditText etQty = dv.findViewById(R.id.etIngredientQuantity);
        Button btnSave = dv.findViewById(R.id.btnSave);
        Button btnCancel = dv.findViewById(R.id.btnCancel);

        tvName.setText(ing.name);
        etQty.setText(String.valueOf(ing.quantity));

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            float newQty;

            try { newQty = Float.parseFloat(etQty.getText().toString()); }
            catch (Exception e) {
                Toast.makeText(activity, "Invalid quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                stockService.updateQuantity(ing.id, newQty);
                ing.quantity = newQty;

                activity.runOnUiThread(() -> {
                    notifyItemChanged(pos);
                    Toast.makeText(activity, "Quantity updated", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            }).start();
        });

        dialog.show();
    }

    // ---------- DELETE ----------
    private void openDeleteDialog(Ingredient ing, int pos) {
        View dv = activity.getLayoutInflater().inflate(R.layout.dialog_confirm_delete, null);
        AlertDialog dialog = new AlertDialog.Builder(activity, R.style.CustomDialog)
                .setView(dv)
                .create();

        TextView tvTitle = dv.findViewById(R.id.tvConfirmTitle);
        TextView tvMsg = dv.findViewById(R.id.tvConfirmMessage);
        Button btnCancel = dv.findViewById(R.id.btnCancel);
        Button btnConfirm = dv.findViewById(R.id.btnConfirm);

        tvTitle.setText("Delete Ingredient");
        tvMsg.setText("Remove this item from stock?");

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            new Thread(() -> {
                stockService.deleteIngredient(ing.id);

                activity.runOnUiThread(() -> {
                    list.remove(pos);
                    notifyItemRemoved(pos);
                    Toast.makeText(activity, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            }).start();
        });

        dialog.show();
    }
}
