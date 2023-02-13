import React from 'react';

function Input(props,{children}) {
    return (
        <div>
            <div>{props.value}</div>
            <input></input>
            {children}
        </div>
    );
}

export default Input;