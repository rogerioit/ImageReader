package com.rion.imagereader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.rion.imagereader.application.AndroidApplication;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        ( (AndroidApplication) getApplication()).getComponent().inject(this);

        setSupportActionBar(toolbar);
    }

    @OnClick(R.id.fab)
    public void callCamera() {
        //todo call camera
    }
}
