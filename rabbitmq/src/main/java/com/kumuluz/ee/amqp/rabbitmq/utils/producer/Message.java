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

import com.kumuluz.ee.amqp.rabbitmq.config.ConfigLoader;
import com.rabbitmq.client.AMQP;

/**
 * @author Blaž Mrak
 * @since 1.0.0
 */
public class Message {
    private AMQP.BasicProperties basicProperties;
    private Object body;
    private String exchange;
    private String[] key;
    private String host;

    public Message() {
    }

    public AMQP.BasicProperties getBasicProperties() {
        return basicProperties;
    }

    public Message basicProperties(AMQP.BasicProperties basicProperties) {
        this.basicProperties = basicProperties;
        return this;
    }

    public Message basicProperties(String basicProperties) {
        this.basicProperties = ConfigLoader.getInstance().getBasicProperties(basicProperties);
        return this;
    }

    public Object getBody() {
        return body;
    }

    public Message body(Object body) {
        this.body = body;
        return this;
    }

    public String getExchange() {
        return exchange;
    }

    public Message exchange(String exchange) {
        this.exchange = exchange;
        return this;
    }

    public String[] getKey() {
        return key;
    }

    public Message key(String[] key) {
        this.key = key;
        return this;
    }

    public String getHost() {
        return host;
    }

    public Message host(String host) {
        this.host = host;
        return this;
    }
}
