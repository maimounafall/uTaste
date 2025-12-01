package com.example.utaste;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.utaste.ui.adapters.ImageGridAdapter;

import java.util.ArrayList;
import java.util.List;

public class SelectRecipeImageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_recipe_image);

        recyclerView = findViewById(R.id.recyclerRecipeImages);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 images per row

        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.recipe_pate);
        imageList.add(R.drawable.recipe_fondant);
        imageList.add(R.drawable.recipe_flan);
        imageList.add(R.drawable.recipe_crem_brulee);
        imageList.add(R.drawable.recipe_burger);
        imageList.add(R.drawable.recipe_pancake);
        imageList.add(R.drawable.recipe_steak);
        imageList.add(R.drawable.recipe_thiebou_dieune);
        imageList.add(R.drawable.recipe_sauce_bechamel);
        // Add more images if you have them

        ImageGridAdapter adapter = new ImageGridAdapter(imageList, imageRes -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedImageResId", imageRes);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });

        recyclerView.setAdapter(adapter);
    }
}
