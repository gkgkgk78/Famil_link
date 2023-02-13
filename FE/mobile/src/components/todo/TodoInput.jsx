import React, {useEffect, useState} from "react";
import "./todo.css";
import { Card, CardHeader, CardContent, CardActions, TextField, Typography, Grid, Icon, Button,  } from '@mui/material';
import { margin } from "@mui/system";
import palette from "../../lib/styles/palette";
import styled from "styled-components";
import { makeStyles } from '@material-ui/core/styles';
import { StyledEngineProvider } from '@mui/material/styles';
import { addTodoList } from "./service/ApiService";

const useStyles = makeStyles({
  root: {
    '& .MuiOutlinedInput-root': {
      '&.Mui-focused fieldset': {
        borderColor: palette.orange[8],
        fontFamily: "'Rubik', sans-serif"
      },
      '&:hover fieldset': {
        borderColor: palette.orange[8],
      },
    '& label': {
        // placeholder text color
        color: palette.orange[8],
        fontFamily: "'Rubik', sans-serif",
      }
    },
    '& label.Mui-focused': {
        // 해당 input focus 되었을 때 placeholder text color
        // floatng label을 사용할 때 처리 필요하다
        color: palette.orange[8],
        fontFamily: "'Rubik', sans-serif",
      },
  },
  changeFont: {
    fontFamily: "'Rubik', sans-serif",
    fontSize: 30,
  },
  changeFont2: {
    fontFamily: "'Do Hyeon', sans-serif",
    fontSize: 15,
    color: palette.gray[3]
  },
  changeFont3: {
    fontFamily: "'Do Hyeon', sans-serif",
    fontSize: 30,
    color: palette.orange[4]
  },
  logo: {
    width: '220px',
    height: '70px',
  },
  customBtn: {
    '&:hover': {
      backgroundColor: palette.orange[1],
      color: palette.orange[5],
      fontFamily: "'Do Hyeon', sans-serif",
      fontSize: 15,
    },
    fontFamily: "'Do Hyeon', sans-serif",
    fontSize: 15,
  },
});


const TodoInput = () => {

    const classes = useStyles();

    const [field, setField] = useState('');

    //텍스트 변경 반영 이벤트
    const displayText = (e) => {
      setField(e.target.value);
    };

    //remove 이벤트
    const onClick = (e) => {
      setField("");
    }

    //add 이벤트
    const onBtnClick = (e) => {
      const content = field;
      e.preventDefault();
      addTodoList(content, (data)=>{
      },
      (error)=>{
        console.log(error)
      })
      setField(""); //전송 후 초기화
    }

    //엔터 등록 이벤트
    const enterKeyEventHandler = (e) => {
      if(e.key == 'Enter'){
        if(field.length>0){
          onBtnClick();
        }
      }

    }

    const [fixTodo, setFixTodo] = useState([
      '거실청소',
      '분리수거',
      '화장실청소',
      '장보기',
      '이불빨래',
      '토르 산책하기',
      '토르 밥주기',
      '토르 데리고 애견카페 가기',
      '종량제 봉투 사러 가기',
      '관리비 내기',
      '물 사오기',
      '설거지 하기'
    ])

    //할일 리스트 인덱스 선택 시 중복제거 알고리즘
    	
  function lottoNum () {
    let num = [];
    let i = 0;
    while (i < 5) {
      let n = Math.floor(Math.random() * fixTodo.length -1) + 1;
      if (! sameNum(n)) {
        num.push(n);
        i++;
      }
    }
    function sameNum (n) {
      for (var i = 0; i < num.length; i++) {
        if (n === num[i]) {
          return true;
        }
      }
      return false;
    }
    return num;
  }

    //todo 5개 뽑아서 버튼 만드는 알고리즘
    function repeatTodo(fixTodo) {
      let num = lottoNum();

      let arr = []

      for(let i = 0; i<5; i++){

        arr.push(
          <Button sx={{color: palette.gray[4]}} className={classes.customBtn} onClick={displayText} value={fixTodo[num[i]]}>{fixTodo[num[i]]}</Button>
        )
      }
      
      console.log(num)
      return arr;
    }

    return (
            <Grid item xs={10}>
                <Card elevation={7} sx={{borderRadius: '16px', textAlign: 'center', padding: '20px'}}>
                    <CardContent>
                        <span className={classes.changeFont2}>recommend</span>
                        {repeatTodo(fixTodo)}
                    </CardContent>

                    <form noValidate onSubmit={onBtnClick}>
                    <CardContent>
                        <TextField 
                        sx={{width: '80%', color: palette.orange[8]}} 
                        label="Todo" className={classes.root}
                        id="content"
                        name="content"
                        required
                        onChange={displayText}
                        onKeyPress={enterKeyEventHandler}
                        value={field}/>
                    </CardContent>
                    <CardActions sx={{float: "right"}}>
                        <Button sx={{color: palette.orange[8]}} onClick={onClick}>Remove</Button>
                        <Button sx={{color: palette.orange[8]}} onClick={onBtnClick}>Add</Button>
                    </CardActions>
                    </form>

                </Card>
            </Grid>
          )
}

export default TodoInput;
