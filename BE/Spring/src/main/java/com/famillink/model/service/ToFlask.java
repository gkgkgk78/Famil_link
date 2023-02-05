package com.famillink.model.service;



import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.net.MalformedURLException;


public interface ToFlask {

    void send(Authentication authentication, String path) throws IOException;




}
