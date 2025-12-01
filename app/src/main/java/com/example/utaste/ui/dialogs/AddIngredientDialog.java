package com.example.utaste.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.utaste.R;
import com.example.utaste.StockActivity;
import com.example.utaste.backend.AppDatabase;
import com.example.utaste.backend.DatabaseProvider;
import com.example.utaste.backend.Ingredient;
import com.example.utaste.backend.RecipeService;
import com.example.utaste.backend.StockService;
import com.example.utaste.backend.UTasteApplication;

public class AddIngredientDialog {

    public static final int MODE_STOCK = 0;
    public static final int MODE_RECIPE = 1;

    private static final int REQUEST_SELECT_INGREDIENT = 4001;

    private final Context context;
    private final int mode;
    private final long recipeId;

    private AlertDialog dialog;
    private Runnable onIngredientAdded;

    private final StockService stockService;
    private final RecipeService recipeService;
    private final AppDatabase db;

    private String pendingBarcode = null;
    private long selectedIngredientId = -1;

    public AddIngredientDialog(Context context, int mode) {
        this(context, mode, -1);
    }

    public AddIngredientDialog(Context context, int mode, long recipeId) {
        this.context = context;
        this.mode = mode;
        this.recipeId = recipeId;

        // Correct global database
        this.db = UTasteApplication.getInstance().getDatabase();
        this.stockService = new StockService(db);
        this.recipeService = new RecipeService(db);
    }

    public void setOnIngredientAdded(Runnable callback) {
        this.onIngredientAdded = callback;
    }

    public void showWithBarcode(String barcode) {
        pendingBarcode = barcode;
        show();
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (dialog == null) return;

        if (requestCode == REQUEST_SELECT_INGREDIENT &&
                resultCode == Activity.RESULT_OK &&
                data != null) {

            String name = data.getStringExtra("ingredientName");
            long id = data.getLongExtra("ingredientId", -1);

            selectedIngredientId = id;

            EditText etName = dialog.findViewById(R.id.etIngredientName);
            if (name != null) etName.setText(name);
        }
    }

    public void show() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_add_ingredient, null);
        dialog = new AlertDialog.Builder(context, R.style.CustomDialog).setView(view).create();

        EditText etName = view.findViewById(R.id.etIngredientName);
        EditText etQty = view.findViewById(R.id.etIngredientQuantity);
        EditText etUnit = view.findViewById(R.id.etIngredientUnit);
        TextView tvBarcode = view.findViewById(R.id.tvBarcodeValue);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnSave = view.findViewById(R.id.btnSave);

        if (pendingBarcode != null) tvBarcode.setText(pendingBarcode);

        if (mode == MODE_STOCK) {
            tvBarcode.setVisibility(View.VISIBLE);
            etUnit.setVisibility(View.VISIBLE);
            etQty.setHint("Quantity in stock");
            etName.setFocusable(true);

        } else { // MODE_RECIPE
            tvBarcode.setVisibility(View.GONE);
            etUnit.setVisibility(View.GONE);
            etQty.setHint("Quantity (%)");

            etName.setFocusable(false);
            etName.setOnClickListener(v -> {
                Intent i = new Intent(context, StockActivity.class);
                i.putExtra("selectMode", true);
                ((Activity) context).startActivityForResult(i, REQUEST_SELECT_INGREDIENT);
            });
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String qStr = etQty.getText().toString().trim();

            if (name.isEmpty() || qStr.isEmpty()) {
                Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // STOCK MODE
            if (mode == MODE_STOCK) {
                String barcode = tvBarcode.getText().toString().trim();
                if (barcode.isEmpty()) {
                    Toast.makeText(context, "Barcode required", Toast.LENGTH_SHORT).show();
                    return;
                }

                float qty;
                try { qty = Float.parseFloat(qStr); }
                catch (Exception e) {
                    Toast.makeText(context, "Invalid quantity", Toast.LENGTH_SHORT).show();
                    return;
                }

                String unit = etUnit.getText().toString().trim();
                if (unit.isEmpty()) unit = "g";

                float finalQty = qty;
                String finalUnit = unit;

                new Thread(() -> {
                    Log.d("DB_CHECK", "Dialog DB: " + db);
                    stockService.addIngredientToStock(name, barcode, finalQty, finalUnit);
                    runUi(() -> {
                        Toast.makeText(context, "Ingredient saved!", Toast.LENGTH_SHORT).show();
                        if (onIngredientAdded != null) onIngredientAdded.run();
                        dialog.dismiss();
                    });
                }).start();
            }

            // RECIPE MODE
            else {
                Log.d("DB_CHECK", "Dialog DB first: " + db);
                if (selectedIngredientId == -1) {
                    Toast.makeText(context, "Select an ingredient first", Toast.LENGTH_SHORT).show();
                    return;
                }

                float percent;
                try { percent = Float.parseFloat(qStr); }
                catch (Exception e) {
                    Toast.makeText(context, "Invalid percent", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (percent <= 0 || percent > 100) {
                    Toast.makeText(context, "Percent must be 1–100", Toast.LENGTH_SHORT).show();
                    return;
                }

                long ingId = selectedIngredientId;

                new Thread(() -> {

                    Log.d("DB_CHECK", "Dialog DB: " + db);
                    recipeService.addIngredientToRecipe(recipeId, ingId, percent);

                    runUi(() -> {
                        Toast.makeText(context, "Ingredient added!", Toast.LENGTH_SHORT).show();
                        if (onIngredientAdded != null) onIngredientAdded.run();
                        dialog.dismiss();
                    });
                }).start();
            }
        });

        dialog.show();
    }

    private void runUi(Runnable r) {
        ((Activity) context).runOnUiThread(r);
    }
}
