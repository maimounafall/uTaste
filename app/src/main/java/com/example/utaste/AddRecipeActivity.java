package com.example.utaste;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.utaste.backend.AppDatabase;
import com.example.utaste.backend.RecipeService;
import com.example.utaste.backend.UTasteApplication;

public class AddRecipeActivity extends AppCompatActivity {

    private EditText etRecipeName, etRecipeDescription;
    private ImageView imgRecipePreview;
    private Button btnSelectImage, btnSaveRecipe;
    private ImageButton btnBack;

    private int selectedImageResId = -1;
    private RecipeService recipeService;

    private final ActivityResultLauncher<Intent> imageSelectorLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    int imageResId = result.getData().getIntExtra("selectedImageResId", -1);
                    if (imageResId != -1) {
                        selectedImageResId = imageResId;
                        imgRecipePreview.setBackground(null);
                        imgRecipePreview.setImageResource(imageResId);
                    }
                }
            });

    private void openImagePicker() {
        Intent intent = new Intent(this, SelectRecipeImageActivity.class);
        imageSelectorLauncher.launch(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.fade_in, 0);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_recipe);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        etRecipeName = findViewById(R.id.etRecipeName);
        etRecipeDescription = findViewById(R.id.etRecipeDescription);
        imgRecipePreview = findViewById(R.id.imgRecipePreview);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSaveRecipe = findViewById(R.id.btnSaveRecipe);
        btnBack = findViewById(R.id.btnBack);

        AppDatabase db = UTasteApplication.getInstance().getDatabase();
        recipeService = new RecipeService(db);

        imgRecipePreview.setImageDrawable(null);
        imgRecipePreview.setBackgroundResource(R.drawable.ic_add_photo);

        btnBack.setOnClickListener(v -> finish());
        btnSelectImage.setOnClickListener(v -> openImagePicker());
        btnSaveRecipe.setOnClickListener(v -> saveRecipe());
    }

    private void saveRecipe() {
        String name = etRecipeName.getText().toString().trim();
        String desc = etRecipeDescription.getText().toString().trim();

        if (name.isEmpty()) {
            etRecipeName.setError("Please enter a recipe name");
            return;
        }
        if (desc.isEmpty()) {
            etRecipeDescription.setError("Please enter a description");
            return;
        }
        if (selectedImageResId == -1) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                recipeService.createRecipe(
                        name,
                        String.valueOf(selectedImageResId),
                        desc,
                        1L
                );

                runOnUiThread(() -> {
                    Toast.makeText(this, "Recipe added", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(this, ManageRecipesActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                    finish();
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
}
