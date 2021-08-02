package com.example.api;

import com.example.model.Account;
import com.example.model.GetAccountsRequest;
import com.example.model.GetAccountsResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class AccountsApiController implements AccountsApi {

    private static final Logger log = LoggerFactory.getLogger(AccountsApiController.class);

    private final HttpServletRequest request;

    private final RestTemplate restTemplate;

    @Value("${api.host.url}")
    private String apiHost;

    @Autowired
    private ReplyingKafkaTemplate < String, GetAccountsRequest, GetAccountsResponse> kafkaTemplate;

    @Value("${kafka.topic.request-topic}")
    String requestTopic;

    @Value("${kafka.topic.requestreply-topic}")
    String requestReplyTopic;

    @org.springframework.beans.factory.annotation.Autowired
    public AccountsApiController(HttpServletRequest request, RestTemplate restTemplate) {
        this.request = request;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<List<Account>> accountsRestClientIdGet(@Parameter(in = ParameterIn.PATH, description = "", required=true, schema=@Schema()) @PathVariable("clientId") Integer clientId) {

        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {

            final String uri = apiHost+"/Accounts/"+clientId.toString();

            var accounts = restTemplate.getForObject(uri, Account[].class);

            return  ResponseEntity.ok(Arrays.asList(accounts));

        }

        return new ResponseEntity<List<Account>>(HttpStatus.NOT_IMPLEMENTED);

    }

    public ResponseEntity<List<Account>> accountsKafkaClientIdGet(@Parameter(in = ParameterIn.PATH, description = "", required=true, schema=@Schema()) @PathVariable("clientId") Integer clientId) throws ExecutionException, InterruptedException {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {

            // create producer record
            ProducerRecord<String, GetAccountsRequest> record = new ProducerRecord<String, GetAccountsRequest>(requestTopic, new GetAccountsRequest().clientId(clientId));
            // set reply topic in header
            record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, requestReplyTopic.getBytes()));
            // post in kafka topic
            RequestReplyFuture<String, GetAccountsRequest, GetAccountsResponse> sendAndReceive = kafkaTemplate.sendAndReceive(record);

            // confirm if producer produced successfully
            SendResult<String, GetAccountsRequest> sendResult = sendAndReceive.getSendFuture().get();

            //print all headers
            sendResult.getProducerRecord().headers().forEach(header -> System.out.println(header.key() + ":" + header.value().toString()));

            // get consumer record
            ConsumerRecord<String, GetAccountsResponse> consumerRecord = sendAndReceive.get();

            var accounts = consumerRecord.value().getAccounts();

            return  ResponseEntity.ok(Arrays.asList(accounts));

        }

        return new ResponseEntity<List<Account>>(HttpStatus.NOT_IMPLEMENTED);

    }


}
