# Exercise 1 step 3

## Define your Json Schema

The Json format of the request body is defined in the resources/schema/PXL_EANConsumptions_API.json file.  
We will use it to validate the imput.  

1. in your route, after the _.log(...)_ call the [json-validator](https://camel.apache.org/components/4.4.x/json-validator-component.html) component:  
   ```
   .to("json-validator:schema/PXL_EANConsumptions_API.json")   
   ``` 
   
## loop on each element of request body

The request body is an array of EAN Consumptions.  
We want to send 1 event per EAN Consumption to the Kafka topic.  
Camel offers the [split](https://camel.apache.org/components/4.4.x/eips/split-eip.html) integration pattern to fulfill this.  
Camel offers as well a Json Path language. It's a way to walk Json documents. You can use it in combination with split to itterate over each Json array element.  

1. in your route after the _json-validator_ added in the previous point, add the split:  
   ```
   .split().jsonpath("$[*]", List.class )
   ```
2. after that you have to serialize (= marshal) each element again in order to have it a string.  
   Add the marshalling as the 1st step after the split:  
   ```
   .marshal().json(JsonLibrary.Jackson)
   ```
3. eventually add some logging to understand what element of the array you are processing:
   ```
   .log(">>>>>>>>>>>>> EANConsumption index ${exchangeProperty.CamelSplitIndex} <<<<<<<<<<<<<<<<<")
   .log("${body}")             
   ```

## Define your Avro Schema

To create your Avro Schema from the avro definition:
1. In your Route configuration method, read you definition and load it:  
   ```
   ClassPathResource avroSchema = new ClassPathResource("schema/schema-dailyEnergy.avsc", this.getClass().getClassLoader());
   InputStream avroSchemaIS = avroSchema.getInputStream();
   Schema schema = new Schema.Parser().parse(avroSchemaIS);
   ```
2. Use the schema in your route to deserialize the request body and transform it in a binary Avro output ready for Kafka.  
   Between the to(...) and the from of your route, add a processor that will handle that logic:  
   ```
   .process(e -> {
		// Deserialize the JSON string into an Avro GenericRecord
		Decoder decoder = DecoderFactory.get().jsonDecoder(schema, e.getIn().getBody(String.class));
		DatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
		GenericRecord result = reader.read(null, decoder);

		log.info("receiving energy consumption for eanNumber " + result.get("eanNumber"));

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
3. configure the Kafka url and topic to use in your application.properties  
   ```
   camel.component.kafka.brokers=localhost:9092
   kafka.energy.info.topic=testtopic
   ```
4. define the topic name in your route and use it

5. run your application with the kafka in docker to check that you receive the event inside kafka.
   Becarefull, the body that you send has conform to the Avro definition otherwise you'll get an error because the input is invalid.
   
    [to step 4](exercice-1-step-4.md) 