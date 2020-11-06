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
package com.kumuluz.ee.amqp.rabbitmq.config;

import com.kumuluz.ee.amqp.rabbitmq.utils.other.RabbitConnection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class which initializes values from config.yml on the broker
 *
 * @author Blaž Mrak
 * @since 1.0.0
 */
public class HostInitializer {

    private static final Logger LOG = Logger.getLogger(HostInitializer.class.getName());
    private static HostInitializer instance;

    public static HostInitializer getInstance() {
        if (instance == null) {
            instance = new HostInitializer();
        }
        return instance;
    }

    public void initializeRabbitmq() {
        List<HostItem> hosts = ConfigLoader.getInstance().getHostsData();
        for (HostItem host : hosts) {
            Connection connection = RabbitConnection.getConnection(host.getName());
            Channel channel;

            try {
                channel = connection.createChannel();
            } catch (IOException e) {
                throw new IllegalStateException("Could not create channel.", e);
            }

            for (Exchange exchange : host.getExchanges()) {
                try {
                    channel.exchangeDeclare(exchange.getName(), exchange.getType(), exchange.isDurable(), exchange.isAutoDelete(), exchange.getArguments());
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "Could not create an exchange: ", e);
                }
            }

            for (Queue queue : host.getQueues()) {
                try {
                    channel.queueDeclare(queue.getName(), queue.isDurable(), queue.isExclusive(), queue.isAutoDelete(), queue.getArguments());
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, "Could not create a queue: ", e);
                }
            }
        }
    }
}
