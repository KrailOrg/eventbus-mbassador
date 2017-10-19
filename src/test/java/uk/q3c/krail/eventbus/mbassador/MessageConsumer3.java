package uk.q3c.krail.eventbus.mbassador;

import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Bus annotation is invalid
 * <p>
 * Created by David Sowerby on 19 Oct 2017
 */

@Listener
@SubscribeTo(Test.class)
public class MessageConsumer3 {

    List<String> msgs = new ArrayList<>();

    @Handler
    public void handler(TestMessage msg) {
        msgs.add(msg.getMsg());
    }
}
