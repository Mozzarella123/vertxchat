import * as React from "react";
import {ChatList} from 'react-chat-elements';
import {SystemMessage} from 'react-chat-elements';

import 'react-chat-elements/dist/main.css';
import EventBusService from "../network/EventBusService";
import {observer} from "mobx-react";
import {action, IObservableArray, observable, reaction} from "mobx";

@observer
export default class Chat extends React.Component {
    eventBusService = new EventBusService();
    usersOnlone = observable([]);
    @observable token: string;

    componentDidMount() {
        this.eventBusService.connect(() => {
            fetch('/chat/token', {method: 'GET'}).then(value => {
                // console.log(value.json());
                this.token = value.toString();
            });
            console.log("eb open");
            this.eventBusService.eventBus.registerHandler("chat.to.client", null, (error, message) => {
                if (error) {
                    console.log(error);
                    return
                }
                const messageJson = JSON.parse(message.body);
                switch (messageJson.type) {
                    case 'connect' :
                    {
                        this.userConnect(messageJson);
                        break;
                    }

                }

            });
            this.eventBusService.eventBus.registerHandler('user.' + this.token, null, (error, message) => {
                if (!error) {

                }
            });

        })
    }
    @action
    userConnect(message) {
        console.log(message.user);
        this.usersOnlone.push({
            avatar: 'https://facebook.github.io/react/img/logo.svg',
            alt: 'Reactjs',
            title: message.user,
            subtitle: 'What are you doing?',
            date: new Date(),
            unread: 0
        });
        console.log(this.usersOnlone.length);
    }

    render() {
        return (
            <div>
                <span>User : {this.token}</span>
                <ChatList
                    className='chat-list'
                    dataSource={this.usersOnlone.slice()}/></div>

        )

    }
}
