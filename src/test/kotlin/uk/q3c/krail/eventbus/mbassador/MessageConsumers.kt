package uk.q3c.krail.eventbus.mbassador

import net.engio.mbassy.listener.Handler
import net.engio.mbassy.listener.Listener
import org.junit.Test
import uk.q3c.krail.eventbus.GlobalEventBus
import uk.q3c.krail.eventbus.GlobalMessageBus
import uk.q3c.krail.eventbus.SubscribeTo
import java.util.*

@Listener
class MessageConsumerShouldDefaultToGlobalMessageBus {

    var msgs: MutableList<String> = ArrayList()

    @Handler
    fun handler(msg: TestMessage) {
        msgs.add(msg.msg)
        println("Message received: " + msg.msg)
    }
}

@Listener
@SubscribeTo(GlobalMessageBus::class)
class MessageConsumerExplicitlySubscribedToGlobalMessageBus {

    var msgs: MutableList<String> = ArrayList()

    @Handler
    fun handler(msg: TestMessage) {
        msgs.add(msg.msg)
        println("Message received: " + msg.msg)
    }
}

@Listener
@SubscribeTo(GlobalMessageBus::class, GlobalEventBus::class)
class MessageConsumerExplicitlySubscribedToBothGlobalBuses {

    var msgs: MutableList<String> = ArrayList()

    @Handler
    fun handler(msg: TestMessage) {
        msgs.add(msg.msg)
        println("Message received: " + msg.msg)
    }
}

@Listener
@SubscribeTo(GlobalEventBus::class)
class MessageConsumerExplicitlySubscribedToGlobalEventBus {

    var msgs: MutableList<String> = ArrayList()

    @Handler
    fun handler(msg: TestMessage) {
        msgs.add(msg.msg)
        println("Message received: " + msg.msg)
    }
}

@Listener
@SubscribeTo(Test::class)
class MessageConsumerInvalidAnnotation {

    internal var msgs: MutableList<String> = ArrayList()

    @Handler
    fun handler(msg: TestMessage) {
        msgs.add(msg.msg)
    }
}
