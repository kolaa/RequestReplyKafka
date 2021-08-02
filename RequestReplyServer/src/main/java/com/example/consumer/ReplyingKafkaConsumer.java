package com.example.consumer;

import com.example.model.Account;
import com.example.model.GetAccountsRequest;
import com.example.model.GetAccountsResponse;
import com.example.service.AccountsService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class ReplyingKafkaConsumer {

	AccountsService accountsService;

	@org.springframework.beans.factory.annotation.Autowired
	public ReplyingKafkaConsumer(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	 @KafkaListener(topics = "${kafka.topic.request-topic}")
	 @SendTo
	  public GetAccountsResponse listen(GetAccountsRequest request) throws InterruptedException {

		var accounts = accountsService.getAccountList(request.getClientId());

		 GetAccountsResponse response = new GetAccountsResponse().accounts(accounts.toArray(new Account[0]));

		 return response;
	  }

}
