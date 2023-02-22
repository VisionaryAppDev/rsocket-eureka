package com.example.demo.controller;

import com.example.demo.entity.Course;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Controller
public class CourseController {

     /// Implements the request-response interaction pattern. The user is expected to supply a course and this endpoint echos it back to the caller
    @MessageMapping("request-response")
    public Mono<Course> requestResponse(final Course course) {
        log.info("Received request-response course details {} ", course);
        return Mono.just(new Course("Your course name: " + course.getCourseName()));
    }

         /// Implements the fire-forget interaction pattern. The user is expected to supply a course and expects nothing. Thus, we are returning an empty Mono.
    @MessageMapping("fire-and-forget")
    public Mono<Void> fireAndForget(final Course course) {
        log.info("Received fire-and-forget course details {} ", course);
        return Mono.empty();
    }

         /// Implements the request-stream interaction pattern. The user is expected to supply a course and this endpoint returns a stream of course with modified course name in an interval of one second
    @MessageMapping("request-stream")
    public Flux<Course> requestStream(final Course course) {
        log.info("Received request-stream course details {} ", course);
        return Flux
                .interval(Duration.ofSeconds(1))
                .map(index -> new Course("Your course name: " + course.getCourseName() + ". Response ///" + index))
                .log();
    }

       /// Implements the channel interaction pattern. The user is expected to supply a stream and this endpoint returns a stream of course with a modified course name in an interval configured by the user. The user can specify the interval by invoking the delayElements() method in the source Flux.  Recall that in channel interaction patterns, both sides can send a stream of data.
    @MessageMapping("stream-stream")
    public Flux<Course> channel(final Flux<Integer> settings) {
        log.info("Received stream-stream (channel) request... ");

        return settings
                .doOnNext(setting -> log.info("Requested interval is {} seconds", setting))
                .doOnCancel(() -> log.warn("Client cancelled the channel"))
                .switchMap(setting -> Flux.interval(Duration.ofSeconds(setting)).map(index -> new Course("Spring. Response ///"+index)))
                .log();
    }
}
