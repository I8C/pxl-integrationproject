# Exercise 1 step 3

## Define your Avro Schema

To create your Avro Schema from the avro definition:
1. In your Route configuration method, read you definition and load it:  
   ```
   File schemaFile = new File(Main.class.getClassLoader().getResource("schema-dailyEnergy.avsc").toURI());
   Schema schema = new Schema.Parser().parse(schemaFile);
   ```
2. Use the schema in your route to deserialize the request body and transform it in a binary Avro output ready for Kafka.  
   Between the to(...) and the from of your route, add a processor that will handle that logic:  
   ```
   .process(e -> {
		// Deserialize the JSON string into an Avro GenericRecord
		Decoder decoder = DecoderFactory.get().jsonDecoder(schema, e.getIn().getBody(String.class));
		DatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
		GenericRecord result = reader.read(null, decoder);

		log.info("receiving energy consumption for meterID " + result.get("meterID") + " with eanNumber " + result.get("eanNumber"));

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
3. run your application with the kafka in docker to check that you receive the event inside kafka.
   Becarefull, the body that you send has conform to the Avro definition otherwise you'll get an error because the input is invalid.