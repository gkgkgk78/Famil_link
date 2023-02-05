import React from "react";
import styled, { css } from "styled-components";
import palette from "../../lib/styles/palette";

const StyledButton = styled.button`
  border: 1px solid;
  border-radius: 25px;
  font-size: 1rem;
  font-weight: 700;
  margin-top: 2.5rem;
  padding-top: 0.5rem;
  justify-content: center;
  //   padding: 0.25rem 1rem;
  color: white;
  outline: none;
  cursor: pointer;
  background: ${palette.gray[8]};
  &:hover {
    background: ${palette.gray[6]};
  }
  ${(props) =>
    props.fullWidth &&
    css`
      padding-top: 0, 75rem;
      padding-bottom: 0.75rem;
      width: 100%;
      font-size: 1.125rem;
    `}
  ${(props) =>
    props.cyan &&
    css`
      background: ${palette.orange[7]};
      &:hover {
        background: ${palette.orange[4]};
        transition: 0.5s;
      }
    `}
`;

const Button = (props) => <StyledButton {...props} />;

export default Button;
