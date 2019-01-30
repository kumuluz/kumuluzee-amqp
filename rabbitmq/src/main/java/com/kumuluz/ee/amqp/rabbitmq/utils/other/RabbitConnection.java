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

package com.kumuluz.ee.amqp.rabbitmq.utils.other;

import com.kumuluz.ee.amqp.rabbitmq.config.ConfigLoader;
import com.kumuluz.ee.amqp.rabbitmq.config.HostItem;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

/**
 * A class which handles all connections
 *
 * @author Blaž Mrak
 * @since 1.0.0
 */

@ApplicationScoped
public class RabbitConnection {
    private static Map<String, Connection> connections = new HashMap<>();
    private static Logger log = Logger.getLogger(RabbitConnection.class.getName());

    public static Connection getConnection(String host){
        Connection connection = connections.get(host);
        if(connection == null){
            ConnectionFactory connectionFactory = new ConnectionFactory();
            HostItem hostItem = ConfigLoader.getInstance().getHost(host);
            if(hostItem.getUri() == null && hostItem.getUrl() == null){
                log.warning("You have to set URL or URI for the host " + host);
            } else {
                connectionFactory.setHost(hostItem.getUrl());

                if (hostItem.getAutomaticRecoveryEnabled() != null) {
                    connectionFactory.setAutomaticRecoveryEnabled(hostItem.getAutomaticRecoveryEnabled());
                }
                if (hostItem.getHandshakeTimeout() != null) {
                    connectionFactory.setHandshakeTimeout(hostItem.getHandshakeTimeout());
                }
                if (hostItem.getChannelShouldCheckRpcResponseType() != null) {
                    connectionFactory.setChannelShouldCheckRpcResponseType(hostItem.getChannelShouldCheckRpcResponseType());
                }
                if (hostItem.getEnableHostnameVerification() != null && hostItem.getEnableHostnameVerification()) {
                    connectionFactory.enableHostnameVerification();
                }
                if (hostItem.getTopologyRecoveryEnabled() != null) {
                    connectionFactory.setTopologyRecoveryEnabled(hostItem.getTopologyRecoveryEnabled());
                }
                if (hostItem.getClientProperties() != null) {
                    connectionFactory.setClientProperties(hostItem.getClientProperties());
                }
                if (hostItem.getConnectionTimeout() != null) {
                    connectionFactory.setConnectionTimeout(hostItem.getConnectionTimeout());
                }
                if (hostItem.getNetworkRecoveryInterval() != null) {
                    connectionFactory.setNetworkRecoveryInterval(hostItem.getNetworkRecoveryInterval());
                }
                if (hostItem.getPassword() != null) {
                    connectionFactory.setPassword(hostItem.getPassword());
                }
                if (hostItem.getUsername() != null) {
                    connectionFactory.setUsername(hostItem.getUsername());
                }
                if (hostItem.getRequestedChannelMax() != null) {
                    connectionFactory.setRequestedChannelMax(hostItem.getRequestedChannelMax());
                }
                if (hostItem.getRequestedFrameMax() != null) {
                    connectionFactory.setRequestedChannelMax(hostItem.getRequestedFrameMax());
                }
                if (hostItem.getRequestedHeartbeat() != null) {
                    connectionFactory.setRequestedHeartbeat(hostItem.getRequestedHeartbeat());
                }
                if (hostItem.getVirtualHost() != null) {
                    connectionFactory.setVirtualHost(hostItem.getVirtualHost());
                }
                if (hostItem.getShutdownTimeout() != null) {
                    connectionFactory.setShutdownTimeout(hostItem.getShutdownTimeout());
                }
                if (hostItem.getUri() != null) {
                    try {
                        connectionFactory.setUri(hostItem.getUri());
                    } catch (URISyntaxException | KeyManagementException | NoSuchAlgorithmException e) {
                        log.severe("Could not set URI: " + e.getLocalizedMessage());
                    }
                }
                if (hostItem.getWorkPoolTimeout() != null) {
                    connectionFactory.setWorkPoolTimeout(hostItem.getWorkPoolTimeout());
                }
                if (hostItem.getPort() != null) {
                    connectionFactory.setPort(hostItem.getPort());
                }
                if (hostItem.getChannelRpcTimeout() != null) {
                    connectionFactory.setChannelRpcTimeout(hostItem.getChannelRpcTimeout());
                }

                try {
                    connection = connectionFactory.newConnection();
                    connections.put(host, connection);
                } catch (IOException | TimeoutException e) {
                    log.severe("Could not create connection: " + e.getLocalizedMessage());
                }
            }
        }
        return connection;
    }

    public static void setConnection(Map<String, Connection> connection){
        connections.putAll(connection);
        log.info("Set connection " + connection.toString());
    }

    public static void closeConnection(String name){
        try {
            Objects.requireNonNull(connections.get(name)).close();
            connections.remove(name);
            log.info("Connection " + name + " closed.");
        } catch (IOException e) {
            log.severe("Could not close connection: " + e.getLocalizedMessage());
        } catch (NullPointerException e){
            log.severe("Host " + name + " does not exist.");
        }
    }
}
