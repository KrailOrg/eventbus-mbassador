package uk.q3c.krail.eventbus.mbassador;

import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David Sowerby on 19 Oct 2017
 */

@Listener
@SubscribeTo(GlobalBus.class)
public class MessageConsumer {

    List<String> msgs = new ArrayList<>();

    @Handler
    public void handler(TestMessage msg) {
        msgs.add(msg.getMsg());
    }
}
