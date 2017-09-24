package org.ea.service;

import java.lang.Exception;
import java.io.IOException;

import com.rabbitmq.client.*;

public class App
{
    private final static String OUTPUT_QUEUE_NAME = "myoutput";

    public static Channel connect() throws Exception {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("172.17.0.2");
      factory.setUsername("guest");
      factory.setPassword("guest");
      Connection connection = factory.newConnection();
      return connection.createChannel();
    }

    public static void main( String[] args ) {
      final Channel channel;
      try {
        channel = connect();

        channel.queueDeclare(OUTPUT_QUEUE_NAME, false, false, false, null);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
          @Override
          public void handleDelivery(
              String consumerTag,
              Envelope envelope,
              AMQP.BasicProperties properties,
              byte[] body
              ) throws IOException {
            String message = new String(body, "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            channel.basicAck(envelope.getDeliveryTag(), false);
          }
        };
        channel.basicConsume(OUTPUT_QUEUE_NAME, false, consumer);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
}
