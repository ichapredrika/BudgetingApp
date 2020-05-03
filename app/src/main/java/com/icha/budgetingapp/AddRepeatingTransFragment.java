package com.icha.budgetingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.icha.budgetingapp.entity.Trans;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.icha.budgetingapp.AddFragment.EXTRA_EXPENSE;
import static com.icha.budgetingapp.AddFragment.EXTRA_INCOME;

public class AddRepeatingTransFragment extends Fragment {
    private TextView edtAmount;
    private TextView edtDescription;
    private RadioGroup rgType;
    private UserPreference userPreference;
    private AlarmReceiver alarmReceiver;

    private static final int REQUEST_CODE = 11;
    private final int ALERT_DIALOG_CLOSE = 10;
    private final int ALERT_DIALOG_DELETE = 20;

    public AddRepeatingTransFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_repeating_trans, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        edtAmount = view.findViewById(R.id.txt_amount);
        edtDescription = view.findViewById(R.id.txt_description);
        rgType = view.findViewById(R.id.rg_type);
        Button btnSubmit = view.findViewById(R.id.btn_save);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        alarmReceiver = new AlarmReceiver();
        userPreference = new UserPreference(getContext());

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edtAmount.getText()) && !TextUtils.isEmpty(edtDescription.getText())) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date datePick = new Date();

                    double amount = Double.parseDouble(edtAmount.getText().toString());
                    String description = edtDescription.getText().toString().trim();
                    String date = dateFormat.format(datePick);
                    String type;
                    if (rgType.getCheckedRadioButtonId() == R.id.rb_income) {
                        type = EXTRA_INCOME;
                    } else {
                        type = EXTRA_EXPENSE;
                    }
                    Trans trans = new Trans();
                    trans.setType(type);
                    trans.setAmount(amount);
                    trans.setDescription(description);
                    trans.setDate(date);
                    userPreference.addTrans(trans);

                    alarmReceiver.setRepeatingAlarm(getContext());
                    ((AppCompatActivity) getContext()).getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    MonthlyBudgetListFragment fragment = new MonthlyBudgetListFragment();
                    ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_layout, fragment)
                            .addToBackStack(null)
                            .commit();
                } else {
                    Toast.makeText(getContext(), "Please input all fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showAlertDialog() {
        String dialogTitle = "Cancel";
        String dialogMessage = "Do you want to cancel the the changes?";

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        ((AppCompatActivity) getContext()).getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        AddTransactionsFragment fragment = new AddTransactionsFragment();
                        ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container_layout, fragment)
                                .addToBackStack(null)
                                .commit();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showAlertDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
