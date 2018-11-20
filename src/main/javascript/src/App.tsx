import * as React from 'react';
import { Switch, Route } from 'react-router-dom'
import EventBusService from "../network/EventBusService";
import Chat from "./Chat";
import LoginForm from "./LoginForm";
import RegisterForm from "./RegisterForm";

require('./app.scss');

class App extends React.Component {

  private eventBusService: EventBusService;

  constructor(props) {
    super(props);
  }

  componentDidMount() {
    this.eventBusService = new EventBusService();

  }


  render() {
      return <Switch>
          <Route exact path='/' component={Chat}/>
          <Route path='/login' component={LoginForm}/>
          <Route path='/register' component={RegisterForm}/>
      </Switch>
  }
}

export default App;
