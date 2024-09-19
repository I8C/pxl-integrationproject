# Exercise 1 step 2

## Send an event to a local test Kafka

1. change the producer of your route to one sending to kafka: https://camel.apache.org/components/4.4.x/kafka-component.html.
   ```
   ...
   .to("kafka:" + topicName);
   ```
   'topicName' is already available and its value is defined in you configuration (resources/application.properties).  
   In the same configuration you'll find the spring boot auto configuration ready for the use of a kafka client: _camel.component.kafka.brokers_.
   The value is the URL of kafka.

2. change the setBody(...) part of your route with a log to verify that you are passing inside it:  
   ```
   .log(">>>>>>>>>>>>> in my route! <<<<<<<<<<<<<<<<<")
   ```

3. test that you are sending something to Kafa in an integration test using TestContainer framework.  
   Open the test class _EANConsumptionRouteITest_.  
   Verify that the producerTemplate.sendBody(...) is set to the same value as your from(...) in you business route _CamelRoute_.  
   For your information, when a container is started, it choose a random port available to expose it on your machine.  
   In the _'runtimeConfiguration(...)'_ method, the server to connect is dynamically configured to the current local URI.  
   
   Start you docker server.  
   Run the integration test. If it succeed, it means that:
   - a Kafka container was started
   - an event was sent on it on a specific topic
   - exactly one event was consumed from it from that specific topic
   - the consumed event content is exactly the same as what was sent
   
   Take some time to read the test class and the comments to understand how this is working.
   
## Send an event to a local Kafka

Now that you know that you have managed to send an event on a Kafka Test container, lets send an event on local Kafka container.

1. start a local kafka container and expose his port on you host machine.
   In your git bash shell: _docker run docker run -d -p 9092:9092 --name broker apache/kafka:latest_  
2. prepare a consumer in the container that will consume the events you are sending on Kafka.
   Continue in you shell with these commands to login the container and start the consumer.
   The queue name to use is define in the application.properties with the key: _kafka.energy.info.topic_.  
   Use that topic name in you consumer.
   - _docker exec -it broker sh_
   - _/opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic **testtopic**_
3. start you application
4. send a request on you API using Postman.
5. you should see the log of your route
6. you should see the content of your request printed on the command line by your consumer in your container
   
    [to step 3](exercice-1-step-3.md) 