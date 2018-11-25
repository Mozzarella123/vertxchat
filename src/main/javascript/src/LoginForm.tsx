import * as React from "react";
import {Button, Form, FormGroup, Label, Input, FormText} from 'reactstrap';

const LoginForm = () => (
    <Form method="POST" action='/login-auth'>
        <FormGroup>
            <Label for="username">Username</Label>
            <Input type="text" name="username" id="username" placeholder="Enter username"/>
        </FormGroup>
        <FormGroup>
            <Label for="password">Password</Label>
            <Input type="password" name="password" id="password"/>
        </FormGroup>

        <Input type="hidden" name="return_url" value="/"/>

        <Button type="submit">Login</Button>
    </Form>)

export default LoginForm;