package uk.q3c.krail.eventbus.mbassador

import com.google.inject.Guice
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

/**
 * Created by David Sowerby on 19 Oct 2017
 */

class BusProviderTest {
    private lateinit var messageBusProvider: MBassadorMessageBusProvider
    private lateinit var eventBusProvider: MBassadorEventBusProvider
    private lateinit var consumerOnMessage1: MessageConsumerExplicitlySubscribedToGlobalMessageBus
    private lateinit var consumerOnMessage2: MessageConsumerShouldDefaultToGlobalMessageBus
    private lateinit var consumerOnEvent: MessageConsumerExplicitlySubscribedToGlobalEventBus
    private lateinit var consumerOnBoth: MessageConsumerExplicitlySubscribedToBothGlobalBuses


    @Before
    fun setup() {
        val injector = Guice.createInjector(EventBusModule())
        messageBusProvider = injector.getInstance(MBassadorMessageBusProvider::class.java)
        eventBusProvider = injector.getInstance(MBassadorEventBusProvider::class.java)
        consumerOnMessage1 = injector.getInstance(MessageConsumerExplicitlySubscribedToGlobalMessageBus::class.java)
        consumerOnMessage2 = injector.getInstance(MessageConsumerShouldDefaultToGlobalMessageBus::class.java)
        consumerOnEvent = injector.getInstance(MessageConsumerExplicitlySubscribedToGlobalEventBus::class.java)
        consumerOnBoth = injector.getInstance(MessageConsumerExplicitlySubscribedToBothGlobalBuses::class.java)
    }

    @Test
    @Throws(Exception::class)
    fun singletonInstanceAndIsolationBetweenMessageAndEventBus() {
        // given
        val messageBus1 = messageBusProvider.get()
        val messageBus2 = messageBusProvider.get()
        val eventBus1 = eventBusProvider.get()
        val eventBus2 = eventBusProvider.get()

        // then: buses are singleton
        assertThat(messageBus1).isSameAs(messageBus2)
        assertThat(eventBus1).isSameAs(eventBus2)

        // then: separated event and message buses
        assertThat(eventBus1).isNotSameAs(messageBus1)

        // then: native buses are separated
        assertThat(messageBus1.implementation()).isSameAs(messageBus2.implementation())
        assertThat(messageBus1.implementation()).isNotSameAs(eventBus2.implementation())
    }

    @Test
    fun publishOnMessageBusOnly() {
        // given
        val messageBus = messageBusProvider.get()
        val eventBus = eventBusProvider.get()


        // when
        messageBus.publishSync(TestMessage("published message"))
        messageBus.publishASync(TestMessage("published async message"))
        eventBus.publishSync(TestMessage("published event"))
        eventBus.publishASync(TestMessage("published async event"))


        // then
        assertThat(consumerOnMessage1.msgs).containsOnly("published message", "published async message")
        assertThat(consumerOnMessage2.msgs).containsOnly("published message", "published async message")
        assertThat(consumerOnEvent.msgs).containsOnly("published event", "published async event")
        assertThat(consumerOnBoth.msgs).containsOnly("published message", "published async message", "published event", "published async event")
    }


}