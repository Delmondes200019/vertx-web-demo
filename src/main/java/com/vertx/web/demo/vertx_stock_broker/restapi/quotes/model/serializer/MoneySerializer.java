package com.vertx.web.demo.vertx_stock_broker.restapi.quotes.model.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneySerializer extends JsonSerializer<BigDecimal> {

  @Override
  public void serialize(BigDecimal bigDecimal, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
    jsonGenerator.writeString(bigDecimal.setScale(2, RoundingMode.HALF_UP).toString());
  }
}
