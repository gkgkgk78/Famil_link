import family from "./family";

//로그인

export const login = ({ email, pw }) =>
    family.post('/api/auth/login', { email, pw });

//회원가입

export const signup = ({ pw, nickname, phone, email, address }) =>
    family.post('/api/auth/signup', { pw, nickname, phone, email, address });

//로그인 상태 확인

export const  check = () => family.get('/api/auth/check');