
package com.example.model;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"accounts"
})
public class GetAccountsResponse {

@JsonProperty("accounts")
private Account[] accounts;

@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("accounts")
public Account[] getAccounts() {
return accounts;
}

@JsonProperty("accounts")
public void setAccounts(Account[] clientId) {
this.accounts = accounts;
}

@JsonIgnore
public GetAccountsResponse accounts(Account[] accounts) {
    this.accounts = accounts;
    return this;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}