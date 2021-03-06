import * as React from "react";
import {ChatList} from 'react-chat-elements';
import {SystemMessage, SideBar, MessageList, Button, Input} from 'react-chat-elements';

import 'react-chat-elements/dist/main.css';
import EventBusService from "../network/EventBusService";
import {observer} from "mobx-react";
import {action, IObservableArray, observable, reaction} from "mobx";

@observer
export default class Chat extends React.Component {
    eventBusService = new EventBusService();
    usersOnlone = observable([]);
    currentMessages = observable([]);
    @observable token: string;
    @observable currentUser: string;
    private inputMessage: Input;
    private inputRef;
    private readonly addressTo = 'client.to.chat';
    private readonly addressFrom = 'chat.to.client';


    componentDidMount() {
        this.eventBusService.connect(() => {

            fetch('/chat/token', {method: 'GET'}).then(value => {
                // console.log(value.json());
                value.text().then(value1 => {
                    this.token = value1;
                    this.eventBusService.eventBus.registerHandler(this.addressFrom, null, (error, message) => {
                        if (error) {
                            console.log(error);
                            return
                        }
                        const messageJson = JSON.parse(message.body);
                        switch (messageJson.type) {
                            case 'connect' : {
                                this.userConnect(messageJson);
                                break;
                            }

                        }

                    });
                    this.eventBusService.eventBus.registerHandler('user.' + this.token, null, (error, message) => {
                        if (!error) {

                        }
                        const messageJson = JSON.parse(message.body);
                        switch (messageJson.type) {
                            case 'message' : {
                                this.messageRecieve(messageJson);
                                break;
                            }

                        }
                    });
                    this.eventBusService.eventBus.send(this.addressTo, null, {action: 'users'}, ((error, message) => {
                        const users = JSON.parse(message.body);
                        this.usersOnlone.replace(users.map((user) => ({
                            avatar: 'https://facebook.github.io/react/img/logo.svg',
                            alt: 'Reactjs',
                            title: user,
                            subtitle: 'What are you doing?',
                            date: new Date(),
                            unread: 0
                        })));
                        this.currentUser = this.usersOnlone[0].title;
                    }));
                });


            });
        })
    }

    @action
    messageRecieve(message) {
        if (message.from === this.token) {
            // this.currentMessages.push({
            //     position: 'right',
            //     type: 'text',
            //     text: message.body,
            //     date: new Date(),
            // });
            return;
        }
        if (message.from === this.currentUser) {
            this.currentMessages.push({
                position: 'left',
                type: 'text',
                text: message.body,
                date: new Date(),
            });
            return;
        }

    }

    @action
    sendMessage(message) {
        const sent = {
            body: message,
            to: this.currentUser
        };
        this.eventBusService.eventBus.send(this.addressTo, sent, {action: 'message'}, (error, response) => {
            if (error) {
                throw new Error();
            }
            console.log('sent success');
            this.currentMessages.push({
                position: 'right',
                type: 'text',
                text: message,
                date: new Date(),
            })
        });
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

    @action
    goToChat(chat) {
        this.currentUser = chat.title;
        this.currentMessages.clear();
    }

    onSendClick() {
        const message = this.inputMessage.value;
        if (message && message != '') {
            this.sendMessage(message);
            this.inputRef.clear();
        }
    }

    render() {
        return (
            <div style={{'display': 'grid', 'gridTemplateColumns': '1fr 3fr'}}>
                <SideBar type='light'
                         top={
                             <div>
                                 <span>User : {this.token}</span>
                                 <a href='/logout'>Logout</a></div>

                         }
                         center={
                             <ChatList
                                 className='chat-list'
                                 onClick={(chat) => this.goToChat(chat)}
                                 dataSource={this.usersOnlone.slice()}/>
                         }
                />
                <SideBar
                    type='light'
                    top={"With: " + this.currentUser}
                    center={<MessageList
                        className='message-list'
                        lockable={true}
                        toBottomHeight={'100%'}
                        dataSource={this.currentMessages.slice()}/>}
                    bottom={
                        <Input
                            ref={(input)=>this.inputRef = input}
                            inputRef={(ref) => this.inputMessage = ref}
                            placeholder="Type here..."
                            multiline={true}
                            rightButtons={
                                <Button
                                    onClick={() => this.onSendClick()}
                                    color='white'
                                    backgroundColor='black'
                                    text='Send'/>
                            }/>}/></div>


        )

    }
}
