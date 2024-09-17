package be.openint.pxltraining;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CamelRoute extends RouteBuilder {

    //TODO use this in your from(...) when you to("kafka:topicName") is ready to trigger the integration test CamelRouteITest
    public static final String FROM = "direct:camelRoute";

    @Value("${pxltraining.result.file.path}")
    private String resultFilePath;

    @Override
    public void configure() {
        // doc: https://camel.apache.org/components/4.4.x/scheduler-component.html
        from("scheduler:runOnceForPXLTemplate?delay=1000&repeatCount=1")
            .setBody(constant(">>>>>>>>> hello world! <<<<<<<<<<"))
            .to("log:" + resultFilePath);
    }
}
