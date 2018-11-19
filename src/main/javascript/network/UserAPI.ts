import {NetworkSettings} from './NetworkSettings';
class UserAPI {

    public login(request) {
        return fetch(this.buildUrl("login"), {
            method: 'POST',
            body : request
        })
    }

    private buildUrl(action) {
        return NetworkSettings.HOST + "/" + action;
    }

}

export default UserAPI;
