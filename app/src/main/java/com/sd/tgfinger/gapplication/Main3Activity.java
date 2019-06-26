package com.sd.tgfinger.gapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sd.tgfinger.utils.AudioProvider;

public class Main3Activity extends AppCompatActivity implements View.OnClickListener {

    private AudioProvider ap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Button btn1 = findViewById(R.id.btn1);
        Button btn2 = findViewById(R.id.btn2);
        Button btn3 = findViewById(R.id.btn3);
        Button btn4 = findViewById(R.id.btn4);
        Button btn5 = findViewById(R.id.btn5);
        Button btn6 = findViewById(R.id.btn6);
        Button btn7 = findViewById(R.id.btn7);
        Button btn8 = findViewById(R.id.btn8);
        Button btn9 = findViewById(R.id.btn9);
        Button btn10 = findViewById(R.id.btn10);
        Button btn11 = findViewById(R.id.btn11);
        Button btn12 = findViewById(R.id.btn12);
        Button btn13 = findViewById(R.id.btn13);
        Button btn14 = findViewById(R.id.btn14);
        Button btn15 = findViewById(R.id.btn15);
        Button btn16 = findViewById(R.id.btn16);
        Button btn17 = findViewById(R.id.btn17);
        Button btn18 = findViewById(R.id.btn18);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        btn4.setOnClickListener(this);
        btn5.setOnClickListener(this);
        btn6.setOnClickListener(this);
        btn7.setOnClickListener(this);
        btn8.setOnClickListener(this);
        btn9.setOnClickListener(this);
        btn10.setOnClickListener(this);
        btn11.setOnClickListener(this);
        btn12.setOnClickListener(this);
        btn13.setOnClickListener(this);
        btn14.setOnClickListener(this);
        btn15.setOnClickListener(this);
        btn16.setOnClickListener(this);
        btn17.setOnClickListener(this);
        btn18.setOnClickListener(this);


        ap = AudioProvider.getInstance(Main3Activity.this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                ap.play_di();
                break;
            case R.id.btn2:
                ap.play_didi();
                break;
            case R.id.btn3:
                ap.play_checkInSuccess();
                break;
            case R.id.btn4:
                ap.play_checkInFail();
                break;
            case R.id.btn5:
                ap.play_inputAgain();
                break;
            case R.id.btn6:
                ap.play_inputCorrect();
                break;
            case R.id.btn7:
                ap.play_inputDownGently();
                break;
            case R.id.btn8:
                ap.play_verifySuccess();
                break;
            case R.id.btn9:
                ap.play_verifyFail();
                break;
            case R.id.btn10:
                ap.play_retryFeature();
                break;
            case R.id.btn11:
                ap.play_deleteSuccess();
                break;
            case R.id.btn12:
                ap.play_deleteFail();
                break;
            case R.id.btn13:
                ap.play_featureFull();
                break;
            case R.id.btn14:
                ap.play_registerRepeat();
                break;
            case R.id.btn15:
                ap.play_upLiftFinger();
                break;
            case R.id.btn16:
                ap.play_notConnectNet();
                break;
            case R.id.btn17:
                ap.play_noConnectServer();
                break;
            case R.id.btn18:
                ap.play_doNotAuthRepeat();
                break;
        }
    }
}
