package com.example.utaste.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.utaste.R;
import com.example.utaste.backend.NutritionInfo;

public class NutritionDialog {

    public static void show(Context context, String title, NutritionInfo info) {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_nutrition, null);

        TextView tvTitle = view.findViewById(R.id.tvNutritionTitle);
        TextView tvIngredientName = view.findViewById(R.id.tvNumberOfSales);
        TextView tvEnergy = view.findViewById(R.id.tvEnergy);
        TextView tvCarbs = view.findViewById(R.id.tvCarbs);
        TextView tvProteins = view.findViewById(R.id.tvProteins);
        TextView tvFat = view.findViewById(R.id.tvFat);
        TextView tvSalt = view.findViewById(R.id.tvSalt);
        TextView tvFibers = view.findViewById(R.id.tvFibers);
        Button btnClose = view.findViewById(R.id.btnClose);

        tvTitle.setText("Nutrition");
        tvIngredientName.setText(title);
        tvEnergy.setText(value(info.energyKcalPer100g, "kcal"));
        tvCarbs.setText(value(info.carbsPer100g, "g"));
        tvProteins.setText(value(info.proteinsPer100g, "g"));
        tvFat.setText(value(info.fatPer100g, "g"));
        tvSalt.setText(value(info.saltPer100g, "g"));
        tvFibers.setText(value(info.fibersPer100g, "g"));

        AlertDialog dialog = new AlertDialog.Builder(context, R.style.CustomDialog)
                .setView(view)
                .create();

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

        // ------- Add missing custom window styling -------
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
            );
            window.setGravity(android.view.Gravity.CENTER);
            window.setWindowAnimations(R.style.DialogFadeAnimation);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.45f;
            window.setAttributes(lp);
        }
        // -------------------------------------------------
    }

    private static String value(Float v, String unit) {
        return v == null ? "N/A" : (v + " " + unit);
    }
}
