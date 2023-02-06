package com.famillink.model.service;


import org.springframework.security.core.Authentication;


public interface ToFlask {

    void send(Authentication authentication, String path) throws Exception;




}
