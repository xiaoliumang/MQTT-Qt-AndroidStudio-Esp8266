package com.example.androidmqtt;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.ScheduledExecutorService;

public class LoginActivity extends AppCompatActivity {
    private EditText iptext;
    private EditText tvWelcome;
    private EditText porttext;
    private EditText usertext;
    private EditText passtext;

    private EditText topictext;
    private Button loginbutton;
    private String host;// = "tcp://ip地址:1883";
    private String port;
    private String userName ;//= "";

    private String passWord ;//= "admin203";

    private String mqtt_id="2222222";
    private MqttClient client;

    private MqttConnectOptions options;

    private int i = 1;

    private Handler handler;

    private String mqtt_sub_topic;// = "mtopic/2"; //为了保证你不受到别人的消息  哈哈

    private String mqtt_pub_topic;//="mtopic/2";


    private ScheduledExecutorService scheduler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        iptext=(EditText)this.findViewById(R.id.edip);
        porttext=(EditText)this.findViewById(R.id.eddk);
        usertext=(EditText)this.findViewById(R.id.eduser);
        passtext=(EditText)this.findViewById(R.id.edps);
        topictext=(EditText)this.findViewById(R.id.edzt);
        loginbutton=(Button)this.findViewById(R.id.bt);
        loginbutton.setOnClickListener(new ButtonListener());
    }
    private class ButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v){
            userName=usertext.getText().toString();
            passWord=passtext.getText().toString();
            host=iptext.getText().toString();
            port=porttext.getText().toString();
            mqtt_sub_topic=topictext.getText().toString();
            mqtt_pub_topic=topictext.getText().toString();
        //    host=iptext.getText().toString();
            init();

            try {
                if (!(client.isConnected())){
                    client.connect(options);

                }

            } catch (Exception e) {
                e.printStackTrace();

            }

            if (userName.equals("")||passWord.equals("")||host.equals("")||port.equals("")||mqtt_pub_topic.equals("")){
                Toast.makeText(LoginActivity.this,"以上信息不能为空！",Toast.LENGTH_SHORT).show();
            }
            else if (client.isConnected()){
                try {
                        client.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(LoginActivity.this,"连接成功",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MqttActivity.class);
                intent.putExtra("IP", host);
                intent.putExtra("PORT", port);
                intent.putExtra("USER", userName);
                intent.putExtra("PASS", passWord);
                intent.putExtra("TOPIC",mqtt_sub_topic);
                startActivity(intent);
            }
            else{
                Toast.makeText(LoginActivity.this,"连接失败",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void init() {

        try {

            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存

            client = new MqttClient("tcp://"+host+":"+port, mqtt_id,

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

                    msg.obj = topicName + "---" + message.toString();

                    handler.sendMessage(msg);

                }

            });

        } catch (Exception e) {

            e.printStackTrace();

        }

    }


}