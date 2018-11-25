import * as React from "react";
import { Button, Form, FormGroup, Label, Input, FormText } from 'reactstrap';

const RegisterForm = () => (
    <Form method="POST" action='/register'>
        <FormGroup>
            <Label for="email"> Email:</Label>
            <Input type="text" name="email" id="email" placeholder="Enter email" />
        </FormGroup>
        <FormGroup>
            <Label for="username"> Username:</Label>
            <Input type="text" name="username" id="username" placeholder="Enter username" />
        </FormGroup>
        <FormGroup>
            <Label for="password"> Password:</Label>
            <Input type="password" name="password" id="password" />
        </FormGroup>
        <FormGroup>
            <Label>Repeat Password::</Label>
            <Input type="password" req />
        </FormGroup>

        <Input type="hidden" name="return_url" value="/"/>

        <Button type="submit">Register</Button>
    </Form>)

export default RegisterForm;