package be.openint.pxltraining;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Exercise 2 base route
 */
@Component
public class GetImbalancedPriceRoute extends RouteBuilder {

    @Value("${kafka.energy.imbalanced.price.topic}")
    private String topicName;

    @Override
    public void configure() throws URISyntaxException, IOException {

        // https://camel.apache.org/components/4.4.x/scheduler-component.html
        from("scheduler:checkImbalancedPrice?delay=1000&repeatCount=1")
                .id("GetImbalancedPriceRoute")
                .setBody(constant(">>>>>>>>> hello world 2! <<<<<<<<<<"))
                // https://camel.apache.org/components/4.4.x/log-component.html
                .to("log:be.openint.pxltraining");
    }
}
