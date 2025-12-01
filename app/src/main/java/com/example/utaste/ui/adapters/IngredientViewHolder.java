package com.example.utaste.ui.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.utaste.R;

public class IngredientViewHolder extends RecyclerView.ViewHolder {

    public TextView tvName, tvQuantity;
    public Button btnModify, btnDelete, btnNutrition;
    public ImageView imgNutrition;

    public IngredientViewHolder(View itemView) {
        super(itemView);

        tvName = itemView.findViewById(R.id.tvNumberOfSales);
        tvQuantity = itemView.findViewById(R.id.tvQuantity);

        btnModify = itemView.findViewById(R.id.btnModifyIngredient);
        btnDelete = itemView.findViewById(R.id.btnDeleteIngredient);

        btnNutrition = itemView.findViewById(R.id.btnNutrition);
        imgNutrition = itemView.findViewById(R.id.imgNutrition);
    }
}
