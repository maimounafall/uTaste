package com.example.utaste;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utaste.backend.AppDatabase;
import com.example.utaste.backend.RecipeDao;
import com.example.utaste.backend.RecipeEntity;
import com.example.utaste.backend.RecipeSalesStats;
import com.example.utaste.backend.RecipeService;
import com.example.utaste.backend.Sale;
import com.example.utaste.backend.SaleDao;
import com.example.utaste.backend.SellerService;
import com.example.utaste.backend.UTasteApplication;
import com.example.utaste.ui.adapters.RecipeAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesReportActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Button btnSelectSales;
    private FrameLayout btnDeleteSales;
    private ImageButton btnBack;

    private RecipeAdapter adapter;

    private SellerService sellerService;
    private RecipeService recipeService;
    private SaleDao saleDao;
    private RecipeDao recipeDao;

    private final List<RecipeEntity> recipeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report);

        AppDatabase db = UTasteApplication.getInstance().getDatabase();

        saleDao = db.saleDao();
        recipeDao = db.recipeDao();
        recipeService = new RecipeService(db);
        sellerService = new SellerService(recipeDao, saleDao, recipeService);

        recyclerView = findViewById(R.id.recyclerRecipes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnSelectSales = findViewById(R.id.btnSelectSales);
        btnDeleteSales = findViewById(R.id.btnDeleteSales);
        btnBack = findViewById(R.id.btnBack);

        btnDeleteSales.setVisibility(View.GONE);

        btnBack.setOnClickListener(v -> finish());

        // ENTER MULTI-SELECT MODE
        btnSelectSales.setOnClickListener(v -> {
            if (adapter != null) {
                adapter.enableMultiSelect(true);   // <── CORRECT API
                btnDeleteSales.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Select a recipe to delete its last sale", Toast.LENGTH_SHORT).show();
            }
        });

        btnDeleteSales.setOnClickListener(v -> deleteLastSale());

        loadSalesReport();
        updateTotalSales();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSalesReport();
        updateTotalSales();
    }

    private void loadSalesReport() {

        List<RecipeSalesStats> stats = sellerService.getSalesReport();
        Map<Long, RecipeSalesStats> statMap = new HashMap<>();

        for (RecipeSalesStats s : stats) {
            statMap.put(s.recipeId, s);
        }

        recipeList.clear();

        for (RecipeSalesStats stat : stats) {
            RecipeEntity r = recipeDao.findById(stat.recipeId);
            if (r != null) {
                r.salesCount = stat.totalSales;
                r.averageRating = stat.averageRating;
                recipeList.add(r);
            }
        }

        adapter = new RecipeAdapter(
                this,
                recipeList,
                recipeService,
                () -> {
                    loadSalesReport();
                    updateTotalSales();
                },
                false,   // isWaiterMode
                true,    // isSalesReportMode
                false,   // isSelectionMode
                false    // isManageRecipesMode
        );


        // callback to toggle delete button visibility
        adapter.setOnSelectionChanged(() -> {
            if (adapter.getSelectedIds().isEmpty()) {
                btnDeleteSales.setVisibility(View.GONE);
            } else {
                btnDeleteSales.setVisibility(View.VISIBLE);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void updateTotalSales() {
        int total = 0;
        for (RecipeEntity r : recipeList) {
            total += r.salesCount;
        }

        TextView tv = findViewById(R.id.tvSalesNumber);
        tv.setText(String.valueOf(total));
    }

    private void deleteLastSale() {

        if (adapter.getSelectedIds().isEmpty()) {
            Toast.makeText(this, "Select a recipe first", Toast.LENGTH_SHORT).show();
            return;
        }

        long recipeId = adapter.getSelectedIds().iterator().next();

        List<Sale> sales = saleDao.getSalesForRecipe(recipeId);
        if (sales.isEmpty()) {
            Toast.makeText(this, "No sales to delete", Toast.LENGTH_SHORT).show();
            return;
        }

        Sale lastSale = sales.get(0);
        saleDao.deleteById(lastSale.id);

        Toast.makeText(this, "Last sale deleted", Toast.LENGTH_SHORT).show();

        // Clear selection mode
        adapter.enableMultiSelect(false);
        btnDeleteSales.setVisibility(View.GONE);

        loadSalesReport();
        updateTotalSales();
    }
}
