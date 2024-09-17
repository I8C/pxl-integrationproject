package be.openint.pxltraining;

import org.apache.avro.Schema;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.dataformat.avro.AvroDataFormat;
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

import java.io.File;

@SpringBootTest
@CamelSpringBootTest
@EnableAutoConfiguration
public class CamelRouteITest {

  @Autowired
  private CamelContext context;

  @Container
  private static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));

  @Autowired
  ProducerTemplate producerTemplate;

  @EndpointInject("mock:consumeKafkaTopic")
  private MockEndpoint messageConsumer;

  @Value("${kafka.energy.info.topic}")
  private String topicName;

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

  @DynamicPropertySource
  public static void activeQProperties(DynamicPropertyRegistry registry) {
    kafka.start();
    registry.add("camel.component.kafka.brokers", kafka::getBootstrapServers);
  }


  @Test
  public void testKafkaProduced() throws Exception {

    context.addRoutes(new RouteBuilder() {
      File schemaFile = new File(CamelRouteITest.class.getClassLoader().getResource("schema-dailyEnergy.avsc").toURI());
      Schema schema = new Schema.Parser().parse(schemaFile);
      AvroDataFormat format = new AvroDataFormat(schema);

      @Override
      public void configure() throws Exception {
        //without seekTo, it will wait for the next event coming at the end of the topic.
        //This will not happen because of a raise condition.
        //This route will most likely be ready too late and miss the event.
        from("kafka:" + topicName + "?seekTo=BEGINNING")
            .log("unmarshalling ===")
            .unmarshal(format)
            .to(messageConsumer);
      }
    });

    messageConsumer.expectedMessageCount(1);
    messageConsumer.expectedBodiesReceived(jsonPayload);
    producerTemplate.sendBody(CamelRoute.FROM, jsonPayload);
    messageConsumer.assertIsSatisfied();
  }
}