package com.example.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

interface RemoteService {

  @GetExchange("/")
  String getRoot();
}

@Service
@Order(4)
public class DeclarativeClientService implements CommandLineRunner {

  private final RemoteService remoteService;

  public DeclarativeClientService() {

    RestClient restClient =
        RestClient.builder()
            .defaultHeaders(httpHeaders -> httpHeaders.setBasicAuth("user", "password"))
            .baseUrl("http://localhost:8080")
            .build();
    RestClientAdapter adapter = RestClientAdapter.create(restClient);
    HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

    this.remoteService = factory.createClient(RemoteService.class);
  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("\n**********************************************");
    System.out.println("calling remote service using Declarative Client");

    var response = this.remoteService.getRoot();
    System.out.println(response);
  }
}
