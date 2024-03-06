package com.fluxexa.exa1.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document
@Getter
@Setter
@RequiredArgsConstructor
public class User {


    @MongoId
    @JsonIgnore
    private ObjectId id;

    @Indexed(unique = true)
    private String email;

    private String name;

    private String password;

    private Address address;

}
