import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { isLoggIn } from '../modules/auth';
import { useDispatch } from "react-redux";
import '../components/navbar/navbar.css'
import TodoInput from "../components/todo/TodoInput"
import { familyToken } from "../modules/token";
import axios from "axios";


const Todo = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();
    const fatkn = localStorage.getItem('facesstoken')

    return (
        <div>
            <TodoInput />
        </div>
    )

}

export default Todo;