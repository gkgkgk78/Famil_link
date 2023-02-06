package com.famillink.model.service;


import org.springframework.security.core.Authentication;

import java.io.IOException;


public interface ToFlask {

    void send(Authentication authentication, String path) throws IOException;




}
