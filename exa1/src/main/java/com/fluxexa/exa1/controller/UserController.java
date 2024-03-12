package com.fluxexa.exa1.controller;

import com.fluxexa.exa1.model.User;
import com.fluxexa.exa1.exec.UserAlreadyExistsException;
import com.fluxexa.exa1.exec.UserNotFoundException;
import com.fluxexa.exa1.repo.UserRepo;
import lombok.extern.log4j.Log4j2;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;

@RestController
@Log4j2
public class UserController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private static final String FORMAT = "classpath:videos/%s.mp4";

    @Autowired
    private ResourceLoader resourceLoader;

//    @GetMapping(value = "/video" ,produces="video/mp4") //Range of data will be sent
//    @GetMapping(value = "/video", produces = MediaType.TEXT_PLAIN_VALUE)
//    public Mono<Resource> getVideo(@RequestHeader("Range") String range){
      @GetMapping(value = "/video", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
      public Mono<Resource> getVideo(){
        return Mono.fromSupplier(
 //               () -> resourceLoader.getResource(String.format(FORMAT,"javatechie"))
                () -> resourceLoader.getResource("file:D:/files/random.txt") //expects file: or classpath:
 //               () -> resourceLoader.getResource("file:D:/files/random.xlsx")
        );
    }

    @PostMapping("/")
    public Mono<User> saveUser(@RequestBody User u) {
        return userRepo.insert(u)
                .onErrorMap(exception -> {
                    if (exception instanceof DuplicateKeyException) {
                        return new UserAlreadyExistsException("User already exists with " + u.getEmail());
                    }
                    return exception;
                });
    }

/*
    @GetMapping(value = "/",produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<List<User>> getAllUsers() {
        Instant startTime = Instant.now();
        return Flux.interval(Duration.ofSeconds(2))
                .flatMap(interval ->
                        userRepo.findAll().buffer(5))
                .concatMap(
                        interval -> userRepo.findAll().buffer(5)
                )
                .takeUntil(list -> !list.isEmpty())
                .timeout(Duration.ofMillis(10))
                .onErrorResume(
                        error ->{
                            log.info("Can't produce data in 10 milliseconds");
                            return Flux.empty();
                        }
                )
                .take(5)
                .doFinally(signalType -> {
                    Instant endTime = Instant.now();
                    Duration duration = Duration.between(startTime, endTime);
                    log.info("Time taken to generate all data: {} milliseconds", duration.toMillis());
                })
                .switchIfEmpty(Mono.error(new UserNotFoundException("No users found!")));
    }
*/

    @GetMapping("/email")
    public Mono<User> getUserByName(@RequestParam String email) {
        return userRepo.findByEmail(email)
                .switchIfEmpty(Mono.error(new UserNotFoundException("User with email "+ email + " not found!")));
    }

    @PutMapping("/")
    public Mono<User> updateUser(@RequestBody User updatedUser) {
        return userRepo.findByEmail(updatedUser.getEmail())
                .flatMap(existingUser -> {
                    if(updatedUser.getName()!=null)
                        existingUser.setName(updatedUser.getName());
                    if(updatedUser.getPassword()!=null)
                        existingUser.setPassword(updatedUser.getPassword());
                    if(updatedUser.getAddress().getCity()!=null)
                        existingUser.getAddress().setCity(updatedUser.getAddress().getCity());
                    if(updatedUser.getAddress().getState() != null)
                        existingUser.getAddress().setState(updatedUser.getAddress().getState());
                    if(updatedUser.getAddress().getStreet() != null)
                        existingUser.getAddress().setStreet(updatedUser.getAddress().getStreet());
                    if(updatedUser.getAddress().getZipCode() != null)
                        existingUser.getAddress().setZipCode(updatedUser.getAddress().getZipCode());
                    return userRepo.save(existingUser);
                })
                .switchIfEmpty(Mono.error(new UserNotFoundException("No user with " + updatedUser.getEmail())));
    }

/*
    @GetMapping("/")
    public Flux<List<User>> getAllUser(){
        return userRepo.findAll()
                .buffer(5)
                .delayElements(Duration.ofSeconds(2));
    }
//On each record sent it is using different thread
    @GetMapping("/")
    public Flux<User>  (){
        return userRepo.findAll()
                .delayElements(Duration.ofSeconds(2))
                .doOnNext(ignore -> log.info(Thread.currentThread().getId()));
    }
*/

    @GetMapping("/")
    public Flux<User> getAllUser(){
        return userRepo.findAll()
                .distinct(User::getAddress)
                .delayElements(Duration.ofSeconds(2))
                .publishOn(Schedulers.single())
                .doOnNext(ignore -> log.info(Thread.currentThread().getId()));
    }

}
