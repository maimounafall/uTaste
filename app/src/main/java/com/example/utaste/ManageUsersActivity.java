package com.example.utaste;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.utaste.backend.*;

import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    private LinearLayout listContainer;
    private TextView tvEmpty;
    private Button btnResetAdmin, btnResetChef;
    private UTasteBackend backend;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_users);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backend = UTasteApplication.getInstance().getBackend();
        userManager = backend.getUserManager();

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        listContainer = findViewById(R.id.listContainer);
        tvEmpty = findViewById(R.id.tvEmpty);
        btnResetAdmin = findViewById(R.id.btnResetAdmin);
        btnResetChef = findViewById(R.id.btnResetChef);

        btnResetAdmin.setOnClickListener(v -> {
            User admin = userManager.getUserByEmail(UserManager.DEFAULT_ADMIN_EMAIL);
            if (admin != null)
                showConfirmDialog("Reset Password", "Reset admin password?", () -> resetPassword(admin, "Admin"));
        });

        btnResetChef.setOnClickListener(v -> {
            User chef = userManager.getUserByEmail(UserManager.DEFAULT_CHEF_EMAIL);
            if (chef != null)
                showConfirmDialog("Reset Password", "Reset chef password?", () -> resetPassword(chef, "Chef"));
        });

        refreshWaiterList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshWaiterList();
    }

    private void refreshWaiterList() {
        listContainer.removeAllViews();
        List<Waiter> waiters = userManager.getAllWaiters();

        if (waiters.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            for (User waiter : waiters) {
                listContainer.addView(createWaiterCard(waiter));
            }
        }
    }

    private View createWaiterCard(User waiter) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setPadding(dp(14), dp(12), dp(14), dp(12));
        card.setBackgroundResource(R.drawable.pale_background);
        card.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dp(10), 0, 0);
        card.setLayoutParams(params);

        ImageView img = new ImageView(this);
        img.setImageResource(R.drawable.waiter_icon);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(dp(48), dp(48));
        imgParams.setMarginEnd(dp(12));
        img.setLayoutParams(imgParams);
        card.addView(img);

        LinearLayout infoLayout = new LinearLayout(this);
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        infoLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView tvName = new TextView(this);
        tvName.setText(waiter.getFirstName() + " " + waiter.getLastName());
        tvName.setTextColor(getColor(R.color.burgundy));
        tvName.setTextSize(17);
        tvName.setTypeface(null, Typeface.BOLD);
        infoLayout.addView(tvName);

        TextView tvEmail = new TextView(this);
        tvEmail.setText(waiter.getEmail());
        tvEmail.setTextColor(getColor(R.color.pale_grey));
        tvEmail.setTextSize(14);
        infoLayout.addView(tvEmail);

        card.addView(infoLayout);

        LinearLayout btnLayout = new LinearLayout(this);
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);
        btnLayout.setGravity(Gravity.CENTER_VERTICAL);
        card.addView(btnLayout);

        btnLayout.addView(createCircleButton(android.R.drawable.ic_menu_edit,
                v -> showEditDialog(waiter)));

        btnLayout.addView(createSpace());

        btnLayout.addView(createCircleButton(android.R.drawable.ic_menu_rotate,
                v -> showConfirmDialog("Reset Password", "Reset password for " + waiter.getEmail() + "?",
                        () -> resetPassword(waiter, "Waiter"))));

        btnLayout.addView(createSpace());

        btnLayout.addView(createCircleButton(android.R.drawable.ic_menu_delete,
                v -> showConfirmDialog("Delete Waiter", "Delete " + waiter.getEmail() + "?",
                        () -> deleteWaiter(waiter))));

        return card;
    }

    private View createCircleButton(int iconRes, View.OnClickListener listener) {
        ConstraintLayout wrapper = new ConstraintLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(46), dp(46));
        wrapper.setLayoutParams(params);
        wrapper.setBackgroundResource(R.drawable.circle_button);

        ImageView icon = new ImageView(this);
        icon.setImageResource(iconRes);
        icon.setPadding(dp(10), dp(10), dp(10), dp(10));
        icon.setColorFilter(getColor(R.color.burgundy));
        wrapper.addView(icon);

        Button btn = new Button(this);
        btn.setBackgroundColor(Color.TRANSPARENT);
        btn.setOnClickListener(listener);
        ConstraintLayout.LayoutParams btnParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
        );
        btn.setLayoutParams(btnParams);
        wrapper.addView(btn);

        return wrapper;
    }

    private void showConfirmDialog(String title, String message, Runnable onConfirm) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_delete);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvTitle = dialog.findViewById(R.id.tvConfirmTitle);
        TextView tvMessage = dialog.findViewById(R.id.tvConfirmMessage);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirm);

        tvTitle.setText(title);
        tvMessage.setText(message);

        if (title.toLowerCase().contains("delete")) {
            btnConfirm.setText("Delete");
        } else if (title.toLowerCase().contains("reset")) {
            btnConfirm.setText("Reset");
        } else {
            btnConfirm.setText("Confirm");
        }

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            onConfirm.run();
        });

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setWindowAnimations(R.style.DialogFadeAnimation);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.45f;
            window.setAttributes(lp);
        }

        dialog.show();
    }

    private void showEditDialog(User waiter) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edit_waiter);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        EditText etFirst = dialog.findViewById(R.id.etFirstName);
        EditText etLast = dialog.findViewById(R.id.etLastName);
        EditText etEmail = dialog.findViewById(R.id.etEmail);
        EditText etPwd = dialog.findViewById(R.id.etPassword);
        Button btnSave = dialog.findViewById(R.id.btnSave);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        etFirst.setText(waiter.getFirstName());
        etLast.setText(waiter.getLastName());
        etEmail.setText(waiter.getEmail());
        etPwd.setText(waiter.getPassword());

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String nf = etFirst.getText().toString().trim();
            String nl = etLast.getText().toString().trim();
            String ne = etEmail.getText().toString().trim();
            String np = etPwd.getText().toString().trim();

            if (nf.isEmpty() || ne.isEmpty() || np.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                waiter.setFirstName(nf);
                waiter.setLastName(nl);
                waiter.setEmail(ne);
                waiter.setPassword(np);

                userManager.updateUser(waiter);
                Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
                refreshWaiterList();
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.CENTER);
            window.setWindowAnimations(R.style.DialogFadeAnimation);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.4f;
            window.setAttributes(lp);
        }

        dialog.show();
    }

    private void resetPassword(User user, String role) {
        String newPwd = UserManager.DEFAULT_WAITER_PASSWORD;
        if (role.equalsIgnoreCase("Admin")) newPwd = UserManager.DEFAULT_ADMIN_PASSWORD;
        if (role.equalsIgnoreCase("Chef")) newPwd = UserManager.DEFAULT_CHEF_PASSWORD;

        user.setPassword(newPwd);
        userManager.updateUser(user);
        Toast.makeText(this, role + " password reset successfully.", Toast.LENGTH_SHORT).show();
    }

    private void deleteWaiter(User waiter) {
        userManager.removeUser(waiter.getEmail());
        Toast.makeText(this, "Waiter deleted successfully.", Toast.LENGTH_SHORT).show();
        refreshWaiterList();
    }

    private Space createSpace() {
        Space space = new Space(this);
        space.setLayoutParams(new LinearLayout.LayoutParams(dp(8), dp(1)));
        return space;
    }

    private int dp(int v) {
        return Math.round(getResources().getDisplayMetrics().density * v);
    }
}
