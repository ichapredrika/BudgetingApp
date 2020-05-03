package com.icha.budgetingapp;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.icha.budgetingapp.adapter.TransAdapter;
import com.icha.budgetingapp.db.TransHelper;
import com.icha.budgetingapp.entity.Trans;
import com.icha.budgetingapp.helper.MappingHelper;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class StatisticFragment extends Fragment {
    private TextView tvSaving;
    private TextView tvIncome;
    private TextView tvExpense;
    private Spinner spDate;
    private ArrayList<String> listData = new ArrayList<String>();
    private TransAdapter adapter;
    private TransHelper transHelper;
    private ArrayList<String> dateList = new ArrayList<>();
    private DecimalFormat df = new DecimalFormat("#,###");
    private static final String EXTRA_STATE = "EXTRA_STATE";
    private PieChart pieChart;
    private ArrayList<Entry> listTotalPie = new ArrayList<Entry>();

    public StatisticFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvSaving = view.findViewById(R.id.txt_balance);
        tvIncome = view.findViewById(R.id.txt_income);
        tvExpense = view.findViewById(R.id.txt_expense);
        spDate = view.findViewById(R.id.sp_date);
        pieChart = view.findViewById(R.id.piechart);

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

        spDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                getBalance(spDate.getSelectedItem().toString().substring(0, 7));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
    }

    private void getDate() {
        Cursor dataCursor = transHelper.getDate();
        dateList.addAll(MappingHelper.mapDateToArrayList(dataCursor));
        ArrayAdapter<String> adapterDate = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, dateList);
        adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDate.setAdapter(adapterDate);
    }

    private void getBalance(String substring) {
        double income = transHelper.sumIncome(substring);
        double expense = transHelper.sumExpense(substring);
        double saving = (income - expense) / income * 100;

        listTotalPie.clear();
        listData.clear();
        listTotalPie.add(new Entry(Float.parseFloat(Double.toString(income)), 0));
        listTotalPie.add(new Entry(Float.parseFloat(Double.toString(expense)), 1));
        listData.add(AddFragment.EXTRA_INCOME);
        listData.add(AddFragment.EXTRA_EXPENSE);
        createPieChart();

        String savingStr = df.format(saving) + "%";
        String incometr = getString(R.string.format_amount, df.format(income));
        String expenseStr = getString(R.string.format_amount, df.format(expense));
        tvIncome.setText(incometr);
        tvExpense.setText(expenseStr);
        tvSaving.setText(savingStr);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, adapter.getListTrans());
    }

    private void createPieChart() {
        PieDataSet dataSet = new PieDataSet(listTotalPie, "");
        PieData data = new PieData(listData, dataSet);
        pieChart.setData(data);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.setContentDescription(null);
        pieChart.setDescription(null);
        pieChart.animateXY(3000, 3000);
    }
}
