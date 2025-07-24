package com.example.stream_04.orders;

public class OrderClient5 {

  //  private static final Logger log = LoggerFactory.getLogger(OrderClient.class);
  //  private WebClient webClient;
  //
  //  public OrderClient() {
  //    this.webClient = WebClient.builder().baseUrl("http://localhost:8080").build();
  //  }
  //
  //  public ApiResponse makeOrder(LimitOrderRequest order, boolean allowImmediate) {
  //
  //    log.info("makeOrder( {}, allowImmediate {} ) ", order, allowImmediate);
  //
  //    this.webClient
  //        .post()
  //        .uri("/orders?allowImmediate={allowImmediate}", allowImmediate)
  //        .contentType(APPLICATION_JSON)
  //        .bodyValue(order)
  //        .accept(APPLICATION_JSON, TEXT_EVENT_STREAM)
  //        .exchangeToFlux(
  //            response -> {
  //              log.info("Starting processing of server response");
  //              var contentType =
  // response.headers().contentType().orElse(APPLICATION_OCTET_STREAM);
  //              if (response.statusCode().isError()) {
  //                return Flux.error(new RuntimeException("something went wrong"));
  //              }
  //              if (APPLICATION_JSON.isCompatibleWith(contentType)) {
  //                log.info(" Processing application/json so returning ApiResponse.Immediate()");
  //                return Flux.from(response.bodyToMono(String.class).map(Immediate::new))
  //                    .cast(Object.class);
  //              } else if (TEXT_EVENT_STREAM.isCompatibleWith(contentType)) {
  //                log.info(" Processing text/evet-stream");
  //                return response
  //                    .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<String>>() {
  //                    })
  //                    .cast(Object.class);
  //              } else {
  //                return Flux.error(
  //                    new IllegalStateException(" Unsupported content type: " + contentType));
  //              }
  //            })
  //        .switchOnFirst(
  //            (signal, objectFlux) -> {
  //              if (signal.isOnNext()) {
  //                Object o = signal.get();
  //                if (o instanceof Immediate immediate) {
  //                  return Mono.just(immediate);
  //                } else if (o instanceof ServerSentEvent serverSentEvent) {
  //                  if ("order-executed".equals(serverSentEvent.event())) {
  //                    return Mono.just(new ApiResponse.Immediate(serverSentEvent.data()));
  //                  }
  //                  SseStreamId streamId = SseStreamId.generate("foo");
  //                  SseEventId eventId = SseEventId.firstEvent(streamId);
  //                  // call blocking code to create a stream in rabbitMQ
  //                  return Mono.fromRunnable(
  //                          () -> {
  //                            log.info("Make a blocking call to make the rabbit stream");
  //                          })
  //                      .subscribeOn(
  //                          Schedulers.fromExecutor(Executors.newVirtualThreadPerTaskExecutor()))
  //                      .then(
  //                          Mono.fromCallable(
  //                              () -> {
  //                                objectFlux
  //                                    .publishOn(
  //                                        Schedulers.fromExecutor(
  //                                            Executors.newVirtualThreadPerTaskExecutor()))
  //                                    .doOnNext(
  //                                        event -> {
  //                                          ServerSentEvent<String> sseEvent =
  //                                              (ServerSentEvent<String>) event;
  //                                        })
  //                                    .subscribe(); // todo 3 lambda version ot catch errors
  //                                return new ApiResponse.Stream(eventId);
  //                              }));
  //
  //                  // after create stream I can yield an ApiResponse with the steram id
  //
  //                }
  //              }
  //            )
  //            })
  //
  //  public static void main(String[] args) throws InterruptedException, IOException {
  //    OrderClient client = new OrderClient();
  //    // try price of 111 to get an immediate fill
  //    // try a price of 101 or 100.5 to get a stream
  //    // even if order can be filled right away set allow immediate to false to force a stream to
  // be
  //    // returned
  //    var streamOrder = new LimitOrderRequest("APPL", 100, BigDecimal.valueOf(110.5));
  //    ApiResponse response = client.makeOrder(streamOrder, false);
  //    switch (response) {
  //      case Immediate immediate:
  //        log.info("immediate response payload: {}", immediate.payload());
  //        break;
  //      case Stream stream:
  //        log.info("stream response streamId: {}", stream.lastEventId());
  //    }
  //
  //    System.out.println("Press Enter to exit...");
  //    System.in.read();
  //  }
}
