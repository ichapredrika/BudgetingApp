package com.icha.budgetingapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.icha.budgetingapp.db.TransHelper;
import com.icha.budgetingapp.entity.Trans;

import static com.icha.budgetingapp.db.DatabaseContract.TransColumns.AMOUNT;
import static com.icha.budgetingapp.db.DatabaseContract.TransColumns.DATE;
import static com.icha.budgetingapp.db.DatabaseContract.TransColumns.DESCRIPTION;
import static com.icha.budgetingapp.db.DatabaseContract.TransColumns.TYPE;

public class AddFragment extends Fragment {

    private TextView edtAmount;
    private TextView edtDescription;
    private TextView edtDate;
    private RadioGroup rgType;
    private boolean isEdit = false;
    private Trans trans;
    private int position;
    private TransHelper transHelper;
    private UserPreference userPreference;

    private static final int REQUEST_CODE = 11;
    public static final String EXTRA_INCOME = "income";
    public static final String EXTRA_EXPENSE = "expense";
    public static final String EXTRA_TRANS = "extra_trans";
    public static final String EXTRA_POSITION = "extra_position";
    private final int ALERT_DIALOG_CLOSE = 10;
    private final int ALERT_DIALOG_DELETE = 20;

    public AddFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        final FragmentManager fm = getActivity().getSupportFragmentManager();

        edtAmount = view.findViewById(R.id.txt_amount);
        edtDescription = view.findViewById(R.id.txt_description);
        edtDate = view.findViewById(R.id.txt_date);
        rgType = view.findViewById(R.id.rg_type);
        Button btnSubmit = view.findViewById(R.id.btn_save);
        Button btnDelete = view.findViewById(R.id.btn_delete);
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            trans = bundle.getParcelable(EXTRA_TRANS);
            position = bundle.getInt(EXTRA_POSITION);
        }

        userPreference = new UserPreference(getContext());

        transHelper = TransHelper.getInstance(getContext().getApplicationContext());
        if (trans != null) {
            position = getActivity().getIntent().getIntExtra(EXTRA_POSITION, 0);
            isEdit = true;
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            trans = new Trans();
            btnDelete.setVisibility(View.GONE);
        }

        String actionBarTitle;
        String btnTitle;

        if (isEdit) {
            actionBarTitle = "Change Transaction";
            btnTitle = "Update";

            if (trans != null) {
                edtAmount.setText(Double.toString(trans.getAmount()));
                edtDate.setText(trans.getDate());
                edtDescription.setText(trans.getDescription());
                if (trans.getType().equals(EXTRA_INCOME)) {
                    rgType.check(R.id.rb_income);
                } else {
                    rgType.check(R.id.rb_expense);
                }
            }

        } else {
            actionBarTitle = "Add Transaction";
            btnTitle = "Save";
        }

        toolbar.setTitle(actionBarTitle);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        btnSubmit.setText(btnTitle);


        edtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatDialogFragment newFragment = new DatePickerFragment();
                newFragment.setTargetFragment(AddFragment.this, REQUEST_CODE);
                newFragment.show(fm, "datePicker");
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(ALERT_DIALOG_DELETE);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(edtAmount.getText()) && !TextUtils.isEmpty(edtDate.getText()) && !TextUtils.isEmpty(edtDescription.getText())) {

                    double amount = Double.parseDouble(edtAmount.getText().toString());
                    String description = edtDescription.getText().toString().trim();
                    String date = edtDate.getText().toString();
                    String type;
                    if (rgType.getCheckedRadioButtonId() == R.id.rb_income) {
                        type = EXTRA_INCOME;
                    } else {
                        type = EXTRA_EXPENSE;
                    }

                    double expense = transHelper.sumExpense(date.substring(0, 7));
                    double maxSpent = userPreference.getWarning();
                    double totalExpense = expense + amount;

                    if (type.equals(EXTRA_EXPENSE) && totalExpense > maxSpent && maxSpent > 0) {
                        Toast.makeText(getContext(), "You have spent more than maximum monthly spent!", Toast.LENGTH_SHORT).show();
                        maxSpent = totalExpense + 100000;
                        String maxSpentstr = Double.toString(maxSpent);
                        userPreference.setWarning(Float.parseFloat(maxSpentstr));
                    }

                    trans.setType(type);
                    trans.setAmount(amount);
                    trans.setDescription(description);

                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_TRANS, trans);
                    intent.putExtra(EXTRA_POSITION, position);

                    ContentValues values = new ContentValues();
                    values.put(DESCRIPTION, description);
                    values.put(AMOUNT, amount);
                    values.put(DATE, date);
                    values.put(TYPE, type);

                    if (isEdit) {
                        long result = transHelper.update(String.valueOf(trans.getId()), values);
                        if (result > 0) {
                            ((AppCompatActivity) getContext()).getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                            TransactionFragment fragment = new TransactionFragment();
                            ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container_layout, fragment)
                                    .addToBackStack(null)
                                    .commit();

                        } else
                            Toast.makeText(getContext(), "Fail updating data", Toast.LENGTH_SHORT).show();
                    } else {
                        long result = transHelper.insert(values);

                        if (result > 0) {
                            trans.setId((int) result);
                            ((AppCompatActivity) getContext()).getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                            TransactionFragment fragment = new TransactionFragment();
                            ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container_layout, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        } else
                            Toast.makeText(getContext(), "Fail adding data", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Please input all fields!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showAlertDialog(int type) {
        final boolean isDialogClose = type == ALERT_DIALOG_CLOSE;
        String dialogTitle, dialogMessage;
        if (isDialogClose) {
            dialogTitle = "Cancel";
            dialogMessage = "Do you want to cancel the the changes?";
        } else {
            dialogMessage = "Do you want to delete this transaction?";
            dialogTitle = "Delete Transaction";
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setTitle(dialogTitle);
        alertDialogBuilder
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (isDialogClose) {
                            ((AppCompatActivity) getContext()).getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            TransactionFragment fragment = new TransactionFragment();
                            ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.container_layout, fragment)
                                    .addToBackStack(null)
                                    .commit();

                        } else {
                            long result = transHelper.deleteById(String.valueOf(trans.getId()));
                            if (result > 0) {
                                ((AppCompatActivity) getContext()).getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                TransactionFragment fragment = new TransactionFragment();
                                ((AppCompatActivity) getContext()).getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.container_layout, fragment)
                                        .addToBackStack(null)
                                        .commit();
                            } else {
                                Toast.makeText(getContext(), "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                            }
                        }
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String selectedDate = data.getStringExtra("selectedDate");
            edtDate.setText(selectedDate);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showAlertDialog(ALERT_DIALOG_CLOSE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
