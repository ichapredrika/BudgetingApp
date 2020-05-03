package com.icha.budgetingapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.icha.budgetingapp.adapter.RepeatingTransAdapter;


public class MonthlyBudgetListFragment extends Fragment {

    public MonthlyBudgetListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_monthly_budget_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        UserPreference userPreference = new UserPreference(getContext());
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        RecyclerView rvTrans = view.findViewById(R.id.rv_trans);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvTrans.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTrans.setHasFixedSize(true);
        RepeatingTransAdapter adapter = new RepeatingTransAdapter(getActivity());
        rvTrans.setAdapter(adapter);
        adapter.setListTrans(userPreference.getTransList());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
