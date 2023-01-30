import React, { useEffect, useState } from 'react';
import {MdCheckBox, MdCheckBoxOutlineBlank} from 'react-icons/md';
import axios from 'axios';
import "../pages/Main.css";


function Todo(){
    const [todos, setTodos] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchTodo = async () => {
            try {
                setError(null);
                setTodos(null);
                setLoading(true);
                
                const response = await axios.get(
                    'https://jsonplaceholder.typicode.com/todos'
                );
                setTodos(response.data);
            } catch (e) {
                setError(e);
            }
            setLoading(false);
        };
        fetchTodo();
    }, []);

    if (loading) return <div>로딩중...</div>;
    if (error) return <div>애러가 발생했습니다.</div>;
    if (!todos) return null;

    const fiveTodos = todos.filter( fiveto => fiveto.id < 6);
    
    return (
        <div className='Todo'>
            <h4 className='Todotext'>To-do</h4>
            <hr />
            <div className='TodoListItem'>
            <ul >
                {fiveTodos.map(todo => (
                <div key={todo.id} className='text'>
                { todo.completed ? <div className='checkbox'><MdCheckBox /><p className='checktext'>{todo.title}</p></div> : <div className='blankbox'><MdCheckBoxOutlineBlank/><p className='blanktext'>{todo.title}</p></div> }    
                </div>
                ))}
            </ul>
            </div>
        </div>
    );
}


export default Todo;