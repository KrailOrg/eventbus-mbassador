package uk.q3c.krail.eventbus.mbassador;

import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * This has not SubscribeTo annotation
 * <p>
 * Created by David Sowerby on 19 Oct 2017
 */

@Listener
public class MessageConsumer2 {

    List<String> msgs = new ArrayList<>();

    @Handler
    public void handler(TestMessage msg) {
        msgs.add(msg.getMsg());
    }
}
