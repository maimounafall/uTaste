package com.example.utaste;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.utaste.backend.AppDatabase;
import com.example.utaste.backend.RecipeDao;
import com.example.utaste.backend.RecipeEntity;
import com.example.utaste.backend.RecipeService;
import com.example.utaste.backend.Sale;
import com.example.utaste.backend.SaleDao;
import com.example.utaste.backend.SellerService;
import com.example.utaste.backend.UTasteApplication;

public class RecordSaleActivity extends AppCompatActivity {

    private static final int REQUEST_SELECT_RECIPE = 2005;

    private ImageButton btnBack;
    private ImageView imgRecipePreview;
    private EditText etRating, etReview;
    private TextView tvRecipeName;
    private Button btnCancel, btnSave;

    private long selectedRecipeId = -1;
    private RecipeDao recipeDao;
    private SaleDao saleDao;
    private SellerService sellerService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_sale);

        AppDatabase db = UTasteApplication.getInstance().getDatabase();

        recipeDao = db.recipeDao();
        saleDao = db.saleDao();
        sellerService = new SellerService(recipeDao, saleDao, new RecipeService(db));
        imgRecipePreview = findViewById(R.id.imgRecipeSalePreview);
        tvRecipeName = findViewById(R.id.tvRecipeName);
        etRating = findViewById(R.id.etRecipeRating);
        etReview = findViewById(R.id.etRecipeReview);
        btnCancel = findViewById(R.id.btnCancel);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // Open recipe selector
        tvRecipeName.setOnClickListener(v -> {
            Intent intent = new Intent(this, ViewRecipesActivity.class);
            intent.putExtra("selectMode", true);
            intent.putExtra("waiterMode", false);
            startActivityForResult(intent, REQUEST_SELECT_RECIPE);
        });

        btnSave.setOnClickListener(v -> saveSale());
        btnCancel.setOnClickListener(v -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_RECIPE &&
                resultCode == Activity.RESULT_OK &&
                data != null &&
                data.hasExtra("recipeId")) {

            selectedRecipeId = data.getLongExtra("recipeId", -1);
            fillRecipeInfo(selectedRecipeId);
        }
    }

    private void fillRecipeInfo(long recipeId) {
        RecipeEntity recipe = recipeDao.findById(recipeId);
        if (recipe == null) return;

        tvRecipeName.setText(recipe.name);

        try {
            int resId = Integer.parseInt(recipe.imagePath);
            imgRecipePreview.setImageResource(resId);
        } catch (Exception e) {
            Glide.with(this)
                    .load(recipe.imagePath)
                    .placeholder(R.drawable.ic_add_photo)
                    .into(imgRecipePreview);
        }
    }

    private void saveSale() {

        if (selectedRecipeId == -1) {
            Toast.makeText(this, "Select a recipe first", Toast.LENGTH_SHORT).show();
            return;
        }

        String ratingStr = etRating.getText().toString().trim();
        if (ratingStr.isEmpty()) {
            Toast.makeText(this, "Rating is required", Toast.LENGTH_SHORT).show();
            return;
        }

        float rating;
        try {
            rating = Float.parseFloat(ratingStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid rating format", Toast.LENGTH_SHORT).show();
            return;
        }

        String review = etReview.getText().toString().trim();

        try {
            sellerService.recordSale(
                    selectedRecipeId,
                    1,          // quantity
                    rating,
                    review
            );

            Toast.makeText(this, "Sale recorded!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, SalesReportActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
