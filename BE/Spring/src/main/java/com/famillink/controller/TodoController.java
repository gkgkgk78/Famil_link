package com.famillink.controller;


import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Todo;
import com.famillink.model.service.TodoService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api("Account Controller")
@RequiredArgsConstructor
@RequestMapping("/todo")
@JsonAutoDetect
@RestController
public class TodoController {

    private final TodoService todoService;

    @PostMapping("")
    @ApiOperation(value = "가족 todo생성", notes = "req_data : [token, 내용]")
    public ResponseEntity<?> AddTodo(Authentication authentication, @RequestParam String content) throws Exception {
        Account auth = (Account) authentication.getPrincipal();
        Long tt = auth.getUid();
        todoService.createtodo(tt, content);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "todo생성 성공");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }



    @GetMapping("")
    @ApiOperation(value = "가족 todo불러오기", notes = "req_data : [token]")
    public ResponseEntity<?> GetTodo(Authentication authentication) throws Exception {
        Account auth = (Account) authentication.getPrincipal();
        Long tt = auth.getUid();
        List<Todo> list = todoService.gettodo(tt);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "todo불러오기 성공");
        responseResult.put("todolist", list);
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }


    @DeleteMapping("/{uid}")
    @ApiOperation(value = "가족 todo삭제하기", notes = "req_data : [todo uid]")
    public ResponseEntity<?> DeleteTodo(@PathVariable Long uid) throws Exception {
        todoService.deletetodo(uid);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "todo삭제 성공");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }


    @PutMapping("/{uid}")
    @ApiOperation(value = "가족 todo완료처리", notes = "req_data : [token,todo uid]")
    public ResponseEntity<?> CompleteTodo(@PathVariable Long uid) throws Exception {
        todoService.updatetodo(uid);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "todo완료처리 성공");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }


}
