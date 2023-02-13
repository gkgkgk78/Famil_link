import React, { useEffect, useState } from "react";
import StyledEngine, { StyledEngineProvider } from "@mui/styled-engine";
import { Card, List, CardHeader, CardContent, CardActions, TextField, Typography, Grid, Icon, Button, Paper,  } from '@mui/material';
import { getTodoList, deleteTodo, editTodo } from "./service/ApiService";
import TodoList from "./TodoList";

const TodoListPage = () => {

    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getTodoList("", (response)=>{
            setItems(response.data.todolist)
            setLoading(false);
        },
        (error)=> {
            console.log("error: "+error)
        })
    }, [items]);


    const deleteItem = (item) => {
        deleteTodo(item, (item)=>{

        }, 
        (error)=>{
            console.log("uid: "+item.uid+"//"+error)
        })
    }

    const editItem = (item) => {
        editTodo(item, (item)=>{
            console.log("todo check")
        },
        (error)=>{
            console.log(error)
        })
    }


    return (
    <Grid item xs={10}>
        <Paper style={{margin: 16}}>
        <List>
            {items.map((item, index) => (
                <TodoList
                    item={item}
                    key={index}
                    editItem={editItem}
                    deleteItem={deleteItem}
                />
            ))}
        </List>
        </Paper>
    </Grid>
    )
}

export default TodoListPage;