import React from 'react';

function Calendar(props) {
    return (
        <div style={{
            width : '500px',
            height : '500px',
            marginRight:'auto',
            border : '5px solid',
        }}>
            <div style={{borderBottomStyle: 'solid',}}>일정</div>
            <ol>
                <li>1</li>
                <li>2</li>
                <li>3</li>
            </ol>
        </div>
    );
}

export default Calendar;