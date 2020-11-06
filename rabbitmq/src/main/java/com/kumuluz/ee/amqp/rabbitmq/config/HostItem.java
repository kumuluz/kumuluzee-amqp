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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Blaž Mrak
 * @since 1.0.0
 */
public class HostItem {
    private String name;
    private String url;
    private String username;
    private String password;
    private Integer port;
    private Boolean automaticRecoveryEnabled;
    private Integer channelRpcTimeout;
    private Boolean enableHostnameVerification;
    private Boolean channelShouldCheckRpcResponseType;
    private Map<String, Object> clientProperties;
    private Integer connectionTimeout;
    private Integer handshakeTimeout;
    private Integer networkRecoveryInterval;
    private Integer requestedChannelMax;
    private Integer requestedFrameMax;
    private Integer requestedHeartbeat;
    private Integer shutdownTimeout;
    private Boolean topologyRecoveryEnabled;
    private String uri;
    private String virtualHost;
    private Integer workPoolTimeout;

    private List<Exchange> exchanges = new ArrayList<>();
    private List<Queue> queues = new ArrayList<>();

    public HostItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Exchange> getExchanges() {
        return exchanges;
    }

    public void setExchanges(List<Exchange> exchanges) {
        this.exchanges = exchanges;
    }

    public List<Queue> getQueues() {
        return queues;
    }

    public void setQueues(List<Queue> queues) {
        this.queues = queues;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean getAutomaticRecoveryEnabled() {
        return automaticRecoveryEnabled;
    }

    public void setAutomaticRecoveryEnabled(Boolean automaticRecoveryEnabled) {
        this.automaticRecoveryEnabled = automaticRecoveryEnabled;
    }

    public Integer getChannelRpcTimeout() {
        return channelRpcTimeout;
    }

    public void setChannelRpcTimeout(Integer channelRpcTimeout) {
        this.channelRpcTimeout = channelRpcTimeout;
    }

    public Boolean getEnableHostnameVerification() {
        return enableHostnameVerification;
    }

    public void setEnableHostnameVerification(Boolean enableHostnameVerification) {
        this.enableHostnameVerification = enableHostnameVerification;
    }

    public Boolean getChannelShouldCheckRpcResponseType() {
        return channelShouldCheckRpcResponseType;
    }

    public void setChannelShouldCheckRpcResponseType(Boolean channelShouldCheckRpcResponseType) {
        this.channelShouldCheckRpcResponseType = channelShouldCheckRpcResponseType;
    }

    public Map<String, Object> getClientProperties() {
        return clientProperties;
    }

    public void setClientProperties(Map<String, Object> clientProperties) {
        this.clientProperties = clientProperties;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getHandshakeTimeout() {
        return handshakeTimeout;
    }

    public void setHandshakeTimeout(Integer handshakeTimeout) {
        this.handshakeTimeout = handshakeTimeout;
    }

    public Integer getNetworkRecoveryInterval() {
        return networkRecoveryInterval;
    }

    public void setNetworkRecoveryInterval(Integer networkRecoveryInterval) {
        this.networkRecoveryInterval = networkRecoveryInterval;
    }

    public Integer getRequestedChannelMax() {
        return requestedChannelMax;
    }

    public void setRequestedChannelMax(Integer requestedChannelMax) {
        this.requestedChannelMax = requestedChannelMax;
    }

    public Integer getRequestedFrameMax() {
        return requestedFrameMax;
    }

    public void setRequestedFrameMax(Integer requestedFrameMax) {
        this.requestedFrameMax = requestedFrameMax;
    }

    public Integer getRequestedHeartbeat() {
        return requestedHeartbeat;
    }

    public void setRequestedHeartbeat(Integer requestedHeartbeat) {
        this.requestedHeartbeat = requestedHeartbeat;
    }

    public Integer getShutdownTimeout() {
        return shutdownTimeout;
    }

    public void setShutdownTimeout(Integer shutdownTimeout) {
        this.shutdownTimeout = shutdownTimeout;
    }

    public Boolean getTopologyRecoveryEnabled() {
        return topologyRecoveryEnabled;
    }

    public void setTopologyRecoveryEnabled(Boolean topologyRecoveryEnabled) {
        this.topologyRecoveryEnabled = topologyRecoveryEnabled;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public Integer getWorkPoolTimeout() {
        return workPoolTimeout;
    }

    public void setWorkPoolTimeout(Integer workPoolTimeout) {
        this.workPoolTimeout = workPoolTimeout;
    }
}
