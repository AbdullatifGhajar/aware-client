package com.aware.ui;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aware.R;

/**
 * Created by denzil on 29/05/15.
 */
public class Aware_Toolbar extends Preference {
    public Aware_Toolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {

        parent.setPadding(0,0,0,0);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.aware_toolbar, parent, false);

        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.aware_toolbar);
        toolbar.setTitle(this.getTitle());
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceScreen pref = (PreferenceScreen) getPreferenceManager().findPreference(getKey());
                pref.getDialog().dismiss();
            }
        });

        return layout;
    }
}