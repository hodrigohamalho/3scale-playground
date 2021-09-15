// camel-k: language=java dependency=camel-netty-http dependency=camel-http dependency=camel-kafka dependency=camel-dataformat

import java.util.Locale;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;

// CPF to use: 00045789100

public class Composition extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // Main Route
        from("netty-http:proxy://0.0.0.0:8080/").routeId("Main Route")
            .streamCaching()
            .wireTap("direct:kafka-audit")
            .multicast(new MyAggregationStrategy()).parallelProcessing()
                .to("direct:person")
                .to("direct:accounts")
            .end() // End Multicast
        .end()
        ;

        // Person Route
        from("direct:person").routeId("Person Route")
            //.circuitBreaker()
                .setHeader("type",constant("person"))
                .toD("http://person.demo.svc:8080/person/${headers.CamelHttpPath}?bridgeEndpoint=true")
            //.onFallback()
            //    .transform().constant("Person service fallback")
            //.endCircuitBreaker()
        .end();

        // Account Route
        from("direct:accounts").routeId("Accounts Route")
            .circuitBreaker()
                .setHeader("type",constant("accounts"))
                .toD("http://account.demo.svc:8080/account/${headers.CamelHttpPath}?bridgeEndpoint=true")
            .onFallback()
                .transform().constant("Account service fallback")
            .endCircuitBreaker()
        .end();

        // Kafka Route
        from("direct:kafka-audit").routeId("Kafka Route")
            .log("Enviando mensagem pro Kafka = ${headers.x-forwarded-for}")          
            .setHeader(KafkaConstants.KEY, constant("ip"))
            .transform().simple("${headers.x-forwarded-for}")
            .to("kafka:auditoria?brokers=my-cluster-kafka-bootstrap.demo.svc:9092")
        .end();
    }
    
   
    class MyAggregationStrategy implements org.apache.camel.AggregationStrategy {
        List<Map<String, Object>> accounts;
        Map<String, Object> person;
        ObjectMapper objectMapper;
        
        public MyAggregationStrategy() {
            objectMapper = new ObjectMapper();
        }
        
        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
            try {
                if (newExchange.getIn().getHeader("type").equals("person")) {
                    this.person = objectMapper.readValue(newExchange.getIn().getBody(String.class), new TypeReference<Map<String, Object>>() {});
                } else if (newExchange.getIn().getHeader("type").equals("accounts")) {
                    this.accounts = objectMapper.readValue(newExchange.getIn().getBody(String.class), new TypeReference<List<Map<String, Object>>>() {});
                }
                
                Map<String, Object> results = new HashMap<>();
                results.put("person", this.person);
                results.put("accounts", this.accounts);
                
                newExchange.getIn().setBody(objectMapper.writeValueAsString(results));
                
                return newExchange;
            } catch (Exception e) {
                throw new RuntimeException (e);
            }
        }
  
    }
  }