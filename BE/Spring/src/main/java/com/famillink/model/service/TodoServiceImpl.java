package com.famillink.model.service;


import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.user.Todo;
import com.famillink.model.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

//private final MemberMapper mapper;


    private final TodoMapper todoMapper;


    @Override
    public void createtodo(Long uid, String content) throws Exception {

        try {
            Todo todo = new Todo(uid, content);
            todoMapper.createtodo(todo);
        } catch (Exception e) {
            throw new BaseException(ErrorMessage.NOT_CORRECT_INFORMATION);
        }


    }

    @Override
    public List<Todo> gettodo(Long accoountuid) {
        try {
            return todoMapper.gettodo(accoountuid);
        } catch (Exception e) {
            throw new BaseException(ErrorMessage.NOT_CORRECT_INFORMATION);
        }
    }


    @Override
    public void deletetodo(Long uid) throws Exception {
        //삭제하고자 하는 todo의 uid를 입력해주면 삭제를 하고자 함


        Todo to = todoMapper.getonetodo(uid);
        if (to == null) {
            throw new BaseException(ErrorMessage.NOT_CORRECT_INFORMATION);
        } else {
            todoMapper.deletetodo(uid);
        }

    }
}
