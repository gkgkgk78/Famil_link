package com.famillink.model.service;


import com.famillink.model.domain.user.Todo;
import java.util.List;


public interface TodoService {


    void createtodo(Long uid, String content) throws Exception;

    List<Todo> gettodo(Long accoountuid);

    void deletetodo(Long uid) throws Exception;

    void updatetodo(Long uid) throws Exception;
}
