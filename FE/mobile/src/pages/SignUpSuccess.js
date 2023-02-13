import React, { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import styled from 'styled-components';
import palette from '../lib/styles/palette';
import Button from '../components/common/Button';
import AuthTemplate from '../components/auth/AuthTemplate';

const StyledSucces = styled.div`
  padding: 20px;
  align-items: center;
  h3 {
    text-align: center;
    color: ${palette.orange[7]};
    margin-bottom: 3rem;
  }
  h5 {
    padding: 0;
    font-weight: 1rem;
    text-align: center;
    color: ${palette.gray[6]};
    // padding-bottom: 0.5rem;
    margin-bottom: 3rem;
    letter-spacing: 1.5px;
    
    line-height: 2em;   
  }
`;

const ButtonWithMarginTop = styled(Button)`
  margin-top: 1rem;
`;

const SignupSuccess = () => {
    const navigate = useNavigate();
    const goToLogin = () => {
        navigate('/login');
    }

    return (
      <AuthTemplate>
        <StyledSucces>
            <h3>

    성공적으로 가입 신청 되었습니다.
                </h3>
                <h5>
    메일에 첨부된 링크를 확인해주세요.
    <br />

    링크를 클릭하시면
    <br />

    패밀링크의 서비스를 이용하실 수 있습니다
            </h5>
            <ButtonWithMarginTop orange fullWidth onClick={goToLogin}>로그인 하기</ButtonWithMarginTop>
        </StyledSucces>
      </AuthTemplate>
    );
}
export default SignupSuccess;