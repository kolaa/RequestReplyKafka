package com.example.service;

import com.example.model.Account;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AccountsService {

    private static final int maxArray = 10000;

    List<Account> accounts;

    public AccountsService() {

        this.accounts = new ArrayList<>();

        for (Integer i = 0; i < maxArray; i++)
        {
            var a= new Account()
                    .accountId(i)
                    .description("Счет клиента для всего что только нужно #"+ i.toString())
                    .name("Расчетный счет " + String.format("%010d" , i))
                    .number("408178105" + String.format("%010d" , i))
                    .balance(new BigDecimal(100000 + i))
                    .openDate(new Date());

            accounts.add(a);
        }
    }

    public List<Account> getAccountList(Integer cnt) {
        return accounts.subList(0, cnt);
    }
}

