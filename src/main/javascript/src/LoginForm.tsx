import * as React from "react";

const LoginForm = (props) => (
     <form method="POST" action='/login-auth'>
        <label>
            Username:
            <input type="text" name="username" required/>
        </label>
        <label>
            Password:
            <input type="password" name="password" required/>
        </label>
        <input type="hidden" name="return_url" value="/"/>

        <input type="submit" value="Submit"/>
    </form>)

export default LoginForm;