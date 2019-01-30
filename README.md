# KumuluzEE AMQP
[![Build Status](https://img.shields.io/travis/kumuluz/kumuluzee-ethereum/master.svg?style=flat)](https://travis-ci.org/kumuluz/kumuluzee-ethereum)

KumuluzEE AMQP project for development of messaging applications.

KumuluzEE AMQP enables you to easily send and recieve messages. We will be using the RabbitMQ implementation in this example. The RabbitMQ documentation can be found [here](https://www.rabbitmq.com/).

## Usage

You can enable KumuluzEE RabbitMQ support by adding the following dependency to pom.xml:
```xml
<dependency>
    <groupId>com.kumuluz.ee.amqp</groupId>
    <artifactId>kumuluzee-amqp-rabbitmq</artifactId>
    <version>${kumuluzee-amqp-rabbitmq.version}</version>
</dependency>
```

## Installing RabbitMQ
In order to use RabbitMQ, you first need to install the RabbitMQ broker. You can find installation guide [here](https://www.rabbitmq.com/download.html).

### Configuration
To configure RabbitMQ, create configuration in resources/config.yaml.
Here you can put your RabbitMQ hosts and their configurations. 
```yaml
kumuluzee:
  amqp:
    rabbitmq:
      hosts: List
        name: String (required)
        url: String (required) - null
        password: String - null
        username: String - null
        port: Integer - null
        automaticRecoveryEnabled: Boolean - null
        channelRpcTimeout: Integer - null
        channelShouldCheckRpcResponseType: Boolean - null
        connectionTimeout: Integer - null
        enableHostnameVerification: Boolean - null
        handshakeTimeout: Integer - null
        networkRecoveryInterval: Integer - null
        requestedChannelMax: Integer - null
        requestedFrameMax: Integer - null
        requestedHeartbeat: Integer - null
        shutdownTimeout: Integer - null
        topologyRecoveryEnabled: Boolean - null
        uri: String - null
        virtualHost: String - null
        automaticRecoveryEnabled: Boolean - null
        clientProperties: Map<String, Object> - null
        exchanges: List
          name: String (required)
          type: String [fanout, direct, topic, headers] - fanout
          durable: Boolean - false
          autoDelete: Boolean - false
          arguments: Map<String, Object> - null
        queues:
          name: String (required)
          exclusive: Boolean - false
          durable: Boolean - false
          autoDelete: Boolean - false
          arguments: Map<String, Object> - null
      properties: List
        name: String (required)
        contentType: String - null
        contentEncoding: String - null
        headers: Map<String, Object> - null
        deliveryMode: Integer - null
        priority: Integer - null
        corelationId: String - null
        replyTo: String - null
        expiration: String - null
        messageId: String - null
        timestamp: Boolean - null
        type: String - null
        userId: String - null
        appId: String - null
        clusterId: String - null
```

## Connection
You can also create a connection to a server with parameters that are not available in the config.yaml. 

Annotate a class (all methods which return a map will be considered) or a method (only a method which returns a map will be considered) with `@AMQPConnection`. In this method create a new connection to the broker using ConnectionFactory provided by RabbitMQ. 

The method has to return a Map<String, Connection> object, where String is the name of the connection. You can then configure exchanges and queues in the config.yaml with the name you selected. All other parameters in the config.yaml are ignored.

```java
@AMQPConnection
    public Map<String, Connection> localhostConnection(){
        Map<String, Connection> localhost = new HashMap<>();

        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");

        Connection connection = null;

        try {
            connection = connectionFactory.newConnection();
        } catch (IOException | TimeoutException  e) {
            log.severe("Connection could not be created");
        }

        localhost.put("MQtest", connection);
        return localhost;
    }
```

```yaml
kumuluzee:
  amqp:
    rabbitmq:
      hosts:
        - name: MQtest
          port: 9000 #ignored
          automaticRecoveryEnabled: true #ignored
          exchanges:
           - name: directExchange
             type: direct	
```

The connection to the server is managed by the framework and does not need to be closed. In case you want to manage connections yourself use RabbitConnection class where you can create and close connections.
## Channel
You can obtain RabbitMQ Channel by using `@AMQPChannel`. Then you can use this channel to send and recieve messages ([Read more](https://www.rabbitmq.com/getstarted.html))
```java
    @AMQPChannel(host: String - "")
```
```java
    @Inject
    @AMQPChannel("hostName")
    private Channel channel;
```
## Consuming messages
In order to listen to queues you can annotate your method with `@AMQPConsumer`.
You must add ConsumerMessage parameter which will allow you to obtain details about the recieved messages. 

```java
@AMQPConsumer(
host: String - "", 
exchange: String - "", 
key: String[] - {""}, 
prefetch: int - 100, 
autoAck: boolean - true
)
```

```java
@AMQPConsumer(host="MQtest", exchange="directExchange", key="secret")
    public void listenToDirectExchange(ConsumerMessage consumerMessage){
        ...
   }
```

## Send messages
You can send messages by annotating a method with `@AMQPProducer` annotation. The method must return an object which will be then sent to the consumer. 
```java
@AMQPProducer(
host: String - "", 
exchange: String - "", 
key: String[] - {""}, 
properties: String - ""
)
```

```java
@AMQPProducer(host="MQtest", exchange = "directExchange", key = "object")
    public ExampleObject sendObject(){
        ExampleObject exampleObject = new ExampleObject();
        exampleObject.setContent("I'm just an object.");
        return exampleObject;
    }
```
You can also send a Message object, where you can define host, exchange, keys, body and properties (which wouldn't be possible to define in the config.yml). Keep in mind that Message parameters will override annotation parameters.

```java
@AMQPProducer
    public Message sendFullMessage(){
        Message message = new Message();
        ExampleObject exampleObject = new ExampleObject();
        exampleObject.setContent("I'm an object in a special message");

        if(Math.random() < 0.5){
            message.host("MQtest")
                    .key(new String[]{"object"})
                    .exchange("directExchange")
                    .basicProperties(MessageProperties.BASIC);
        } else {
            message.host("MQtest2")
                    .key(new String[]{"testQueue"})
                    .basicProperties("testProperty");
        }

        return message.body(exampleObject);
    }
```
To send a message to a specific queue, you just have to remove the exchange from the annotation and use key as a queue name

```java
@AMQPProducer(host="MQtest2", key = "testQueue")
    public Message sendToQueue(){
        Message message = new Message();
        ExampleObject exampleObject = new ExampleObject();
        exampleObject.setContent("I'm an object in a message");
        return message.body(exampleObject).basicProperties(MessageProperties.BASIC);
    }
```

## Sample

You can start by using the [sample code](https://github.com/kumuluz/kumuluzee-samples/tree/master/amqp/rabbitmq).

## License

MIT
