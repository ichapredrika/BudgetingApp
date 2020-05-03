package com.icha.budgetingapp;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.icha.budgetingapp.entity.Trans;

import java.util.ArrayList;
import java.util.List;

class UserPreference {
    private static final String PREFS_NAME = "user_pref";
    private static final String MAX_SPENT = "max_spent";
    private static final String ARRAY_TRANS = "array_trans";

    private final SharedPreferences preferences;

    UserPreference(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    void setWarning(float maxSpent) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(MAX_SPENT, maxSpent);
        editor.apply();
    }

    float getWarning() {
        return preferences.getFloat(MAX_SPENT, 0);
    }

    void addTrans(Trans value) {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String response = preferences.getString(ARRAY_TRANS, "");
        ArrayList<Trans> transList = new ArrayList<>();
        if (!response.isEmpty()) {
            transList = gson.fromJson(response, new TypeToken<List<Trans>>() {
            }.getType());
        }
        transList.add(value);
        String json = gson.toJson(transList);
        editor.putString(ARRAY_TRANS, json);

        editor.apply();
    }

    ArrayList getTransList() {
        Gson gson = new Gson();
        String response = preferences.getString(ARRAY_TRANS, "");
        ArrayList<Trans> transList = gson.fromJson(response, new TypeToken<List<Trans>>() {
        }.getType());
        return transList;
    }

}


