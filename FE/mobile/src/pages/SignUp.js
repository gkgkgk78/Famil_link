import React from 'react';
import SignupForm from '../containers/auth/SignupForm';
import AuthTemplate from '../components/auth/AuthTemplate';

const SignUp = () => {
    return (
        <AuthTemplate>
            <SignupForm />
        </AuthTemplate>
    );
}

export default SignUp;