import * as React from "react";

const RegisterForm = (props) => (
    <form method="POST" action='/register'>
        <label>
            Email:
            <input type="text" name="email" required/>
        </label>
        <label>
            Username:
            <input type="text" name="username" required/>
        </label>
        <label>
            Password:
            <input type="password" name="password" required/>
        </label>
        <label>
            Repeat Password:
            <input type="password" required/>
        </label>
        <input type="hidden" name="return_url" value="/"/>

        <input type="submit" value="Submit"/>
    </form>)

export default RegisterForm;