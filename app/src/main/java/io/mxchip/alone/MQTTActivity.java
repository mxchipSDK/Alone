package io.mxchip.alone;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import io.fogcloud.fog_mqtt.api.MQTT;
import io.fogcloud.fog_mqtt.helper.ListenDeviceCallBack;
import io.fogcloud.fog_mqtt.helper.ListenDeviceParams;

/**
 * Created by Sin on 2016/07/27.
 * Email:88635653@qq.com
 */
public class MQTTActivity extends AppCompatActivity implements View.OnClickListener {
    String LOG_TAG = "---activity---";

    private Button startmqtt, stopmqtt, publish, addsubscrib, unsubscrib;
    private TextView logview;

    private MQTT mqttapi;

    private Context ctx;

    String txtin = null;
    private MyHandler myhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mqtt);

        ctx = MQTTActivity.this;
        mqttapi = new MQTT(ctx);
        myhandler = new MyHandler();

        initView();
        initClick();
    }

    private LinearLayout configll;
    private EditText hostet;
    private EditText portet;
    private EditText userNameet;
    private EditText passWordet;
    private EditText topicet;
    private EditText clientIDet;
    private EditText addtopicet;
    private EditText sendtopicet;
    private EditText commandet;

    private void initView() {
        startmqtt = (Button) findViewById(R.id.startmqtt);
        stopmqtt = (Button) findViewById(R.id.stopmqtt);
        publish = (Button) findViewById(R.id.publish);
        addsubscrib = (Button) findViewById(R.id.addsubscrib);
        unsubscrib = (Button) findViewById(R.id.unsubscrib);
        logview = (TextView) findViewById(R.id.logview);

        configll = (LinearLayout) findViewById(R.id.configll);

        hostet = (EditText) findViewById(R.id.hostet);
        portet = (EditText) findViewById(R.id.portet);
        userNameet = (EditText) findViewById(R.id.userNameet);
        passWordet = (EditText) findViewById(R.id.passWordet);
        clientIDet = (EditText) findViewById(R.id.clientIDet);
        topicet = (EditText) findViewById(R.id.topicet);
        addtopicet = (EditText) findViewById(R.id.addtopicet);
        sendtopicet = (EditText) findViewById(R.id.sendtopicet);
        commandet = (EditText) findViewById(R.id.commandet);
    }

    private void initClick() {
        startmqtt.setOnClickListener(this);
        stopmqtt.setOnClickListener(this);
        publish.setOnClickListener(this);
        addsubscrib.setOnClickListener(this);
        unsubscrib.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startmqtt:
//			new Thread(){
//				public void run() {
//					TLSMqtt();
//				};
//			}.start();

                ListenDeviceParams ldp = new ListenDeviceParams();
                ldp.host = hostet.getText().toString().trim();
                ldp.port = portet.getText().toString().trim();
                ldp.userName = userNameet.getText().toString().trim();
                ldp.passWord = passWordet.getText().toString().trim();
                ldp.topic = topicet.getText().toString().trim();
                ldp.clientID = clientIDet.getText().toString().trim();
                ldp.isencrypt = false;

                mqttapi.startListenDevice(ldp, new ListenDeviceCallBack() {
                    @Override
                    public void onSuccess(String message) {
                        Log.d("---", message);
                        configll.setVisibility(View.GONE);
                        sendMSGH(message);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Log.d("---", code + " - " + message);
                        sendMSGH(code + " - " + message);
                    }

                    @Override
                    public void onDeviceStatusReceived(int code, String messages) {
                        Log.d("---" + code + "---", messages);
                        sendMSGH(code + messages);
                    }
                });
                break;
            case R.id.stopmqtt:
                mqttapi.stopListenDevice(new ListenDeviceCallBack() {
                    @Override
                    public void onSuccess(String message) {
                        Log.d("---", message);
                        configll.setVisibility(View.VISIBLE);
                        sendMSGH(message);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Log.d("---", code + " - " + message);
                        sendMSGH(code + " - " + message);
                    }
                });
                break;
            case R.id.publish:
                String sendtopic = sendtopicet.getText().toString().trim();
                String command = commandet.getText().toString().trim();
                mqttapi.sendCommand(sendtopic, command, 0, false,
                        new ListenDeviceCallBack() {
                            @Override
                            public void onSuccess(String message) {
                                Log.d("---", message);
                                sendMSGH(message);
                            }

                            @Override
                            public void onFailure(int code, String message) {
                                Log.d("---", code + " - " + message);
                                sendMSGH(code + " - " + message);
                            }
                        });
                break;
            case R.id.addsubscrib:
                String addtopic = addtopicet.getText().toString().trim();
                mqttapi.addDeviceListener(addtopic, 0, new ListenDeviceCallBack() {
                    @Override
                    public void onSuccess(String message) {
                        Log.d("---", message);
                        sendMSGH(message);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Log.d("---", code + " - " + message);
                        sendMSGH(code + " - " + message);
                    }
                });
                break;
            case R.id.unsubscrib:
                String rmtopic = addtopicet.getText().toString().trim();
                mqttapi.removeDeviceListener(rmtopic, new ListenDeviceCallBack() {
                    @Override
                    public void onSuccess(String message) {
                        Log.d("---", message);
                        sendMSGH(message);
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        Log.d("---", code + " - " + message);
                        sendMSGH(code + " - " + message);
                    }
                });
                break;
        }
    }

    private void sendMSGH(String message) {
        Message msg = new Message();
        msg.what = 1;
        msg.obj = message;
        myhandler.sendMessage(msg);
    }

    class MyHandler extends Handler {
        int count = 0;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(count > 10){
                logview.setText("");
                count = 0;
            }
            logview.append(msg.obj.toString() + "\r\n");
            count ++;
        }
    }

    //MQTT¼ÓÃÜÍ¨µÀ
//	private void TLSMqtt(){
//		String HOST = "tcp://api.easylink.io:8443";
//		String username = "admin";
//		String password = "admin";
//		String topic = "d64f517c/c8934691813c/#";
//		String clientID = "clientId-sin2016062852";

//		String HOST = "tcp://v2.fogcloud.io:1883";
//		String username = "a015e6d6-1d6e-11e6-a739-00163e0204c0";
//		String password = "123456";
//		String topic = "d2c/aa2dde14-0b8d-11e6-a739-00163e0204c0/status";
//		String clientID = "a015e6d6-1d6e-11e6-a739-00163e0204c0";
//
//	    MqttClient client;
//		 try {
//			client = new MqttClient(HOST, clientID, new MemoryPersistence());
//			MqttConnectOptions options = new MqttConnectOptions();
//			options.setCleanSession(true);
//			options.setUserName(username);
//			options.setPassword(password.toCharArray());
//			client.setCallback(new MqttCallback() {
//
//				@Override
//				public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
//					Log.d(LOG_TAG, arg1.toString());
//				}
//
//				@Override
//				public void deliveryComplete(IMqttDeliveryToken arg0) {
//					Log.d(LOG_TAG, "Client deliveryComplete");
//				}
//
//				@Override
//				public void connectionLost(Throwable arg0) {
//					Log.d(LOG_TAG, "Client connectionLost");
//				}
//			});
//			client.connect(options);
//			client.subscribe(topic);
//		} catch (MqttException e) {
//			e.printStackTrace();
//		}
//
//		String HOST = "ssl://v2.fogcloud.io:8443";
//		String username = "a015e6d6-1d6e-11e6-a739-00163e0204c0";
//		String password = "123456";
//		String topic = "d2c/aa2dde14-0b8d-11e6-a739-00163e0204c0/status";
//		String clientID = "a015e6d6-1d6e-11e6-a739-00163e0204c0";
//		// ´´½¨SSLÁ¬½Ó
//		try {
//			//´´½¨jkdÃÜÔ¿·ÃÎÊ¿â
//	        TrustManagerFactory tmf=TrustManagerFactory.getInstance("X509");
//			tmf.init((KeyStore)null);
//			TrustManager[] trustManagers = tmf.getTrustManagers();
//
//			//´´½¨TrustManagerFactory,¹ÜÀíÊÚÈ¨Ö¤Êé
//	        SSLContext sslc=SSLContext.getInstance("SSLv3");
//	        // ¹¹ÔìSSL»·¾³£¬Ö¸¶¨SSL°æ±¾Îª3.0£¬Ò²¿ÉÒÔÊ¹ÓÃTLSv1£¬µ«ÊÇSSLv3¸ü¼Ó³£ÓÃ¡£
//	        sslc.init(null,trustManagers,null);
//
//			// ÅäÖÃMQTTÁ¬½Ó
//			MqttConnectOptions options = new MqttConnectOptions();
//			options.setCleanSession(true);
//			options.setUserName(username);
//			options.setPassword(password.toCharArray());
//			options.setSocketFactory(sslc.getSocketFactory());
//
//			MqttClient client = new MqttClient(HOST, clientID, new MemoryPersistence());
//			client.setCallback(new MqttCallback() {
//
//				@Override
//				public void messageArrived(String arg0, MqttMessage arg1) throws Exception {
//					Log.d(LOG_TAG, arg1.toString());
//				}
//
//				@Override
//				public void deliveryComplete(IMqttDeliveryToken arg0) {
//					Log.d(LOG_TAG, "Client deliveryComplete");
//				}
//
//				@Override
//				public void connectionLost(Throwable arg0) {
//					Log.d(LOG_TAG, "Client connectionLost");
//				}
//			});
//
//			// ´´½¨MQTTÁ¬½Ó
//			client.connect(options);
//			client.subscribe(topic);
//
//			// ·¢ËÍÏûÏ¢
////			MqttMessage message = new MqttMessage();
////			message.setPayload("15".getBytes());
////			client.publish(topic, message);
////			client.disconnect();
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (KeyManagementException e) {
//			e.printStackTrace();
//		} catch (KeyStoreException e) {
//			e.printStackTrace();
//		} catch (MqttSecurityException e) {
//			e.printStackTrace();
//		} catch (MqttException e) {
//			e.printStackTrace();
//		}
//
//	}
}
