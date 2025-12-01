package com.example.utaste;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utaste.backend.AppDatabase;
import com.example.utaste.backend.RecipeDao;
import com.example.utaste.backend.RecipeEntity;
import com.example.utaste.backend.RecipeService;
import com.example.utaste.backend.UTasteApplication;
import com.example.utaste.ui.adapters.RecipeAdapter;
import com.example.utaste.ui.dialogs.AddIngredientDialog;
import com.example.utaste.ui.dialogs.ConfirmDeleteDialog;

import java.util.ArrayList;
import java.util.List;

public class ManageRecipesActivity extends AppCompatActivity {

    private RecyclerView recyclerRecipes;
    private RecipeAdapter adapter;

    private ImageButton btnBack;
    private Button btnSelectMode;
    private FrameLayout btnDeleteSelected;

    private RecipeDao recipeDao;
    private RecipeService recipeService;
    private AppDatabase db;

    private AddIngredientDialog activeDialog = null;

    private final List<RecipeEntity> recipeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_recipes);

        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));

        btnBack = findViewById(R.id.btnBack);
        btnSelectMode = findViewById(R.id.btnSelectMode);
        btnDeleteSelected = findViewById(R.id.btnDeleteSelected);
        btnDeleteSelected.setVisibility(View.GONE);

        db = UTasteApplication.getInstance().getDatabase();
        recipeDao = db.recipeDao();
        recipeService = new RecipeService(db);

        btnBack.setOnClickListener(v -> finish());

        btnSelectMode.setOnClickListener(v -> {
            if (adapter == null) return;

            if (adapter.isMultiSelectEnabled()) {
                adapter.enableMultiSelect(false);
                btnDeleteSelected.setVisibility(View.GONE);
                btnSelectMode.setText("Select");
            } else {
                adapter.enableMultiSelect(true);
                btnDeleteSelected.setVisibility(View.VISIBLE);
                btnSelectMode.setText("Cancel");
            }
        });

        btnDeleteSelected.setOnClickListener(v ->
                new ConfirmDeleteDialog(this, this::deleteSelectedRecipes).show()
        );

        loadRecipes();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (activeDialog != null)
            activeDialog.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes();
    }

    private void loadRecipes() {
        recipeList.clear();
        recipeList.addAll(recipeDao.listAll());

        Log.d("DB_CHECK", "ManageRecipes DB = " + db);

        adapter = new RecipeAdapter(
                this,
                recipeList,
                recipeService,
                this::loadRecipes,
                false, // waiter
                false, // sales report
                false, // selection mode
                true   // MANAGE RECIPES MODE
        );

        adapter.setListener(recipeId -> {
            activeDialog = new AddIngredientDialog(
                    ManageRecipesActivity.this,
                    AddIngredientDialog.MODE_RECIPE,
                    recipeId
            );
            activeDialog.setOnIngredientAdded(this::loadRecipes);
            activeDialog.show();
        });

        adapter.setOnSelectionChanged(() -> {
            if (adapter.getSelectedIds().isEmpty()) {
                btnDeleteSelected.setVisibility(View.GONE);
                btnSelectMode.setText("Select");
            } else {
                btnDeleteSelected.setVisibility(View.VISIBLE);
            }
        });

        recyclerRecipes.setAdapter(adapter);
    }


    private void deleteSelectedRecipes() {
        if (adapter.getSelectedIds().isEmpty()) {
            Toast.makeText(this, "Select at least one recipe.", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            for (Long id : adapter.getSelectedIds()) {
                recipeService.deleteRecipe(id);
            }

            runOnUiThread(() -> {
                Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                btnDeleteSelected.setVisibility(View.GONE);
                btnSelectMode.setText("Select");
                loadRecipes();
            });
        }).start();
    }
}
