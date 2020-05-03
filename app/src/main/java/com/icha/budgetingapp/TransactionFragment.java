package com.icha.budgetingapp;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.icha.budgetingapp.adapter.TransAdapter;
import com.icha.budgetingapp.db.TransHelper;
import com.icha.budgetingapp.entity.Trans;
import com.icha.budgetingapp.helper.MappingHelper;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class TransactionFragment extends Fragment implements LoadTransCallback {

    private ProgressBar progressBar;
    private TransAdapter adapter;
    private TransHelper transHelper;
    private TextView tvTotalBalance;
    private TextView tvBalance;
    private TextView tvIncome;
    private TextView tvExpense;
    private Spinner spDate;
    private ArrayList<String> dateList = new ArrayList<>();
    private static final String EXTRA_STATE = "EXTRA_STATE";
    private DecimalFormat df = new DecimalFormat("#,###");

    public TransactionFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progressbar);
        tvBalance = view.findViewById(R.id.txt_balance);
        tvIncome = view.findViewById(R.id.txt_income);
        tvExpense = view.findViewById(R.id.txt_expense);
        tvTotalBalance = view.findViewById(R.id.txt_total_balance);
        spDate = view.findViewById(R.id.sp_date);
        RecyclerView rvTrans = view.findViewById(R.id.rv_trans);
        rvTrans.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTrans.setHasFixedSize(true);
        adapter = new TransAdapter(getActivity());
        rvTrans.setAdapter(adapter);

        transHelper = TransHelper.getInstance(getActivity().getApplicationContext());
        transHelper.open();

        if (savedInstanceState == null) {
            getDate();
        } else {
            ArrayList<Trans> list = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if (list != null) {
                adapter.setListTrans(list);
            }
        }

        getTotalBalance();

        spDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                loadMonthlyTrans(spDate.getSelectedItem().toString().substring(0, 7));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
    }

    private void loadMonthlyTrans(String substring) {
        getBalance(substring);
        new LoadTransAsync(transHelper, substring, this).execute();
    }

    private void getDate() {
        Cursor dataCursor = transHelper.getDate();
        dateList.addAll(MappingHelper.mapDateToArrayList(dataCursor));
        ArrayAdapter<String> adapterDate = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dateList);
        adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDate.setAdapter(adapterDate);
    }

    private void getTotalBalance() {
        double totalIncome = transHelper.sumTotalIncome();
        double totalExpense = transHelper.sumTotalExpense();
        double totalBalance = totalIncome - totalExpense;

        String balanceStr = getString(R.string.format_amount, df.format(totalBalance));
        tvTotalBalance.setText(balanceStr);
    }

    private void getBalance(String substring) {
        double income = transHelper.sumIncome(substring);
        double expense = transHelper.sumExpense(substring);
        double balance = income - expense;

        String balanceStr = getString(R.string.format_amount, df.format(balance));
        String incometr = getString(R.string.format_amount, df.format(income));
        String expenseStr = getString(R.string.format_amount, df.format(expense));
        tvIncome.setText(incometr);
        tvExpense.setText(expenseStr);
        tvBalance.setText(balanceStr);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, adapter.getListTrans());
    }

    @Override
    public void preExecute() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void postExecute(ArrayList<Trans> trans) {
        progressBar.setVisibility(View.INVISIBLE);
        if (trans.size() > 0) {
            adapter.setListTrans(trans);
        } else {
            adapter.setListTrans(new ArrayList<Trans>());
            Toast.makeText(getContext(), "No Data", Toast.LENGTH_SHORT).show();
        }
    }

    private static class LoadTransAsync extends AsyncTask<Void, Void, ArrayList<Trans>> {
        private final WeakReference<TransHelper> weakTransHelper;
        private final WeakReference<LoadTransCallback> weakCallback;
        private String subDate;

        private LoadTransAsync(TransHelper transHelper, String subDate, LoadTransCallback callback) {
            weakTransHelper = new WeakReference<>(transHelper);
            weakCallback = new WeakReference<>(callback);
            this.subDate = subDate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected ArrayList<Trans> doInBackground(Void... voids) {
            Cursor dataCursor = weakTransHelper.get().queryByDate(subDate);
            return MappingHelper.mapCursorToArrayList(dataCursor);
        }

        @Override
        protected void onPostExecute(ArrayList<Trans> trans) {
            super.onPostExecute(trans);
            weakCallback.get().postExecute(trans);
        }

    }
}

interface LoadTransCallback {
    void preExecute();

    void postExecute(ArrayList<Trans> trans);
}
