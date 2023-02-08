package com.famillink.model.service;


import org.springframework.security.core.Authentication;


public interface ToFlask {

    void send(Long uid, String path) throws Exception;




}
