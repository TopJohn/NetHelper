package com.john;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button button;

    private ProgressDialog progressDialog;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {


            if (msg.arg1 == 1) {

                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "登陆成功~!", Toast.LENGTH_SHORT).show();
                finish();

            } else {
                Toast.makeText(MainActivity.this, "登陆失败~!", Toast.LENGTH_SHORT).show();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custome_view);
        View view = getSupportActionBar().getCustomView();
        TextView textView = (TextView) view.findViewById(R.id.actionbar_title);
        textView.setText("NetHelper");

        button = (Button) findViewById(R.id.login);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!LoginUtil.hasInternet(MainActivity.this)) {

                    Toast.makeText(MainActivity.this, "请连接WIFI哦~!", Toast.LENGTH_SHORT).show();
                    return;

                }

                showDialog();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        LoginUtil.login(handler);

                    }
                }).start();


            }
        });

    }

    private void showDialog() {

        if (progressDialog == null) {

            progressDialog = ProgressDialog.show(MainActivity.this, "", "正在登陆哦~!", true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

        } else {
            progressDialog.show();
        }


    }


}
