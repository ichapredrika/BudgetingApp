package com.icha.budgetingapp;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;

public class AddTransactionsFragment extends Fragment {

    private Dialog popUpDialog;
    private UserPreference userPreference;
    private double maxSpent;
    private DecimalFormat df = new DecimalFormat("#,###");

    public AddTransactionsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_transactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvAddTrans = view.findViewById(R.id.txt_add_trans);
        TextView tvSetWarning = view.findViewById(R.id.txt_set_max_spent);
        TextView tvAddMothly = view.findViewById(R.id.txt_add_monthly_expense);
        TextView tvMonthlyExpenseList = view.findViewById(R.id.txt_monthly_expense_list);
        popUpDialog = new Dialog(getContext());

        userPreference = new UserPreference(getContext());

        tvAddTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddFragment fragment = new AddFragment();
                ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_layout, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        tvAddMothly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddRepeatingTransFragment fragment = new AddRepeatingTransFragment();
                ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_layout, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        tvMonthlyExpenseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthlyBudgetListFragment fragment = new MonthlyBudgetListFragment();
                ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container_layout, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        tvSetWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpDialog.setContentView(R.layout.pop_up_set_warning);
                maxSpent = userPreference.getWarning();
                final TextView edtAmount = popUpDialog.findViewById(R.id.txt_max_spent);
                Button btnSave = popUpDialog.findViewById(R.id.btn_save);

                edtAmount.setText(Double.toString(maxSpent));
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(edtAmount.getText())) {
                            userPreference.setWarning(Float.parseFloat(edtAmount.getText().toString().trim()));
                            popUpDialog.dismiss();
                            double amount = Double.parseDouble(edtAmount.getText().toString());
                            String message = "Max spent is set to: " + getString(R.string.format_amount, df.format(amount));
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        } else edtAmount.setError("Please input the amount!");
                    }
                });
                if (popUpDialog.getWindow() != null) {
                    popUpDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    popUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
                popUpDialog.show();
            }
        });
    }
}
