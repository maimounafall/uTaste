package com.example.utaste.ui.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.utaste.R;
import com.example.utaste.backend.Sale;
import com.example.utaste.backend.SaleDao;
import com.example.utaste.backend.UTasteApplication;

import java.util.List;

public class SalesReviewDialog {

    public static void show(Context context, long recipeId) {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_reviews, null);
        LinearLayout container = view.findViewById(R.id.containerReviews);

        // consistent DB access
        SaleDao saleDao = UTasteApplication.getInstance().getDatabase().saleDao();
        List<Sale> list = saleDao.getSalesForRecipe(recipeId);

        container.removeAllViews();

        for (Sale sale : list) {

            View card = LayoutInflater.from(context)
                    .inflate(R.layout.item_review, container, false);

            TextView tvRating = card.findViewById(R.id.tvReviewRating);
            TextView tvComment = card.findViewById(R.id.tvReviewComment);

            tvRating.setText(String.format("%.1f ★", sale.rating));
            tvComment.setText(sale.comment == null ? "" : sale.comment);

            container.addView(card);
        }

        AlertDialog dialog = new AlertDialog.Builder(context, R.style.CustomDialog)
                .setView(view)
                .create();

        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setWindowAnimations(R.style.DialogFadeAnimation);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.45f;
            window.setAttributes(lp);
        }
    }
}
