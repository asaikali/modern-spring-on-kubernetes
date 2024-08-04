package com.example.client;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientSsl;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
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

  public DeclarativeClientService(
      WebClient.Builder builder, WebClientSsl sslClient, RestClientSsl restClientSsl) {
    // uncomment if you want to create with the web client
    //    WebClient webClient = builder
    //        .baseUrl("https://localhost:8443")
    //        .apply(sslClient.fromBundle("client"))
    //        .build();
    //
    //    WebClientAdapter adapter = WebClientAdapter.create(webClient);
    //    HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

    RestClient restClient =
        RestClient.builder()
            .apply(restClientSsl.fromBundle("client"))
            .baseUrl("https://localhost:8443")
            .build();
    RestClientAdapter adapter = RestClientAdapter.create(restClient);
    HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

    this.remoteService = factory.createClient(RemoteService.class);
  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("\n**********************************************");
    System.out.println("calling remote service using Declarative Client mTLS");

    var response = this.remoteService.getRoot();
    System.out.println(response);
  }
}
