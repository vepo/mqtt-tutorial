package io.vepo.mqtt.producer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class Producer {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("OK");
        String pubTopic = "testtopic/1";
        int qos = 2;
        String broker = "tcp://" + System.getenv("MQTT_HOST") + ":1883";
        String clientId = "vepo_producer_test";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient client = new MqttClient(broker, clientId, persistence);

            // MQTT connection option
            MqttConnectOptions connOpts = new MqttConnectOptions();
            //connOpts.setUserName("emqx_test");
            //connOpts.setPassword("emqx_test_password".toCharArray());
            // retain session
            connOpts.setCleanSession(true);

            // set callback
            client.setCallback(new OnMessageCallback());

            // establish a connection
            System.out.println("Connecting to broker: " + broker);
            client.connect(connOpts);

            System.out.println("Connected");
            
            AtomicBoolean running = new AtomicBoolean(true);
            Runtime.getRuntime().addShutdownHook(new Thread(){
                @Override
                public void run() {
                    running.set(false);
                }
            });
            AtomicInteger counter = new AtomicInteger(0);
            while (running.get()) { 
                String content = "Hi! number=" + counter.incrementAndGet();
                System.out.println("Publishing message: " + content);

                // Required parameters for message publishing
                MqttMessage message = new MqttMessage(content.getBytes());
                message.setQos(qos);
                client.publish(pubTopic, message);
                System.out.println("Message published");
                Thread.sleep(1000);
            }

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