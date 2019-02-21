/*
 *  Copyright (c) 2014-2019 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.kumuluz.ee.amqp.rabbitmq.utils.consumer;

import com.kumuluz.ee.amqp.common.annotations.AMQPConsumer;
import com.kumuluz.ee.amqp.common.utils.AnnotatedMethod;
import com.kumuluz.ee.amqp.common.utils.ConsumerUtilInitializer;
import com.kumuluz.ee.amqp.common.utils.SerializationUtil;
import com.kumuluz.ee.amqp.rabbitmq.utils.other.RabbitConnection;
import com.rabbitmq.client.*;

import javax.annotation.Priority;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * A class which initializes consumer methods
 *
 * @author Blaž Mrak
 * @since 1.0.0
 */

public class ConsumerInitializer implements ConsumerUtilInitializer {

    private static final Logger log = Logger.getLogger(ConsumerInitializer.class.getName());

    public void after(@Observes @Priority(2600) AfterDeploymentValidation adv, BeanManager bm) {
        for (AnnotatedMethod inst : methodList) {
            log.info("Found method " + inst.getMethod().getName() + " in class " + inst.getMethod().getDeclaringClass());
        }

        if (methodList.size() > 0) {
            for (AnnotatedMethod<AMQPConsumer> inst : methodList) {
                AMQPConsumer annotation = inst.getAnnotation();
                Method method = inst.getMethod();

                String host = annotation.host();
                String[] key = annotation.key();
                String exchange = annotation.exchange();
                boolean autoAck = annotation.autoAck();
                int prefetch = annotation.prefetch();

                Connection connection = RabbitConnection.getConnection(host);
                Channel channel = null;
                String queueName = null;

                try {
                    channel = connection.createChannel();
                    channel.basicQos(prefetch);
                } catch (IOException e) {
                    log.severe("Channel could not be created: " + e.getLocalizedMessage());
                }

                if(!exchange.equals("")){
                    try {
                        queueName = channel.queueDeclare().getQueue();
                    } catch (IOException e) {
                        log.severe("Queue could not be created for exchange " + exchange + ": " + e.getLocalizedMessage());
                    }

                    for(int i = 0; i < key.length; i++){
                        try {
                            channel.queueBind(queueName, exchange, key[i]);
                        } catch (IOException e) {
                            log.severe("Queue could not be bound to exchange " + exchange + " with key " + key[i] + ": " + e.getLocalizedMessage());
                        }
                    }
                }

                /*DeliverCallback deliverCallback = (consumerTag, message) -> {
                    Object body = null;
                    try {
                        if(message.getProperties().getContentType().equals("text/plain")){
                            body = new String(message.getBody(), "UTF-8");
                        } else {
                            body = SerializationUtil.getInstance().deserialize(message.getBody());
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    Object[] args = new Object[]{consumerTag, message.getEnvelope(), message.getProperties(), body};

                    try {
                        method.invoke(inst.getBean().getBeanClass().getDeclaredConstructor().newInstance(), args);
                    } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                };*/

                int parameterCount = method.getParameterCount();
                Class[] params = method.getParameterTypes();

                //If there are two parameters
                if(parameterCount == 2){
                    //If the second parameter is not MessageInfo
                    if(!params[1].getClass().equals(MessageInfo.class)){
                        log.severe("Second parameter in method " + method.getName() + " must be MessageInfo");
                    }
                }

                //Else
                else if(parameterCount == 0){
                    throw new IllegalArgumentException("There must be at least 1 parameter in the method " + method.getName());
                } else if(parameterCount > 2){
                    log.severe("There must be at most 2 parameters in the method " + method.getName());
                }

                Consumer consumer = new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                            throws IOException {
                        Object[] args = null;
                        try {
                            args = new Object[parameterCount];

                            //Create a message
                            //Check if the method expects a string
                            if(params[0].equals(String.class)){
                                try{
                                    args[0] = params[0].cast(SerializationUtil.deserialize(body));
                                } catch(Exception e){
                                    args[0] = new String(body, "UTF-8");
                                }
                            } else {
                                args[0] = params[0].cast(SerializationUtil.deserialize(body));
                            }

                            //If there are two parameters
                            if(parameterCount == 2){
                                MessageInfo messageInfo = new MessageInfo();
                                messageInfo.setChannel(this.getChannel());
                                messageInfo.setConsumerTag(consumerTag);
                                messageInfo.setEnvelope(envelope);
                                messageInfo.setProperties(properties);

                                args[1] = messageInfo;
                            }
                        } catch (ClassNotFoundException e) {
                            log.severe(e.getLocalizedMessage());
                        }

                        Object instance = bm.getReference(inst.getBean(), method.getDeclaringClass(), bm.createCreationalContext(inst.getBean()));

                        try {
                            method.invoke(instance, args);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            log.severe("Method could not be invoked: " + e.getLocalizedMessage());
                        }
                    }
                };

                if(!exchange.equals("")){
                    try {
                        channel.basicConsume(queueName, autoAck, consumer);
                    } catch (IOException e) {
                        log.severe("Channel could not consume: " + e.getLocalizedMessage());
                    }
                } else {
                    for(int i = 0; i < key.length; i++){
                        try {
                            channel.basicConsume(key[i], autoAck, consumer);
                        } catch (IOException e) {
                            log.severe("Channel could not consume: " + e.getLocalizedMessage());
                    }
                    }
                }

            }
        }
    }
}
