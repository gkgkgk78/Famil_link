package com.famillink.controller;


import com.famillink.annotation.ValidationGroups;
import com.famillink.exception.BaseException;
import com.famillink.exception.ErrorMessage;
import com.famillink.model.domain.param.PhotoSenderDTO;
import com.famillink.model.domain.user.Account;
import com.famillink.model.domain.user.Member;
import com.famillink.model.domain.user.Todo;
import com.famillink.model.service.AccountService;
import com.famillink.model.service.FlaskService;
import com.famillink.model.service.PhotoService;
import com.famillink.model.service.TodoService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api("Account Controller")
@RequiredArgsConstructor
@RequestMapping("/account")
@JsonAutoDetect
@RestController
public class TodoController {

    private final TodoService todoService;

    @PostMapping("/todo")
    @ApiOperation(value = "가족 todo생성", notes = "req_data : [token, 내용]")
    public ResponseEntity<?> AddTodo(Authentication authentication, @RequestPart String content) throws Exception {
        Account auth = (Account) authentication.getPrincipal();
        Long tt = auth.getUid();
        todoService.createtodo(tt, content);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "todo생성 성공");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }



    @GetMapping("/todo")
    @ApiOperation(value = "가족 todo불러오기", notes = "req_data : [token, flask 파일]")
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


    @DeleteMapping("/todo/{uid}")
    @ApiOperation(value = "가족 todo삭제하기", notes = "req_data : [token]")
    public ResponseEntity<?> DeleteTodo(@PathVariable Long uid) throws Exception {
        todoService.deletetodo(uid);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "todo삭제 성공");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }


    @PutMapping("/todo/{uid}")
    @ApiOperation(value = "가족 todo완료처리", notes = "req_data : [token,가족 uid]")
    public ResponseEntity<?> CompleteTodo(@PathVariable Long uid) throws Exception {
        todoService.updatetodo(uid);
        Map<String, Object> responseResult = new HashMap<>();
        responseResult.put("result", true);
        responseResult.put("msg", "todo완료처리 성공");
        return ResponseEntity.status(HttpStatus.OK).body(responseResult);
    }


}
