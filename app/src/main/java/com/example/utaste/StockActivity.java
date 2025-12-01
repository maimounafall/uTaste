package com.example.utaste;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utaste.backend.AppDatabase;
import com.example.utaste.backend.Ingredient;
import com.example.utaste.backend.NutritionInfo;
import com.example.utaste.backend.NutritionRepository;
import com.example.utaste.backend.StockService;
import com.example.utaste.backend.UTasteApplication;
import com.example.utaste.ui.adapters.StockIngredientAdapter;
import com.example.utaste.ui.dialogs.AddIngredientDialog;

import java.util.ArrayList;
import java.util.List;

public class StockActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StockIngredientAdapter adapter;
    private final List<Ingredient> allIngredients = new ArrayList<>();
    private final List<Ingredient> filteredIngredients = new ArrayList<>();
    private EditText editSearch;
    private ImageButton btnBack;
    private Button btnScanBarcode;
    private ImageView iconBarcode;

    private AppDatabase db;
    private StockService stockService;
    private NutritionRepository nutritionRepository;

    private boolean selectMode = false;

    private static final int REQ_SCAN = 6001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        selectMode = getIntent().getBooleanExtra("selectMode", false);

        db = UTasteApplication.getInstance().getDatabase();
        stockService = new StockService(db);
        nutritionRepository = new NutritionRepository(db.nutritionDao());

        recyclerView = findViewById(R.id.recyclerStock);
        editSearch = findViewById(R.id.editSearch);
        btnBack = findViewById(R.id.btnBack);
        btnScanBarcode = findViewById(R.id.btnScanBarcode);
        iconBarcode = findViewById(R.id.iconBarcode);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new StockIngredientAdapter(
                filteredIngredients,
                selectMode,
                this::returnSelectedIngredient,
                this::showIngredientNutrition,
                this
        );
        recyclerView.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        View.OnClickListener scanAction = v -> startBarcodeScan();
        btnScanBarcode.setOnClickListener(scanAction);
        iconBarcode.setOnClickListener(scanAction);

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterIngredients(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadStock();
    }

    private void loadStock() {
        new Thread(() -> {
            List<Ingredient> list = stockService.listStock();
            Log.d("DB_CHECK", "StockActivity DB: " + db);

            runOnUiThread(() -> {
                allIngredients.clear();
                allIngredients.addAll(list);
                filterIngredients(editSearch.getText().toString());
            });
        }).start();
    }

    private void filterIngredients(String query) {
        filteredIngredients.clear();

        if (query == null || query.trim().isEmpty()) {
            filteredIngredients.addAll(allIngredients);
        } else {
            String lower = query.toLowerCase();
            for (Ingredient ing : allIngredients) {
                if (ing.name != null && ing.name.toLowerCase().contains(lower)) {
                    filteredIngredients.add(ing);
                }
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void startBarcodeScan() {
        Intent i = new Intent(this, BarcodeScannerActivity.class);
        startActivityForResult(i, REQ_SCAN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_SCAN && resultCode == RESULT_OK) {
            String barcode = data.getStringExtra("barcode");
            if (barcode != null) showAddStockIngredientDialog(barcode);
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showAddStockIngredientDialog(String barcode) {
        AddIngredientDialog dialog = new AddIngredientDialog(this, AddIngredientDialog.MODE_STOCK);
        dialog.setOnIngredientAdded(this::loadStock);
        dialog.showWithBarcode(barcode);
    }

    private void returnSelectedIngredient(Ingredient ingredient) {
        if (!selectMode) return;

        Intent data = new Intent();
        data.putExtra("ingredientId", ingredient.id);
        data.putExtra("ingredientName", ingredient.name);

        setResult(RESULT_OK, data);
        finish();
    }

    private void showIngredientNutrition(Ingredient ingredient) {
        if (ingredient.barcode == null || ingredient.barcode.isEmpty()) {
            Toast.makeText(this, "No barcode for this ingredient.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            NutritionInfo info = nutritionRepository.getNutritionByBarcode(ingredient.barcode);

            runOnUiThread(() -> {
                if (info == null) {
                    Toast.makeText(this, "No nutrition data found.", Toast.LENGTH_SHORT).show();
                } else {
                    com.example.utaste.ui.dialogs.NutritionDialog.show(this, ingredient.name, info);
                }
            });
        }).start();
    }
}
