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

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.MessageProperties;

import javax.inject.Inject;
import java.util.*;
import java.util.logging.Logger;

/**
 * Loads rabbitmq configuration from config.yml
 *
 * @author Blaž Mrak
 * @since 1.0.0
 */

public class ConfigLoader {

    private static ConfigLoader instance;

    public HostItem getHost(String host){
        String prefix = "kumuluzee.amqp.rabbitmq.hosts";
        HostItem hostItem = new HostItem();
        ConfigurationUtil config = ConfigurationUtil.getInstance();
        Optional<Integer> size = config.getListSize(prefix);
        if(size.isPresent()){
            for(int i = 0; i < size.get(); i++){
                String name = config.get(prefix + "[" + i + "]" + ".name").get();
                if(name.equals(host)) {
                    hostItem.setName(name);
                    hostItem.setUrl(config.get(prefix + "[" + i + "]" + ".url").orElse(null));
                    hostItem.setAutomaticRecoveryEnabled(config.getBoolean(prefix + "[" + i + "]" + ".automaticRecoveryEnabled").orElse(null));
                    hostItem.setChannelRpcTimeout(config.getInteger(prefix + "[" + i + "]" + ".channelRpcTimeout").orElse(null));
                    hostItem.setChannelShouldCheckRpcResponseType(config.getBoolean(prefix + "[" + i + "]" + ".channelShouldCheckRpcResponseType").orElse(null));
                    hostItem.setConnectionTimeout(config.getInteger(prefix + "[" + i + "]" + ".connectionTimeout").orElse(null));
                    hostItem.setEnableHostnameVerification(config.getBoolean(prefix + "[" + i + "]" + ".enableHostnameVerification").orElse(null));
                    hostItem.setHandshakeTimeout(config.getInteger(prefix + "[" + i + "]" + ".handshakeTimeout").orElse(null));
                    hostItem.setNetworkRecoveryInterval(config.getInteger(prefix + "[" + i + "]" + ".networkRecoveryInterval").orElse(null));
                    hostItem.setPassword(config.get(prefix + "[" + i + "]" + ".password").orElse(null));
                    hostItem.setUsername(config.get(prefix + "[" + i + "]" + ".username").orElse(null));
                    hostItem.setPort(config.getInteger(prefix + "[" + i + "]" + ".port").orElse(null));
                    hostItem.setRequestedChannelMax(config.getInteger(prefix + "[" + i + "]" + ".requestedChannelMax").orElse(null));
                    hostItem.setRequestedFrameMax(config.getInteger(prefix + "[" + i + "]" + ".requestedFrameMax").orElse(null));
                    hostItem.setRequestedHeartbeat(config.getInteger(prefix + "[" + i + "]" + ".requestedHeartbeat").orElse(null));
                    hostItem.setShutdownTimeout(config.getInteger(prefix + "[" + i + "]" + ".shutdownTimeout").orElse(null));
                    hostItem.setTopologyRecoveryEnabled(config.getBoolean(prefix + "[" + i + "]" + ".topologyRecoveryEnabled").orElse(null));
                    hostItem.setUri(config.get(prefix + "[" + i + "]" + ".uri").orElse(null));
                    hostItem.setVirtualHost(config.get(prefix + "[" + i + "]" + ".virtualHost").orElse(null));
                    hostItem.setWorkPoolTimeout(config.getInteger(prefix + "[" + i + "]" + ".automaticRecoveryEnabled").orElse(null));
                    hostItem.setClientProperties(getMap(prefix + "[" + i + "]" + ".clientProperties"));
                    return hostItem;
                }
            }
        }
        return null;
    }

    public Map<String, Object> getMap(String path){
        ConfigurationUtil config = ConfigurationUtil.getInstance();
        Map<String, Object> map = new HashMap<>();
        Optional<List<String>> keys = config.getMapKeys(path);
        if(keys.isPresent()){
            List<String> testKeys = keys.get();
            for(String key : testKeys){
                switch(config.getType(path + "." + key).get()){
                    case BOOLEAN: map.put(key, config.getBoolean(path + "." + key).get());
                    case INTEGER: map.put(key, config.getInteger(path + "." + key).get());
                    case STRING: map.put(key, config.get(path + "." + key).get());
                    case DOUBLE: map.put(key, config.getDouble(path + "." + key).get());
                    case FLOAT: map.put(key, config.getFloat(path + "." + key).get());
                    case MAP: map.put(key, getMap(path + "." + key));
                    case LONG: map.put(key, config.getLong(path + "." + key).get());
                    case LIST: map.put(key, "list");
                    default: map.put(key, null);
                }
            }
            return map;
        }
        return null;
    }

    public List<Exchange> getExchanges(int host){
        List<Exchange> exchanges = new ArrayList<>();
        String prefix = "kumuluzee.amqp.rabbitmq.hosts";
        ConfigurationUtil config = ConfigurationUtil.getInstance();

        Optional<Integer> exchangesSize = config.getListSize(prefix + "[" + host + "]" + ".exchanges");
        if(exchangesSize.isPresent()){
            for(int j = 0; j < exchangesSize.get(); j++){
                Exchange newExchange = new Exchange();
                newExchange.setName(config.get(prefix + "[" + host + "]" + ".exchanges[" + j + "].name").get());
                newExchange.setType(config.get(prefix + "[" + host + "]" + ".exchanges[" + j + "].type").orElse("fanout"));
                newExchange.setDurable(config.getBoolean(prefix + "[" + host + "]" + ".exchanges[" + j + "].durable").orElse(false));
                newExchange.setAutoDelete(config.getBoolean(prefix + "[" + host + "]" + ".exchanges[" + j + "].autoDelete").orElse(false));
                newExchange.setArguments(getMap(prefix + "[" + host + "]" + ".exchanges[" + j + "].arguments"));
                exchanges.add(newExchange);
            }
        }
        return exchanges;
    }

    public List<Queue> getQueues(int host){
        List<Queue> queues = new ArrayList<>();
        String prefix = "kumuluzee.amqp.rabbitmq.hosts";
        ConfigurationUtil config = ConfigurationUtil.getInstance();

        Optional<Integer> queuesSize = config.getListSize(prefix + "[" + host + "]" + ".queues");
        if(queuesSize.isPresent()){
            for(int j = 0; j < queuesSize.get(); j++){
                Queue newQueue = new Queue();
                newQueue.setName(config.get(prefix + "[" + host + "]" + ".queues[" + j + "].name").get());
                newQueue.setExclusive(config.getBoolean(prefix + "[" + host + "]" + ".queues[" + j + "].exclusive").orElse(false));
                newQueue.setDurable(config.getBoolean(prefix + "[" + host + "]" + ".queues[" + j + "].durable").orElse(false));
                newQueue.setAutoDelete(config.getBoolean(prefix + "[" + host + "]" + ".queues[" + j + "].autoDelete").orElse(false));
                newQueue.setArguments(getMap(prefix + "[" + host + "]" + ".queues[" + j + "].arguments"));
                queues.add(newQueue);
            }
        }
        return queues;
    }

    public List<HostItem> getHostsData() {
        List<HostItem> hosts = new ArrayList<>();
        ConfigurationUtil config = ConfigurationUtil.getInstance();
        String prefix = "kumuluzee.amqp.rabbitmq.hosts";
        Optional<Integer> hostsSize = ConfigurationUtil.getInstance().getListSize(prefix);
        if (hostsSize.isPresent()) {
            for (int i = 0; i < hostsSize.get(); i++) {
                HostItem newHost = getHost(config.get(prefix + "[" + i + "]" + ".name").get());
                newHost.setExchanges(getExchanges(i));
                newHost.setQueues(getQueues(i));
                hosts.add(newHost);
            }
        }
        return hosts;
    }

    public BasicProperties getBasicProperties(String properties){
        if(properties == null){
            return null;
        }

        ConfigurationUtil config = ConfigurationUtil.getInstance();
        String prefix = "kumuluzee.amqp.rabbitmq.properties";
        Optional<Integer> propertiesSize = config.getListSize(prefix);
        BasicProperties basicProperties = null;
        if(propertiesSize.isPresent()){
            for(int i = 0; i < propertiesSize.get(); i++){
                if(properties.equals(config.get(prefix + "[" + i + "].name").get())){
                    String propertyPath = prefix + "[" + i + "]";
                    String contentType = config.get(propertyPath + ".contentType").orElse(null);
                    String contentEncoding = config.get(propertyPath + ".contentEncoding").orElse(null);
                    Map<String, Object> headers = new HashMap<>();
                    Optional<List<String>> headerKeys = config.getMapKeys(propertyPath + ".headers");
                    if(headerKeys.isPresent()){
                        List<String> keys = headerKeys.get();
                        for(String key : keys){
                            headers.put(key, config.get(propertyPath + ".headers." + key).get());
                        }
                    }
                    Integer deliveryMode = config.getInteger(propertyPath + ".deliveryMode").orElse(null);
                    Integer priority = config.getInteger(propertyPath + ".priority").orElse(null);
                    String correlationId = config.get(propertyPath + ".corelationId").orElse(null);
                    String replyTo = config.get(propertyPath + ".replyTo").orElse(null);
                    String expiration = config.get(propertyPath + ".expiration").orElse(null);
                    String messageId = config.get(propertyPath + ".messageId").orElse(null);
                    Boolean timestamp = config.getBoolean(propertyPath + ".timestamp").orElse(false);
                    String type = config.get(propertyPath + ".type").orElse(null);
                    String userId = config.get(propertyPath + ".userId").orElse(null);
                    String appId = config.get(propertyPath + ".appId").orElse(null);
                    String clusterId = config.get(propertyPath + ".clusterId").orElse(null);

                    BasicProperties.Builder builder = new BasicProperties.Builder();
                    basicProperties = builder
                            .appId(appId)
                            .clusterId(clusterId)
                            .contentEncoding(contentEncoding)
                            .correlationId(correlationId)
                            .contentType(contentType)
                            .deliveryMode(deliveryMode)
                            .expiration(expiration)
                            .headers(headers)
                            .messageId(messageId)
                            .priority(priority)
                            .replyTo(replyTo)
                            .timestamp(((timestamp) ? new Date(System.currentTimeMillis()) : null))
                            .type(type)
                            .userId(userId)
                            .build();
                    return basicProperties;
                }
            }
        }
        return basicProperties;
    }

    public static ConfigLoader getInstance(){
        if(instance == null){
            instance = new ConfigLoader();
        }
        return instance;
    }
}
