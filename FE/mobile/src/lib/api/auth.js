import family from "./family";

//로그인

export const login = ({ username, password }) =>
    family.post('/api/auth/login', { username, password });

//회원가입

export const signup = ({ username, password }) =>
    family.post('/api/auth/signup', { username, password });

//로그인 상태 확인

export const  check = () => family.get('/api/auth/check');