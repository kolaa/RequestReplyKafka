package com.example.api;

import com.example.model.Account;
import com.example.service.AccountsService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class AccountsApiController implements AccountsApi {

    private static final Logger log = LoggerFactory.getLogger(AccountsApiController.class);

    private final HttpServletRequest request;
    private final AccountsService accountsService;

    @org.springframework.beans.factory.annotation.Autowired
    public AccountsApiController(HttpServletRequest request, AccountsService accountsService) {
        this.request = request;
        this.accountsService = accountsService;
    }

    public ResponseEntity<List<Account>> accountsClientIdGet(@Parameter(in = ParameterIn.PATH, description = "", required=true, schema=@Schema()) @PathVariable("clientId") Integer clientId) {

        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            var accounts = accountsService.getAccountList(clientId);

            return  ResponseEntity.ok(accounts);

        }

        return new ResponseEntity<List<Account>>(HttpStatus.NOT_IMPLEMENTED);

    }

}
