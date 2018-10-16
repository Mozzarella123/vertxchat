import * as React from 'react';
import EventBusService from "../network/EventBusService";
import Chat from "./Chat";

require('./app.scss');

class App extends React.Component {

  private eventBusService: EventBusService;

  constructor(props) {
    super(props);
  }
  componentDidMount() {
    this.eventBusService = new EventBusService();
    console.log("mounted");
    this.eventBusService.connect((err, msg) => this.chatMsgRecieved(err, msg));
  }

  chatMsgRecieved(err, msg) {
    if (err) {
      console.log("error")
    }
    else {
      let event = JSON.parse(msg.body);
      console.log(event.message)
    }
  }

  render() {
    return <Chat></Chat>;
  }
}

export default App;