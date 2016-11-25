package com.pinta.ws_service.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.pinta.ws_service.WsManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, WsManager.WsCallbackListeners {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initListeners();

        WsManager.getWsManager().registerCallback(this, this);
    }

    private void initListeners() {
        findViewById(R.id.b_connect_ws).setOnClickListener(this);
        findViewById(R.id.b_subscribe).setOnClickListener(this);
        findViewById(R.id.b_call).setOnClickListener(this);
        findViewById(R.id.b_publish).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WsManager.getWsManager().disconnect(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.b_connect_ws:
                WsManager.getWsManager()
                        .setPort(((EditText) findViewById(R.id.et_input_ws_host)).getText().toString())
                        .setLog(true)
                        .setHeartBeat(60000L)
                        .connect(this);
                break;
            case R.id.b_subscribe:
                WsManager.getWsManager().subscribe(this,
                        ((EditText) findViewById(R.id.et_input_subscribe)).getText().toString());
                break;
            case R.id.b_call:
                WsManager.getWsManager().call(this,
                        ((EditText) findViewById(R.id.et_input_call)).getText().toString());
                break;
            case R.id.b_publish:
                WsManager.getWsManager().publish(this,
                        ((EditText) findViewById(R.id.et_input_publish_topic)).getText().toString(),
                        ((EditText) findViewById(R.id.et_input_publish_message)).getText().toString());
                break;
        }
    }

    @Override
    public void onWsOpenCallbackListener() {
        Toast.makeText(this, "ws connected successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWsCloseCallbackListener(String onCloseMessage) {
        Toast.makeText(this, "ws was closed with error: " + onCloseMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWsSubscribeCallbackListener(String onSubscribeMessage) {
        Toast.makeText(this, "ws subscribe response: " + onSubscribeMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWsCallCallbackListener(String onCallMessage) {
        Toast.makeText(this, "ws call response: " + onCallMessage, Toast.LENGTH_SHORT).show();
    }
}
