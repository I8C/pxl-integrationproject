package be.openint.pxltraining;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * a route that is scheduled to run one time after one second delay and is logging an "hello world" message.
 */
@Component
public class EANConsumptionRoute extends RouteBuilder {

    @Value("${kafka.energy.info.topic}")
    private String topicName;

    @Override
    public void configure() {

        // https://camel.apache.org/components/4.4.x/scheduler-component.html
        from("scheduler:runOnceForPXLTemplate?delay=1000&repeatCount=1")
            .setBody(constant(">>>>>>>>> hello world! <<<<<<<<<<"))
            // https://camel.apache.org/components/4.4.x/log-component.html
            .to("log:be.openint.pxltraining");
    }
}
