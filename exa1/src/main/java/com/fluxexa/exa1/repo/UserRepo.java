package com.fluxexa.exa1.repo;

import com.fluxexa.exa1.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepo extends ReactiveMongoRepository<User, String> {

    public Mono<User> findByEmail(String email);
}
