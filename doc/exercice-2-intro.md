# Exercise 2

## Introduction

In this exercise you are going to poll on scheduled time the energy prices in Belgium and put it on a Kafka topic.
We will use the Elia Opendata API: https://help.opendatasoft.com/apis/ods-explore-v2/  

We will use the _Imbalance prices per minute (Near real-time)_ API: https://opendata.elia.be/explore/dataset/ods161/api/  

## Schedule the route every 60 seconds

1. We need to gat the price real time. It is available every minute. Let's configure the secheduler in the route of the GetImbalancedPriceRoute class.  
   Change `from("scheduler:runOnceForPXLTemplate?delay=1000&repeatCount=1")` to `from("scheduler:runOnceForPXLTemplate?delay=60000")`  
   That is remove the repeatCount parameter that is limiting the schedule to one run.  
   Change the delay to 60000, that is 1 minute between every run.
   
   
## Query the API

To query the API we will use the Camel ['rest' component](https://camel.apache.org/components/4.4.x/rest-component.htm)  

1. Configure the rest base URL to query Elias.  
   At the beginning of your confirgure method in your route class, add the folowwing configuration:  
   ```
   restConfiguration()
       //the base url for the API calls
       .host("https://opendata.elia.be/api/explore/v2.1/catalog/datasets/ods161");   
   ``` 

2. query the Elia API with the 'rest' component with an HTTP GET on the _records_ sub path with the _limit=1_ query parameter.  
   The _limit=1_ limit the response to the lastest result.  
   Replace _setBody(...)_ and _to(...)_ parts of your route with: 
   ```
   //HTTP GET query on https://opendata.elia.be/api/explore/v2.1/catalog/datasets/ods161/records?limit=1
   .to("rest:get:records?limit=1")   
   ```   

3. the result is a Json array with one element. To extrat the only element of the array, we will use the _split_ integration pattern as we did in the exercise 1 step 3.  
   Add this after your _to("rest:...)_ element in your route:  
   ```
   .split().jsonpath("$.results[*]", List.class)
       .marshal().json(JsonLibrary.Jackson)
       .log(">>>>>>>>>>>>> Imbalanced price date time ${jsonpath($.datetime)} <<<<<<<<<<<<<<<<<")
   ```

## Serialize the price with Avro

1. load the Avro schema. Add in the beginning of your configure method the loading of the schema:  
   ```
   File schemaFile = new File(App.class.getClassLoader().getResource("schema/schema-imbalancedPrice.avsc").toURI());
   Schema schema = new Schema.Parser().parse(schemaFile);
   ```

2. serialize the price in binary fromat with Avro.
   In your route, after _log("${body}"), add the processor that will perform the seralization logic:  
   ```
   .process(e -> {
       // Deserialize the JSON string into an Avro GenericRecord
       Decoder decoder = DecoderFactory.get().jsonDecoder(schema, e.getIn().getBody(String.class));
       DatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
       GenericRecord result = reader.read(null, decoder);
       
       log.info("receiving imbalanced price for date time " + result.get("datetime"));
       
       // Serialize the Avro GenericRecord to bytes
       ByteArrayOutputStream baos = new ByteArrayOutputStream();
       Encoder encoder = EncoderFactory.get().jsonEncoder(schema, baos);
       DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
       writer.write(result, encoder);
       encoder.flush();
       baos.close();
       
       e.getIn().setBody(baos.toByteArray());
   })
   ```
## Send the lastest price to the Kafka topic

1. In your GetImbalancedPriceRoute class add the field to collect the topic name configured in your application.properties:  
   ```
   @Value("${kafka.energy.imbalanced.price.topic}")
   private String topicName;
   ```

2. You are producing to an other topic. Configure the topic name.  
   In your GetImbalancedPriceRoute class add the field to collect the the credentials configured in your application.properties:  
   ```
   @Value("${kafka.imbalanced.price.sasl-jaas-config}")
   private String saslJaasConfig;
   ```
   
3. You cannot have 2 Kafka producers with the same configuration to 2 different topics.  
   You need to an other client id, lets suffix it with '2'. Replace [CLIENT_ID] with your name in the kafka URL.  
   At the end of your route the Kafka producer:  

   ```
   .to("kafka:" + topicName + "?clientId=[CLIENT_ID]2&saslJaasConfig=" + saslJaasConfig);
   ```

4. Ask the credentials to send messages on the _ID_PRODUCE_PRICES_ topic.  
   In your application.properties, add the property `kafka.imbalanced.price.sasl-jaas-config` and replace [USER] and [PASSWORD] with the one received in:  
   ```
   kafka.imbalanced.price.sasl-jaas-config=org.apache.kafka.common.security.plain.PlainLoginModule required username="[USER]" password="[PASSWORD]";
   ```
   
5. run your application locally to verify that it works correctly.  
   At this stage, it will send the price to the IBM Kafka already configured in the previous exercice.
   
## upload your jar to your cloud VM

Use the instructions in exercice 1 step 5 to copy and rund your application on your VM in the cloud.
