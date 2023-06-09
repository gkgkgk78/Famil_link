import React from "react";
import styled from "styled-components";
import { Link } from "react-router-dom";
import palette from "../../lib/styles/palette";
import Button from "../common/Button";
import { signup } from "../../modules/auth";

// 회원가입과 로그인 폼

const StyledAuthForm = styled.div`
  padding: 20px;
  align-items: center;
  h3 {
    text-align: center;
    color: ${palette.gray[8]};
    margin-bottom: 2rem;
  }
`;
//에러 화면

const ErrorMessage = styled.div`
  color: red;
  text-align: center;
  font-size: 0.875rem;
  margin-top: 1rem;
`;

// 스타일링 된 input

const StyledInput = styled.input`
  margin-bottom: 1rem;
  font-size: 1rem;
  border: none;
  border-bottom: 1px solid ${palette.gray[5]};
  padding-bottom: 0.5rem;
  outline: none;
  width: 100%;
  &::placeholder {
    left: 5px;
    color: #adadad;
    // focus {
    //   top: -20px;
    //   font-size: 0.8rem;
    // }
  }
  &:focus {
    ::placeholder {
      color: ${palette.orange[5]};
      transition: 0.5s;
    }
    color: ${palette.orange[5]};
    border-bottom: 2px solid ${palette.orange[5]};
  }
  & + & {
    margin-top: 1rem;
  }
`;

// 폼 하단 로그인 or 회원 가입 링크 보여줌

const Footer = styled.div`
  margin-top: 2rem;
  text-align: right;
  a {
    color: ${palette.gray[6]};
    text-decoration: underline;
    &:hover {
      color: ${palette.gray[9]};
    }
  }
`;
const ButtonWithMarginTop = styled(Button)`
  margin-top: 1rem;
`;

const textMap = {
  login: "로그인",
  signup: "회원가입",
};

const welcomeText = {
  login: "안녕하세요 패밀링크입니다.",
  signup: "패밀링크 회원가입"
}

const AuthForm = ({ type, form, onChange, onSubmit, error }) => {
  const text = textMap[type];
  const wcText = welcomeText[type];
  return (
    <StyledAuthForm>
      <h3>{wcText}</h3>
      <form onSubmit={onSubmit}>
        <StyledInput
          autoComplete="email"
          name="email"
          placeholder="이메일"
          onChange={onChange}
          value={form.email}
        />
        <StyledInput
          autoComplete="new-password"
          name="pw"
          placeholder="비밀번호"
          type="password"
          onChange={onChange}
          value={form.pw}
        />
        {type === "signup" && (
          <StyledInput
            autoComplete="new-password"
            name="pwConfirm"
            placeholder="비밀번호 확인"
            type="password"
            onChange={onChange}
            value={form.pwConfirm}
          />
        )}
        {type === "signup" && (
          <StyledInput
            autoComplete="nickname"
            name="nickname"
            placeholder="가족명"
            type="text"
            onChange={onChange}
            value={form.nickname}
          />
        )}
        {type === "signup" && (
          <StyledInput
            autoComplete="address"
            name="address"
            placeholder="주소"
            type="search"
            onChange={onChange}
            value={form.address}
          />
        )}
        {type === "signup" && (
          <StyledInput
            autoComplete="phone"
            name="phone"
            placeholder="대표 전화번호"
            type="tel"
            onChange={onChange}
            value={form.phone}
          />
        )}
        {error && <ErrorMessage>{error}</ErrorMessage>}
        <ButtonWithMarginTop orange fullWidth>
          {text}
        </ButtonWithMarginTop>
      </form>
      <Footer>
        {type === "login" ? (
          <Link to="/signup">회원가입</Link>
        ) : (
          <Link to="/login">로그인</Link>
        )}
      </Footer>
    </StyledAuthForm>
  );
};

export default AuthForm;
