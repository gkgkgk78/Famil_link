import React from 'react';

function Todo(props){
    return (
        <div style={{
            width : '250px',
            height : '250px',
            marginLeft:'auto',
            border : '5px solid',
        }}>
            <div style={{borderBottomStyle: 'solid',}}>To-do</div>
            <ol>
                <div><input type="checkbox"></input> 여기는 todo입니다</div>
                <div><input type="checkbox"></input> 여기는 todo입니다</div>
            </ol>
        </div>
    );
}

export default Todo;