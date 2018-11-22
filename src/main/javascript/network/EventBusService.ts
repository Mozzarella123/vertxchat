import * as EventBus from "vertx3-eventbus-client";

class EventBusService {
  eventBus: EventBus.EventBus;

  connect(handler) {
    this.eventBus = new EventBus("/eventbus");
    this.eventBus.onopen = handler;
  }

}

export default EventBusService;
