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

import com.kumuluz.ee.amqp.common.annotations.AMQPChannel;
import com.kumuluz.ee.amqp.rabbitmq.utils.other.RabbitConnection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * A class for injecting a channel
 *
 * @author Blaž Mrak
 * @since 1.0.0
 */
@ApplicationScoped
public class ChannelUtil {

    private static final Logger LOG = Logger.getLogger(ChannelUtil.class.getName());

    @Produces
    @AMQPChannel
    public Channel getChannel(InjectionPoint injectionPoint) {
        String name = injectionPoint.getAnnotated().getAnnotation(AMQPChannel.class).value();
        Connection connection = RabbitConnection.getConnection(name);
        Channel channel = null;

        try {
            channel = connection.createChannel();
        } catch (IOException e) {
            LOG.severe("Could not create channel: " + e.getLocalizedMessage());
        }

        return channel;
    }
}
