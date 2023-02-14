import React, { useState } from "react";
import ListItem from '@mui/material/ListItem';
import Checkbox from '@mui/material/Checkbox';
import ListItemText from '@mui/material/ListItemText';
import InputBase from '@mui/material/InputBase';
import { ListItemSecondaryAction, IconButton} from "@mui/material";
import { DeleteOutline } from "@mui/icons-material";
import { FavoriteBorder, Favorite } from '@mui/icons-material';
import { pink } from '@mui/material/colors'
import { makeStyles } from '@mui/styles';

const useStyles = makeStyles({
    root: {
        color: pink[800],
        '&.Mui-checked': {
            color: pink[600],
        },
    }
})

const TodoList = (props) => {

    const [item, setItem] = useState(props.item);
    const [readOnly, setReadOnly] = useState(true);
    const [done, setDone] = useState(props.item.status);


    const classes = useStyles();
    // const turnOffReadOnly = () => {
    //     setReadOnly(false);
    // }

    // const turnOnReadOnly = (e) => {
    //     if(e.key === 'Enter' && readOnly === false){
    //         setReadOnly(true);
    //         editItem(item);
    //     }
    // }



    const deleteItem = props.deleteItem;

    const deleteEventHandler = (e) => {
        deleteItem(item.uid)
    }

    const editItem = props.editItem;

    const checkBoxHandler = (e) => {
        const newDoneState = e.target.checked;
        setDone(newDoneState);
        editItem(item.uid, newDoneState?1:0);
    }

    const content = (item.content).replace(/"/gi, '');

    return (
        <ListItem>
            <Checkbox
            icon={<FavoriteBorder />}
            checkedIcon={<Favorite />}
            className={classes.root}
            //edit handler 생성하고 활성화
            checked={done}
            onChange={checkBoxHandler} 
            />
            <ListItemText>
                <InputBase
                inputProps={{"aria-label" : "naked",
                            readOnly : readOnly }}
                //onClick = {turnOffReadOnly}
                //onKeyDown = {turnOnReadOnly}
                //onChange = {editEventHandler}
                type="text"
                id={item.uid}
                name={item.uid}
                value={content}
                multiline={true}
                fullWidth={true}/>
            </ListItemText>
            <ListItemSecondaryAction>
                <IconButton aria-label="Delete Todo" onClick={deleteEventHandler}>
                    <DeleteOutline />
                </IconButton>
            </ListItemSecondaryAction>
        </ListItem>
    )

}

export default TodoList;