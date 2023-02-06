import family from "./family";

//로그인

export const login = ({ username, password }) =>
    family.post('/api/auth/login', { username, password });

//회원가입

export const signup = ({ username, password, familyname, phone, email, path }) =>
    family.post('/api/auth/signup', { username, password, familyname, email, phone, path });

//로그인 상태 확인

export const  check = () => family.get('/api/auth/check');