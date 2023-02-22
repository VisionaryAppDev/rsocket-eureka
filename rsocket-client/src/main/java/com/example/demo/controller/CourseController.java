package com.example.demo.controller;

import com.example.demo.entity.Course;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
@RestController
public class CourseController {

    private final LoadBalancerClient loadBalancerClient;

    @GetMapping("/test")
    public Object channel() throws ExecutionException, InterruptedException {
        RSocketRequester requester = RSocketRequester.builder().rsocketStrategies(b -> {
            b.decoder(new Jackson2JsonDecoder());
            b.encoder(new Jackson2JsonEncoder());
        }).rsocketConnector(connector -> {
          connector.reconnect(Retry.fixedDelay(1, Duration.ofMillis(100)));
        }).tcp(loadBalancerClient.choose("RSOCKET").getUri().getHost(), 7000);

        Mono<Course> courseMono = requester
                .route("request-response")
                .data(new Course("Spring"))
                .retrieveMono(Course.class);

        System.out.println(courseMono.toFuture().get());
        return courseMono;
    }
}
