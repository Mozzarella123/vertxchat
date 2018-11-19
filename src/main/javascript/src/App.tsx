import * as React from 'react';
import EventBusService from "../network/EventBusService";
import Chat from "./Chat";
import Form from './Form'

require('./app.scss');

class App extends React.Component<{},{}> {

  private eventBusService: EventBusService;
  private isAuthorized : boolean = false;


  componentDidMount() {
    this.eventBusService = new EventBusService();
    // this.eventBusService.connect((err, msg) => this.chatMsgRecieved(err, msg));
  }


  render() {
      return (this.isAuthorized) ?  <Chat/>: <Form/>;
  }
}

export default App;
