package com.example.utaste;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.utaste.backend.Role;
import com.example.utaste.backend.UTasteApplication;
import com.example.utaste.backend.UTasteBackend;
import com.example.utaste.backend.User;
import com.example.utaste.backend.UserEntity;
import com.example.utaste.backend.Waiter;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class WaiterActivity extends AppCompatActivity {

    private Button btnLogout, btnWaiterSave, btnViewRecipes, btnRecordSale, btnSalesReport;
    private EditText editWaiterPassword;
    private LinearLayout cardViewRecipes, cardRecordSale, cardSalesReport;

    private TextView tvWaiterName, tvWaiterEmail, tvWaiterCreatedAt, tvWaiterUpdatedAt;

    private UTasteBackend backend;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        backend = UTasteApplication.getInstance().getBackend();
        user = backend.getCurrentUser();

        if (user == null) {
            String email = getIntent().getStringExtra("user_email");
            if (email != null) {
                user = backend.getUserManager().getUserByEmail(email);
                backend.setCurrentUser(user);
            }
        }

        boolean isWaiter =
                (user instanceof Waiter)
                        || (user != null && user.getRole() == Role.WAITER);

        if (!isWaiter) {
            Toast.makeText(this, "Access denied: invalid session.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Log.i("WaiterActivity", "Connected as waiter: " + user.getEmail());

        btnLogout = findViewById(R.id.btnLogout);
        btnViewRecipes = findViewById(R.id.btnViewRecipes);
        btnRecordSale = findViewById(R.id.btnRecordSale);
        btnSalesReport = findViewById(R.id.btnSalesReport);
        btnWaiterSave = findViewById(R.id.btnWaiterSave);

        editWaiterPassword = findViewById(R.id.editWaiterPassword);
        editWaiterPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editWaiterPassword.setText(user.getPassword()); // masked automatically

        cardViewRecipes = findViewById(R.id.cardViewRecipes);
        cardRecordSale = findViewById(R.id.cardRecordSale);
        cardSalesReport = findViewById(R.id.cardSalesReport);

        tvWaiterName = findViewById(R.id.tvWaiterName);
        tvWaiterEmail = findViewById(R.id.tvWaiterEmail);
        tvWaiterCreatedAt = findViewById(R.id.tvWaiterCreatedAt);
        tvWaiterUpdatedAt = findViewById(R.id.tvWaiterUpdatedAt);

        updateWaiterInfo();

        btnLogout.setOnClickListener(v -> showLogoutDialog());

        cardViewRecipes.setOnClickListener(v -> openWaiterRecipes());
        btnViewRecipes.setOnClickListener(v -> openWaiterRecipes());

        cardRecordSale.setOnClickListener(v -> startActivity(new Intent(this, RecordSaleActivity.class)));
        btnRecordSale.setOnClickListener(v -> startActivity(new Intent(this, RecordSaleActivity.class)));

        cardSalesReport.setOnClickListener(v -> startActivity(new Intent(this, SalesReportActivity.class)));
        btnSalesReport.setOnClickListener(v -> startActivity(new Intent(this, SalesReportActivity.class)));

        btnWaiterSave.setOnClickListener(v -> changePassword());
    }

    private void showLogoutDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_delete);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView title = dialog.findViewById(R.id.tvConfirmTitle);
        TextView message = dialog.findViewById(R.id.tvConfirmMessage);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        title.setText("Log out");
        message.setText("Are you sure you want to log out?");
        btnConfirm.setText("Log out");

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            backend.logout(user);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        dialog.show();
    }

    private void openWaiterRecipes() {
        Intent i = new Intent(this, ViewRecipesActivity.class);
        i.putExtra("waiterMode", true);
        i.putExtra("selectMode", false);
        startActivity(i);
    }

    private void changePassword() {
        String newPwd = editWaiterPassword.getText().toString().trim();

        if (newPwd.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_delete);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView title = dialog.findViewById(R.id.tvConfirmTitle);
        TextView placeholder = dialog.findViewById(R.id.tvConfirmMessage);
        Button cancel = dialog.findViewById(R.id.btnCancel);
        Button confirm = dialog.findViewById(R.id.btnConfirm);

        title.setText("Confirm Password Change");

        EditText pwdInput = new EditText(this);
        pwdInput.setHint("Enter current password");
        pwdInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout box = (LinearLayout) placeholder.getParent();
        box.removeView(placeholder);
        box.addView(pwdInput, 1);

        confirm.setText("Confirm");

        cancel.setOnClickListener(v -> dialog.dismiss());

        confirm.setOnClickListener(v -> {
            String currentPwd = pwdInput.getText().toString().trim();
            if (currentPwd.isEmpty()) {
                Toast.makeText(this, "Current password is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                backend.changeOwnPassword(currentPwd, newPwd);
                Toast.makeText(this, "Password updated!", Toast.LENGTH_LONG).show();
                editWaiterPassword.setText("");
                updateWaiterInfo();
                dialog.dismiss();

            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }

    private void updateWaiterInfo() {
        String full = user.getFirstName();
        if (user.getLastName() != null && !user.getLastName().isEmpty()) {
            full += " " + user.getLastName();
        }
        tvWaiterName.setText(full);
        tvWaiterEmail.setText(user.getEmail());

        UserEntity entity =
                backend.getDatabase().userDao().findByEmail(user.getEmail());

        if (entity != null) {
            SimpleDateFormat fmt =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            tvWaiterCreatedAt.setText(entity.createdAt == 0 ? "" : fmt.format(entity.createdAt));
            tvWaiterUpdatedAt.setText(entity.updatedAt == 0 ? "" : fmt.format(entity.updatedAt));
        }
    }
}
