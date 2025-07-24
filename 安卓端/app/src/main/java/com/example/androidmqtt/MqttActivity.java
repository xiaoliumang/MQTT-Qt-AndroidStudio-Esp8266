package com.example.androidmqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.content.DialogInterface;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;

import android.os.Message;

import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toast;


import com.example.androidmqtt.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;

import org.eclipse.paho.client.mqttv3.MqttCallback;

import org.eclipse.paho.client.mqttv3.MqttClient;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import org.eclipse.paho.client.mqttv3.MqttException;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import org.json.JSONObject;


import java.util.LinkedList;
import java.util.concurrent.Executors;

import java.util.concurrent.ScheduledExecutorService;

import java.util.concurrent.TimeUnit;



public class MqttActivity extends AppCompatActivity {


    private TextView tvWelcome;
    private String host;// = "tcp://ip地址:1883";
    private String ip;
    private String port;
    private String userName ;//= "admin203";

    private String passWord ;//= "admin203";

    private String mqtt_id="111111";

    private int i = 1;

    private Handler handler;

    private MqttClient client;

    private String mqtt_sub_topic;// = "mtopic/2"; //为了保证你不受到别人的消息  哈哈

    private String mqtt_pub_topic;// ="mtopic/2";

    private MqttConnectOptions options;

    private ScheduledExecutorService scheduler;

    private Button loginbutton;

    private EditText text1;

    private EditText usertext;
    private Button button_o;
    private Button button_d;
    private Button button_l;
    private Button button_r;
    private  LinkedList<Integer> list = new LinkedList<>();
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mqtt);
        tvWelcome = findViewById(R.id.tv_welcome);

        // 获取传递过来的用户名
        String username = getIntent().getStringExtra("IP");
        ip = getIntent().getStringExtra("IP");
        port=getIntent().getStringExtra("PORT");
        userName = getIntent().getStringExtra("USER");
        passWord=getIntent().getStringExtra("PASS");
        mqtt_pub_topic=getIntent().getStringExtra("TOPIC");
        mqtt_sub_topic=getIntent().getStringExtra("TOPIC");
        host="tcp://"+ip+":"+port;
        // 在欢迎页面上显示“欢迎光临，用户名”
        tvWelcome.setText("用户:" + userName+"\n"+"服务器:"+ip);

        text1=(EditText) this.findViewById(R.id.show1);
        //TextView edittext = findViewById(R.id.tv_welcome);
        button_o=(Button)this.findViewById(R.id.bto);
        button_o.setOnClickListener(new MqttActivity.ButtonListener());
        button_d=(Button)this.findViewById(R.id.btd);
        button_d.setOnClickListener(new MqttActivity.ButtonListener());
        button_l=(Button)this.findViewById(R.id.btl);
        button_l.setOnClickListener(new MqttActivity.ButtonListener());
        button_r=(Button)this.findViewById(R.id.btr);
        button_r.setOnClickListener(new MqttActivity.ButtonListener());


        init();



        startReconnect();



        handler = new Handler() {

            @SuppressLint("SetTextIl8n")

            public void handleMessage(Message msg) {

                super.handleMessage(msg);

                switch (msg.what) {

                    case 1: //开机校验更新回传

                        break;

                    case 2: //反馈回转

                        break;

                    case 3: //MQTT收到消息回传

                        text1.setText(text1.getText().toString()+"\n"+msg.obj.toString());
                        text1.setSelection(text1.length());

                        break;

                    case 30: //连接失败

                        Toast.makeText(MqttActivity.this,"连接失败",Toast.LENGTH_SHORT).show();

                        break;

                    case 31: //连接成功

                        Toast.makeText(MqttActivity.this,"通讯成功",Toast.LENGTH_SHORT).show();

                        try {

                            client.subscribe(mqtt_sub_topic,2);

                        } catch (MqttException e) {

                            e.printStackTrace();

                        }

                        publishmessageplus(mqtt_pub_topic,"第一个客户端发送的信息");



                        break;

                    default:

                        break;

                }

            }

        };







    }

    private class ButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
           // text1.setText(v.getId());
           // System.out.println(v.getId());
            if(v.getId()==R.id.bto)
            {

                list.addLast(1);
                if (list.size() > 8) {
                    list.removeFirst(); // 如果链表长度超过8，移除头部元素
                }
                StringBuilder sb = new StringBuilder();
                for (Integer num : list) {
                    sb.append(num);
                }
                if(sb.toString().equals("44332211"))
                {
                    publishmessageplus(mqtt_pub_topic,"off");
                }
                publishmessageplus(mqtt_pub_topic,"on");
            }
            if(v.getId()==R.id.btd)
            {
                list.addLast(2);
                if (list.size() > 8) {
                    list.removeFirst(); // 如果链表长度超过8，移除头部元素
                }
                publishmessageplus(mqtt_pub_topic,"down");
            }
            if(v.getId()==R.id.btl)
            {
                list.addLast(3);
                if (list.size() > 8) {
                    list.removeFirst(); // 如果链表长度超过8，移除头部元素
                }
                publishmessageplus(mqtt_pub_topic,"left");
            }
            if(v.getId()==R.id.btr)
            {
                list.addLast(4);
                if (list.size() > 8) {
                    list.removeFirst(); // 如果链表长度超过8，移除头部元素
                }
                StringBuilder sb = new StringBuilder();
                for (Integer num : list) {
                    sb.append(num);
                }
                if(sb.toString().equals("11223344"))
                {publishmessageplus(mqtt_pub_topic,"break");}
                publishmessageplus(mqtt_pub_topic,"right");
            }
        }
    }

    private void init() {

        try {

            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存

            client = new MqttClient(host, mqtt_id,

                    new MemoryPersistence());

            //MQTT的连接设置

            options = new MqttConnectOptions();

            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接

            options.setCleanSession(true);

            //设置连接的用户名

            options.setUserName(userName);

            //设置连接的密码

            options.setPassword(passWord.toCharArray());

            // 设置超时时间 单位为秒

            options.setConnectionTimeout(10);

            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制

            options.setKeepAliveInterval(20);

            //设置回调

            client.setCallback(new MqttCallback() {

                @Override

                public void connectionLost(Throwable cause) {

                    //连接丢失后，一般在这里面进行重连

                    System.out.println("connectionLost----------");

                }

                @Override

                public void deliveryComplete(IMqttDeliveryToken token) {

                    //publish后会执行到这里

                    System.out.println("deliveryComplete---------"

                            + token.isComplete());

                }

                @Override

                public void messageArrived(String topicName, MqttMessage message)

                        throws Exception {

                    //subscribe后得到的消息会执行到这里面

                    System.out.println("messageArrived----------");

                    Message msg = new Message();

                    msg.what = 3;

                    msg.obj = "操作记录" + "---" + message.toString();

                    handler.sendMessage(msg);

                }

            });

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    private void Mqtt_connect() {

        new Thread(new Runnable() {

            @Override

            public void run() {

                try {

                    if (!(client.isConnected())){

                        client.connect(options);

                        Message msg = new Message();

                        msg.what=31;

                        handler.sendMessage(msg);

                    }



                } catch (Exception e) {

                    e.printStackTrace();

                    Message msg = new Message();

                    msg.what = 30;

                    handler.sendMessage(msg);

                }

            }

        }).start();

    }

    private void startReconnect() {

        scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(new Runnable() {



            @Override

            public void run() {

                if (!client.isConnected()) {

                    Mqtt_connect();

                }

            }

        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);

    }

    private void publishmessageplus(String topic,String message2)
    {

        if (client == null || !client.isConnected()) {

            return;

        }

        MqttMessage message = new MqttMessage();

        message.setPayload(message2.getBytes());

        try {

            client.publish(topic,message);

        } catch (MqttException e) {



            e.printStackTrace();

        }

    }
    @Override
    public void onBackPressed() {
        try {
            client.close();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
      //  finish();
    }
}