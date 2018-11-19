import * as React from "react";
import UserAPI from "../network/UserAPI";

export default class Form extends React.Component {
    userAPI = new UserAPI();
    render() {
        let form = <form method="POST" action='/login-auth'>
            <label>
                Username:
                <input type="text" name="username" required/>
            </label>
            <label>
                Password:
                <input type="password" name="password" required/>
            </label>
            <input type="hidden" name="return_url" value="/"/>

            <input type="submit" value="Submit"/></form>;
        return form;
    }

    private handleSubmit(event) {
        event.preventDefault();
        const data = new FormData(event.target);
        this.userAPI.login(data).then((response) => {
            console.log(response);
        }, (reason => {
            console.log(reason);
        }));
    }
}