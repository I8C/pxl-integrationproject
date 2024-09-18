# Exercice 1 step 4

## send your event to the real kafka

The real kafka is using enterprise requirements to connect.  
To be allowed to send events on it, you have to authenticate and to encrypt the connection.

1. got to the EEM console (ask url and credential to access it). There:  
   - request credentials (button on the top left our "topic") and save them locally
   - download the certificate in the PEM format and keep the location path for the configuration in the next point
   - add this configuration to your application.properties adapted with your downloaded credentials:  
     
	 Replace [BROKER-URL], [YOUR-FIRSTNAME], [USER], [PASSWORD], [TOPIC] and [PATH-TO-PEM] with the correct values.  
	 Becarefull, reuse the existing property _camel.component.kafka.brokers_ instead of defining it twice!  
     
	 ```
     #kafka producer client configuration
     camel.component.kafka.brokers=[BROKER-URL]
     camel.component.kafka.value-serializer=org.apache.kafka.common.serialization.ByteArraySerializer
     camel.component.kafka.client-id=[YOUR-FIRSTNAME]
     camel.component.kafka.security-protocol=SASL_SSL
     camel.component.kafka.sasl-mechanism=PLAIN
     camel.component.kafka.sasl-jaas-config=org.apache.kafka.common.security.plain.PlainLoginModule required username="[USER]" password="[PASSWORD]";
     camel.component.kafka.ssl-truststore-location=[PATH-TO-PEM]
     camel.component.kafka.ssl-truststore-type=PEM
     
     kafka.energy.info.topic=[TOPIC]
     ```
2. Run you application and send a valid request body to your API. Check (or ask to check if you don't have access) on the EEM dashboard if your event is present on the topic.  
   (ask url and credential to access it)