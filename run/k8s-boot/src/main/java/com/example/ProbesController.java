package com.example;

import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.LivenessState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ProbesController {

  private final ApplicationEventPublisher eventPublisher;

  public ProbesController(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  @EventListener
  public void onLivenessStateChange(AvailabilityChangeEvent<LivenessState> event) {
    switch (event.getState()) {
      case BROKEN:
        System.out.println("Application is now broken liveness checks will fails");
        break;
      case CORRECT:
        System.out.println("Application is now Live");
        break;
    }
  }

  @GetMapping("/liveness/fail")
  public String failLiveness() {
    AvailabilityChangeEvent.publish(this.eventPublisher, this, LivenessState.BROKEN);
    return "Liveness checks will fail ";
  }

  @GetMapping("/liveness/pass")
  public String passLiveness() {
    AvailabilityChangeEvent.publish(this.eventPublisher, this, LivenessState.CORRECT);
    return "Liveness checks will pass ";
  }

  @EventListener
  public void onReadinessStateChange(AvailabilityChangeEvent<ReadinessState> event) {
    switch (event.getState()) {
      case ACCEPTING_TRAFFIC:
        System.out.println("Application accepting requests ");
        break;
      case REFUSING_TRAFFIC:
        System.out.println("Application refusing requests");
        break;
    }
  }

  @GetMapping("/readiness/fail")
  public String failReadiness() {
    AvailabilityChangeEvent.publish(this.eventPublisher, this, ReadinessState.REFUSING_TRAFFIC);
    return "Readiness checks will fail ";
  }

  @GetMapping("/readiness/pass")
  public String passReadiness() {
    AvailabilityChangeEvent.publish(this.eventPublisher, this, ReadinessState.ACCEPTING_TRAFFIC);
    return "Readiness checks will pass";
  }
}
