package com.sd.tgfinger.gapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


/**
 * 入口Activity
 */
public class Main4Activity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        Button frontBtn = findViewById(R.id.frontBtn);
        Button behindBtn = findViewById(R.id.behindBtn);


        frontBtn.setOnClickListener(this);
        behindBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frontBtn:
                startActivity(new Intent(Main4Activity.this, FrontActivity.class));
                break;
            case R.id.behindBtn:
                startActivity(new Intent(Main4Activity.this, BehindActivity.class));
                break;
        }
    }
}
