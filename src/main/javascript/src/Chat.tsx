import * as React from "react";
import {ChatList} from 'react-chat-elements'
import 'react-chat-elements/dist/main.css';
import EventBusService from "../network/EventBusService";

export default class Chat extends React.Component {
    eventBusService = new EventBusService();

    componentDidMount() {
        this.eventBusService.connect(() => {
            console.log("eb open");
            this.eventBusService.eventBus.registerHandler("chat.to.client", null, (error, message) => {
                console.log('handler registered');
            });
            // this.eventBusService.eventBus.registerHandler("user.foo")
        })
    }

    render() {
        return <ChatList
            className='chat-list'
            dataSource={[
                {
                    avatar: 'https://facebook.github.io/react/img/logo.svg',
                    alt: 'Reactjs',
                    title: 'Facebook',
                    subtitle: 'What are you doing?',
                    date: new Date(),
                    unread: 0,
                }]}/>
    }
}
