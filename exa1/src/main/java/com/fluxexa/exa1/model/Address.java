package com.fluxexa.exa1.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Address {

    private String street;

    private String city;

    private String state;

    private Integer zipCode;
}
