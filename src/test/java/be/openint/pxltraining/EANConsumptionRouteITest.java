package be.openint.pxltraining;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.util.stream.Collectors;

/**
 * an integration test using a kafka container.
 * The "sendBody(...)" triggers the route and is expecting that the route consumer (from) has the uri: "direct:camelRoute".
 * The test checks that exactly one event was sent to the topic and that the body of the event is the same as the one sent.
 */
@SpringBootTest
@CamelSpringBootTest
@EnableAutoConfiguration
public class EANConsumptionRouteITest {

  private static final String jsonPayload = """
          		{
          			"eventType": "NEW",
          			"meterID": "1234",
          			"meterType": "digital",
          			"eanNumber": "abcdefg123456",
          			"timestampStart": "2024-09-16T22:22:22",
          			"timestampEnd": "2024-09-17T11:11:11",
          			"unit": "kWh",
          			"offtakeValue": "1234",
          			"offtakeStartValue": "0",
          			"offtakeEndValue": "1234",
          			"injectionValue": "4321",
          			"injectionStartValue": "0",
          			"injectionEndValue": "4321"
          		}
          """;

  @Autowired
  private CamelContext context;

  @Container
  private static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));

  //a utility to send messages on a route
  @Autowired
  ProducerTemplate producerTemplate;

  //a utility to mock an Camel consumer component
  @EndpointInject("mock:consumeKafkaTopic")
  private MockEndpoint messageConsumer;

  @Value("${kafka.energy.info.topic}")
  private String topicName;

  @DynamicPropertySource
  public static void runtimeConfiguration(DynamicPropertyRegistry registry) {
    kafka.start();
    //dynamically configures the url of the bootstrap server for the Camel Kafka producer.
    registry.add("camel.component.kafka.brokers", kafka::getBootstrapServers);
  }

  @Test
  public void testKafkaProduced() throws Exception {

    context.addRoutes(new RouteBuilder() {
      @Override
      public void configure() throws Exception {
        //seekTo=BEGINNING ensure that all the events on the topic are read.
        //This ensures that if this route is started after that the event was sent it will still be consumed
        from("kafka:" + topicName + "?seekTo=BEGINNING")
            .log("unmarshalling ===")
            .to(messageConsumer);
      }
    });

    // defining what is expected to happen on the mock
    messageConsumer.expectedMessageCount(1);
    String onlineJsonPayload = jsonPayload.lines().collect(Collectors.joining("")).replaceAll("\\s*", "");

    messageConsumer.expectedBodiesReceived(onlineJsonPayload);

    //send the event to the route to test
    producerTemplate.sendBody("direct:addEANConsumptions", onlineJsonPayload);

    //verifies that the expectation were fulfilled
    messageConsumer.assertIsSatisfied();
  }
}