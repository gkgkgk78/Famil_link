package com.famillink.model.service;


import org.springframework.security.core.Authentication;

import java.util.List;

public interface FaceDetection {


    String getMemberUidByFace(List<List<List<Integer>>> data, Authentication authentication) throws Exception;


}
