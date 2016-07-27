package io.mxchip.alone;

import android.support.v7.app.AppCompatActivity;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import io.fogcloud.fog_mdns.api.MDNS;
import io.fogcloud.fog_mdns.helper.SearchDeviceCallBack;

/**
 * Created by Sin on 2016/07/27.
 * Email:88635653@qq.com
 */
public class MDNSActivity extends AppCompatActivity {
    private EditText mdnsserv;
    private Button startmdns;
    private Button stopmdns;
    private TextView showdev;

    private MDNS mdns;

    private MyHandler myhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mdns);

        mdns = new MDNS(MDNSActivity.this);

        mdnsserv = (EditText) findViewById(R.id.mdnsserv);
        startmdns = (Button) findViewById(R.id.startmdns);
        stopmdns = (Button) findViewById(R.id.stopmdns);
        showdev = (TextView) findViewById(R.id.showdev);

        myhandler = new MyHandler();

        startmdns.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String serviceInfo = mdnsserv.getText().toString();

                Message msg = new Message();
                msg.what = 1;
                msg.obj = "\r\n 正在打开mDNS";
                myhandler.sendMessage(msg);

                mdns.startSearchDevices(serviceInfo,
                        new SearchDeviceCallBack() {
                            @Override
                            public void onDevicesFind(JSONArray deviceStatus) {
                                if (!deviceStatus.equals("")) {
                                    Log.d("---mdns---", deviceStatus.toString());

                                    Message msg = new Message();
                                    msg.what = 2;
                                    msg.obj = updateView(deviceStatus);
                                    myhandler.sendMessage(msg);
                                }
                            }

                            @Override
                            public void onFailure(int code, String message) {
                                Message msg = new Message();
                                msg.what = 1;
                                msg.obj = message.toString() + "\r\n";
                                myhandler.sendMessage(msg);
                            }

                            @Override
                            public void onSuccess(String message) {
                                Message msg = new Message();
                                msg.what = 1;
                                msg.obj = message.toString() + "\r\n";
                                myhandler.sendMessage(msg);
                            }
                        });
            }
        });
        stopmdns.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mdns.stopSearchDevices(new SearchDeviceCallBack() {
                    public void onSuccess(String message) {
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = message + "\r\n";
                        myhandler.sendMessage(msg);
                    };

                    @Override
                    public void onFailure(int code, String message) {
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = message.toString() + "\r\n";
                        myhandler.sendMessage(msg);
                    }
                });
            }
        });
    }

    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2)
                showdev.setText("");
            showdev.append(msg.obj.toString());
        }
    }

    private String updateView(JSONArray jsonArray) {
        JSONObject temp;
        String devlist = "";
        for (int i = 0; i < jsonArray.length(); i++) {
            devlist += "---------\r\n" + (i + 1) + "\r\n---------\r\n";
            try {
                temp = (JSONObject) jsonArray.get(i);
                Iterator it = temp.keys();
                String key = "";
                while (it.hasNext()) {
                    key = it.next().toString();
                    devlist += key + " ： " + temp.getString(key) + "\r\n";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return devlist;
    }
}
