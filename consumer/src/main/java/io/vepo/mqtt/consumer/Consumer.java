package io.vepo.mqtt.consumer;

import java.util.concurrent.CountDownLatch;

import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Consumer {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("OK");
        String subTopic = "testtopic/1";
        int qos = 0;
        String broker = "tcp://" + System.getenv("MQTT_HOST") + ":1883";
        String clientId = "vepo_consumer_test";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient client = new MqttClient(broker, clientId, persistence);

            // MQTT connection option
            MqttConnectOptions connOpts = new MqttConnectOptions();
            // connOpts.setUserName("emqx_test");
            // connOpts.setPassword("emqx_test_password".toCharArray());
            // retain session
            connOpts.setCleanSession(true);
            connOpts.setKeepAliveInterval(60);

            // set callback
            client.setCallback(new OnMessageCallback());

            // establish a connection
            System.out.println("Connecting to broker: " + broker);
            client.connect(connOpts);
            System.out.println("Connected");
            // Subscribe
            System.out.println("Subscribing to " + subTopic);
            client.subscribe(subTopic, qos, (topic, message) -> System.out.println("Topic: " + topic + " message: " + message));

            CountDownLatch latch = new CountDownLatch(1);
            Runtime.getRuntime().addShutdownHook(new Thread(){
                public void run() {
                    latch.countDown();
                }
            });
            latch.await();
            client.disconnect();
            System.out.println("Disconnected");
            client.close();
            System.exit(0);
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }
}