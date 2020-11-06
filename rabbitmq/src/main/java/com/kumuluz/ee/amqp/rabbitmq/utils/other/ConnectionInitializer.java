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

import com.kumuluz.ee.amqp.common.annotations.AMQPConnection;
import com.kumuluz.ee.amqp.common.utils.AnnotatedMethod;
import com.kumuluz.ee.amqp.common.utils.ConnectionUtilInitializer;
import com.kumuluz.ee.amqp.rabbitmq.config.HostInitializer;
import com.rabbitmq.client.Connection;

import javax.annotation.Priority;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * A class that creates connections
 *
 * @author Blaž Mrak
 * @since 1.0.0
 */

public class ConnectionInitializer implements ConnectionUtilInitializer {

    private static final Logger LOG = Logger.getLogger(ConnectionInitializer.class.getName());

    @Override
    public void after(@Observes @Priority(2500) AfterDeploymentValidation adv, BeanManager bm) {
        for (AnnotatedMethod<AMQPConnection> inst : methodList) {
            LOG.info("Found method " + inst.getMethod().getName() + " in class " +
                    inst.getMethod().getDeclaringClass());
        }

        if (methodList.size() > 0) {
            for (AnnotatedMethod<AMQPConnection> inst : methodList) {
                Object instance = bm.getReference(inst.getBean(), inst.getMethod().getDeclaringClass(),
                        bm.createCreationalContext(inst.getBean()));
                try {
                    RabbitConnection.setConnection((HashMap<String, Connection>) inst.getMethod()
                            .invoke(instance, null));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LOG.severe("Cannot create a new connection: " + e.getLocalizedMessage());
                }
            }
        }

        HostInitializer.getInstance().initializeRabbitmq();
    }
}
