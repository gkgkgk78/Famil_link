import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { isLoggIn } from '../modules/auth';
import { useDispatch } from "react-redux";
import '../components/navbar/navbar.css'
import TodoInput from "../components/todo/TodoInput"
import { familyToken } from "../modules/token";
import "../components/todo/todo.css";
import axios from "axios";
import StyledEngine, { StyledEngineProvider } from "@mui/styled-engine";
import { Card, List, CardHeader, CardContent, CardActions, TextField, Typography, Grid, Icon, Button, Paper,  } from '@mui/material';
import { getTodoList, deleteTodo, editTodo } from "../components/todo/service/ApiService";
import TodoList from "../components/todo/TodoList";
import palette from "../lib/styles/palette";


const Todo = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const fatkn = localStorage.getItem('facesstoken')

    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getTodoList("", (response)=>{
            setItems(response.data.todolist)
            console.log(response.data.todolist)
            setLoading(false);
        },
        (error)=> {
            console.log("error: "+error)
        })
    }, []);


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

    let todoListPage = (
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
    );


    //로딩중일 때 렌더링할 부분
    let loadingPage = (
        <h1>
        로딩중...
        </h1>
    );

    let content = loadingPage;

    if(!loading){
        content = todoListPage;
    }

    return (
        <div>
        <StyledEngineProvider injectFirst>
            <section className = "todo temp container" id ="todo">
                <Grid container spacing={2}>
                    <Grid item xs={1} />
                    <TodoInput className="temp"/>
                    <Grid item xs={1} />
                    <Grid item xs={1} />
                    {content}
                    <Grid item xs={1} />
                </Grid>
            </section>
        </StyledEngineProvider>
    </div>
    )

}

export default Todo;