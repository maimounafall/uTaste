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
import com.example.utaste.backend.UserManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChefActivity extends AppCompatActivity {


    private Button btnLogout, btnChefSave, btnAddRecipe, btnManageRecipes, btnManageStock;
    private TextView tvChefCreatedAt, tvChefUpdatedAt;
    private EditText editChefPassword;
    private User user;
    private UserManager userManager;
    private UTasteBackend backend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backend = UTasteApplication.getInstance().getBackend();
        userManager = backend.getUserManager();
        user = backend.getCurrentUser();

        Log.i("BackendCheck", "Backend instance: " + backend);

        if (user == null) {
            Log.w("ChefActivity", "No user in session.");
        } else if (user.getRole() == null) {
            user.setRole(Role.CHEF);
            backend.setCurrentUser(user);
        } else if (user.getRole() != Role.CHEF) {
            Toast.makeText(this, "Access denied: you are not a chef.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }


        btnLogout = findViewById(R.id.btnLogout);
        btnChefSave = findViewById(R.id.btnChefSave);
        btnAddRecipe = findViewById(R.id.btnAddRecipe);
        btnManageRecipes = findViewById(R.id.btnManageRecipes);
        editChefPassword = findViewById(R.id.editChefPassword);
        btnManageStock = findViewById(R.id.btnManageStock);
        tvChefCreatedAt = findViewById(R.id.tvChefCreatedAt);
        tvChefUpdatedAt = findViewById(R.id.tvChefUpdatedAt);

        editChefPassword.setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD
        );
        editChefPassword.setText(user.getPassword()); // masked but visible

        btnManageStock.setOnClickListener(v ->
                startActivity(new Intent(this, StockActivity.class)));



        btnLogout.setOnClickListener(v -> showLogoutDialog());

        btnAddRecipe.setOnClickListener(v ->
                startActivity(new Intent(this, AddRecipeActivity.class)));

        btnManageRecipes.setOnClickListener(v ->
                startActivity(new Intent(this, ManageRecipesActivity.class)));

        btnChefSave.setOnClickListener(v -> showPasswordChangeDialog());
        loadChefTimestamps();
    }
    private void loadChefTimestamps() {
        try {
            UserEntity entity = backend.getDatabase().userDao().findByEmail(user.getEmail());
            if (entity == null) return;

            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

            if (entity.createdAt != 0) {
                tvChefCreatedAt.setText( fmt.format(new java.util.Date(entity.createdAt)));
            } else {
                tvChefCreatedAt.setText("");
            }

            if (entity.updatedAt != 0) {
                tvChefUpdatedAt.setText(fmt.format(new java.util.Date(entity.updatedAt)));
            } else {
                tvChefUpdatedAt.setText("");
            }

        } catch (Exception e) {
            tvChefCreatedAt.setText("Error");
            tvChefUpdatedAt.setText("Error");
        }
    }


    // -------------------------------------------
    //  Logout dialog
    // -------------------------------------------
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

    // -------------------------------------------
    // Cute Password Change Dialog
    // -------------------------------------------
    private void showPasswordChangeDialog() {
        String newPwd = editChefPassword.getText().toString().trim();

        if (newPwd.isEmpty()) {
            Toast.makeText(this, "Please enter a new password.", Toast.LENGTH_SHORT).show();
            return;
        }

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_delete);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView title = dialog.findViewById(R.id.tvConfirmTitle);
        TextView placeholderMessage = dialog.findViewById(R.id.tvConfirmMessage);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        title.setText("Confirm Password Change");

        EditText input = new EditText(this);
        input.setHint("Current password");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout container = (LinearLayout) placeholderMessage.getParent();
        container.removeView(placeholderMessage);
        container.addView(input, 1);

        btnConfirm.setText("Confirm");

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String currentPwd = input.getText().toString().trim();

            if (currentPwd.isEmpty()) {
                Toast.makeText(this, "Current password is required.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                backend.changeOwnPassword(currentPwd, newPwd);
                user = backend.getUserManager().getUserByEmail(user.getEmail());
                backend.setCurrentUser(user);

                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        .format(new Date());

                Toast.makeText(this, "Password updated (" + date + ")", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }
}
