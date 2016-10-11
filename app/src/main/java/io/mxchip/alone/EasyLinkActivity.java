package io.mxchip.alone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.fogcloud.easylink.api.EasyLink;
import io.fogcloud.easylink.helper.EasyLinkCallBack;
import io.fogcloud.easylink.helper.EasyLinkParams;

/**
 * Created by Sin on 2016/07/25.
 * Email:88635653@qq.com
 */
public class EasyLinkActivity extends AppCompatActivity {
    private String TAG = "---ey--- ";

    private Button startsearch;
    private Button stopsearch;
    private EditText wifissid;
    private EditText wifipsw;
    private EditText sleeptime;
    private TextView logmessage;
    private EditText runtimesid;
    private EditText extrainfo;
    private EditText rc4key;

    public EasyLink elink;

    private MyHandler myhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.easy_link);

        wifissid = (EditText) findViewById(R.id.wifissid);
        wifipsw = (EditText) findViewById(R.id.wifipsw);
        startsearch = (Button) findViewById(R.id.startsearch);
        stopsearch = (Button) findViewById(R.id.stopsearch);
        logmessage = (TextView) findViewById(R.id.logmessage);
        sleeptime = (EditText) findViewById(R.id.sleeptime);

        extrainfo = (EditText) findViewById(R.id.extrainfo);
        runtimesid = (EditText) findViewById(R.id.runtimesid);
        rc4key = (EditText) findViewById(R.id.rc4key);

        elink = new EasyLink(EasyLinkActivity.this);

        wifissid.setText(elink.getSSID());

        myhandler = new MyHandler();

        startsearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String sltStr = sleeptime.getText().toString().trim();
                int sleeptime = Integer.parseInt(sltStr);
                int runtime = Integer.parseInt(runtimesid.getText().toString().trim());

                EasyLinkParams easylinkPara = new EasyLinkParams();
                easylinkPara.ssid = wifissid.getText().toString().trim();
                easylinkPara.password = wifipsw.getText().toString().trim();
                easylinkPara.runSecond = runtime;
                easylinkPara.sleeptime = sleeptime;
                easylinkPara.extraData = extrainfo.getText().toString().trim();
                easylinkPara.rc4key = rc4key.getText().toString().trim();

                elink.startEasyLink(easylinkPara, new EasyLinkCallBack() {

                    @Override
                    public void onSuccess(int code, String message) {
                        Log.d(TAG, code + message);

                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = "code =  " + code + ", message = " + message;
                        myhandler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Log.d(TAG, code + message);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = "code =  " + code + ", message = " + message;
                        myhandler.sendMessage(msg);
                    }
                });
            }
        });

        stopsearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                elink.stopEasyLink(new EasyLinkCallBack() {

                    @Override
                    public void onSuccess(int code, String message) {
                        Log.d(TAG, code + message);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = "code =  " + code + ", message = " + message;
                        myhandler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Log.d(TAG, code + message);
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = "code =  " + code + ", message = " + message;
                        myhandler.sendMessage(msg);
                    }
                });
            }
        });

        listenwifichange();
    }

    private void listenwifichange() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                    Log.d(TAG, "---heiheihei---");
                    wifissid.setText(elink.getSSID());
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            logmessage.setText(msg.obj.toString());
        }
    }

}
