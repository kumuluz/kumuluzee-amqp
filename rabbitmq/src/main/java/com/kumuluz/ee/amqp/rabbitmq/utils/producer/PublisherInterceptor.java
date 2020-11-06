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
package com.kumuluz.ee.amqp.rabbitmq.utils.producer;

import com.kumuluz.ee.amqp.common.annotations.AMQPProducer;
import com.kumuluz.ee.amqp.common.utils.SerializationUtil;
import com.kumuluz.ee.amqp.rabbitmq.config.ConfigLoader;
import com.kumuluz.ee.amqp.rabbitmq.utils.other.RabbitConnection;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Interceptor for publishing methods
 *
 * @author Blaž Mrak
 * @since 1.0.0
 */
@Priority(Interceptor.Priority.APPLICATION)
@AMQPProducer
@Interceptor
public class PublisherInterceptor {

    private static final Logger LOG = Logger.getLogger(PublisherInterceptor.class.getName());

    @AroundInvoke
    public Object aroundInvoke(InvocationContext invocationContext) throws Exception {
        Method method = invocationContext.getMethod();
        AMQPProducer annotation = method.getAnnotation(AMQPProducer.class);

        Object result = invocationContext.proceed();

        String host = annotation.host();
        String exchange = annotation.exchange();
        String[] keys = annotation.key();
        String property = annotation.properties();

        if (result instanceof Message) {
            Message message = (Message) result;
            if (message.getHost() != null) {
                host = message.getHost();
            }

            if (message.getExchange() != null) {
                exchange = message.getExchange();
            }

            if (message.getKey() != null) {
                keys = message.getKey();
            }
        }

        AMQP.BasicProperties basicProperties;
        byte[] body;

        Connection connection = RabbitConnection.getConnection(host);
        Channel channel = null;

        try {
            if (connection == null) {
                LOG.severe("Cannot find a connection with name " + host);
            } else {
                channel = connection.createChannel();
            }
        } catch (IOException e) {
            LOG.severe("Could not create a new channel: " + e.getLocalizedMessage());
        }

        //Check if it is an instance of Message
        if (result instanceof Message) {
            Message message = (Message) result;

            //Check if we are sending plain text
            if (message.getBody() instanceof String &&
                    message.getBasicProperties() != null &&
                    message.getBasicProperties().getContentType().equals("text/plain")) {

                body = ((String) message.getBody()).getBytes();
            } else {
                body = SerializationUtil.serialize(message.getBody());
            }

            if (message.getBasicProperties() == null && !property.equals("")) {
                basicProperties = getMessageProperties(property);
            } else {
                basicProperties = message.getBasicProperties();
            }
        } else {
            basicProperties = getMessageProperties(property);

            //Check if we are sending plain text
            if (result instanceof String && "text/plain".equals(basicProperties.getContentType())) {
                body = ((String) result).getBytes();
            } else {
                body = SerializationUtil.serialize(result);
            }
        }

        for (String key : keys) {
            try {
                Objects.requireNonNull(channel).basicPublish(exchange, key, basicProperties, body);
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Could not bind key " + key + " to exchange " + exchange + ".", e);
            }
        }
        if (channel != null) {
            channel.close();
        }

        return result;
    }

    private AMQP.BasicProperties getMessageProperties(String property) {
        AMQP.BasicProperties basicProperties = ConfigLoader.getInstance().getBasicProperties(property);
        if (basicProperties == null) {
            switch (property) {
                case "minimalBasic":
                    return MessageProperties.MINIMAL_BASIC;
                case "basic":
                    return MessageProperties.BASIC;
                case "minimalPersistentBasic":
                    return MessageProperties.MINIMAL_PERSISTENT_BASIC;
                case "textPlain":
                    return MessageProperties.TEXT_PLAIN;
                case "persistentTextPlain":
                    return MessageProperties.PERSISTENT_TEXT_PLAIN;
                case "persistentBasic":
                    return MessageProperties.PERSISTENT_BASIC;
                default:
                    return MessageProperties.MINIMAL_BASIC;
            }
        } else {
            return basicProperties;
        }
    }
}
