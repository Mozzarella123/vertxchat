import * as React from "react";
import { ChatList } from 'react-chat-elements'
import 'react-chat-elements/dist/main.css';

export default class Chat extends React.Component {
  theme = {
    vars: {
      'primary-color': 'red',
      'secondary-color': '#fbfbfb',
      'tertiary-color': '#fff',
      'avatar-border-color': '#000',
    },
    AgentBar: {
      Avatar: {
        size: '42px',
      },
      css: {
        backgroundColor: 'var(--secondary-color)',
        borderColor: 'var(--avatar-border-color)',
      }
    },
    Message: {
      css: {
        fontWeight: 'bold',
      },
    },
  };

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
            }]} />
  }
}
