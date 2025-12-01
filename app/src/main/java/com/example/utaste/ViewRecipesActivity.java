package com.example.utaste;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utaste.backend.AppDatabase;
import com.example.utaste.backend.RecipeDao;
import com.example.utaste.backend.RecipeEntity;
import com.example.utaste.backend.RecipeService;
import com.example.utaste.backend.UTasteApplication;
import com.example.utaste.ui.adapters.RecipeAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewRecipesActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private RecyclerView recyclerRecipes;

    private RecipeAdapter adapter;
    private RecipeDao recipeDao;
    private RecipeService recipeService;

    private final List<RecipeEntity> recipeList = new ArrayList<>();

    public static final int RESULT_RECIPE_SELECTED = 5005; // custom code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipes);

        btnBack = findViewById(R.id.btnBack);
        recyclerRecipes = findViewById(R.id.recyclerRecipes);
        recyclerRecipes.setLayoutManager(new LinearLayoutManager(this));

        AppDatabase db = UTasteApplication.getInstance().getDatabase();
        recipeDao = db.recipeDao();
        recipeService = new RecipeService(db);

        btnBack.setOnClickListener(v -> finish());

        loadRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecipes();
    }

    private void loadRecipes() {

        List<RecipeEntity> list = recipeDao.listAll();
        recipeList.clear();
        recipeList.addAll(list);

        Log.d("DEBUG_DB", "recipes = " + list.size());

        boolean selectionMode = getIntent().getBooleanExtra("selectMode", false);
        boolean waiterMode = getIntent().getBooleanExtra("waiterMode", false);
        adapter = new RecipeAdapter(
                this,
                recipeList,
                recipeService,
                null,                // onDataChanged
                waiterMode,          // isWaiterMode
                false,               // isSalesReportMode
                selectionMode,       // isSelectionMode
                false                // isManageRecipesMode
        );


        recyclerRecipes.setAdapter(adapter);
    }

    @Override
    public void finish() {
        super.finish();
    }
}
