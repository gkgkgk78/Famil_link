package com.famillink.model.mapper;


import com.famillink.model.domain.user.Todo;
import org.apache.ibatis.annotations.Mapper;


import java.util.List;

@Mapper
public interface TodoMapper {

    //CRD를 하면됨

    void createtodo(Todo todo) throws Exception;

    List<Todo> gettodo(Long accoountuid) throws Exception;

    void deletetodo(Long uid) throws Exception;

    Todo getonetodo(Long uid) throws Exception;


}
