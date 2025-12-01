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

public class ConfirmDeleteDialog {

    private final Context context;
    private final Runnable onConfirm;

    public ConfirmDeleteDialog(Context context, Runnable onConfirm) {
        this.context = context;
        this.onConfirm = onConfirm;
    }

    public void show() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm_delete, null);
        AlertDialog dialog = new AlertDialog.Builder(context, R.style.CustomDialog)
                .setView(view)
                .create();

        TextView title = view.findViewById(R.id.tvConfirmTitle);
        TextView msg = view.findViewById(R.id.tvConfirmMessage);
        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnConfirm = view.findViewById(R.id.btnConfirm);

        // Title + Message
        title.setText("Delete Recipes");
        msg.setText("Are you sure you want to delete the selected recipes?");
        btnConfirm.setText("Delete");

        // Cancel
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // Confirm
        btnConfirm.setOnClickListener(v -> {
            if (onConfirm != null) onConfirm.run();
            dialog.dismiss();
        });

        // Window + animation styling (same as all your other dialogs)
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

        dialog.show();
    }
}
