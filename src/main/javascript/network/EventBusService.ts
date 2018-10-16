import * as EventBus from "vertx3-eventbus-client";

class EventBusService {
  eventBus: EventBus.EventBus;

  connect(handler) {
    this.eventBus = new EventBus("/eventbus");
    this.eventBus.onopen = () => {
      console.log("eb open");
      this.eventBus.registerHandler("chat.to.client", null, handler)
    }
  }

}

export default EventBusService;
