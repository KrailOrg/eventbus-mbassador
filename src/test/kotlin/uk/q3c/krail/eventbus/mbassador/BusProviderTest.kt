package uk.q3c.krail.eventbus.mbassador

import com.google.inject.Guice
import com.google.inject.Key
import com.google.inject.Provider
import com.google.inject.TypeLiteral
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import uk.q3c.krail.eventbus.EventBus
import uk.q3c.krail.eventbus.MessageBus

/**
 * Created by David Sowerby on 19 Oct 2017
 */

class BusProviderTest {
    private lateinit var messageBusProvider: Provider<MessageBus>
    private lateinit var eventBusProvider: Provider<EventBus>
    private lateinit var eventBusProvider2: Provider<EventBus>
    private lateinit var consumerOnMessage1: MessageConsumerExplicitlySubscribedToGlobalMessageBus
    private lateinit var consumerOnMessage2: MessageConsumerShouldDefaultToGlobalMessageBus
    private lateinit var consumerOnEvent: MessageConsumerExplicitlySubscribedToGlobalEventBus
    private lateinit var consumerOnBoth: MessageConsumerExplicitlySubscribedToBothGlobalBuses
    private lateinit var eventBus: EventBus
    private lateinit var messageBus: MessageBus

    @Before
    fun setup() {
        val injector = Guice.createInjector(EventBusModule())
        val pm = object : TypeLiteral<Provider<MessageBus>>() {

        }
        val pe = object : TypeLiteral<Provider<EventBus>>() {

        }

        messageBusProvider = injector.getInstance(Key.get(pm))
        eventBusProvider = injector.getInstance(Key.get(pe))
        eventBusProvider2 = injector.getProvider(EventBus::class.java)
        consumerOnMessage1 = injector.getInstance(MessageConsumerExplicitlySubscribedToGlobalMessageBus::class.java)
        consumerOnMessage2 = injector.getInstance(MessageConsumerShouldDefaultToGlobalMessageBus::class.java)
        consumerOnEvent = injector.getInstance(MessageConsumerExplicitlySubscribedToGlobalEventBus::class.java)
        consumerOnBoth = injector.getInstance(MessageConsumerExplicitlySubscribedToBothGlobalBuses::class.java)
        eventBus = injector.getInstance(EventBus::class.java)
    }

    @Test
    @Throws(Exception::class)
    fun singletonInstanceAndIsolationBetweenMessageAndEventBus() {
        // given
        val messageBus1 = messageBusProvider.get()
        val messageBus2 = messageBusProvider.get()
        val eventBus1 = eventBusProvider.get()
        val eventBus2 = eventBusProvider.get()
        val eventBus2a = eventBusProvider2.get()


        // then: buses are singleton
        assertThat(messageBus1).isSameAs(messageBus2)
        assertThat(eventBus1).isSameAs(eventBus2)
        assertThat(eventBus).isSameAs(eventBus1)
        assertThat(eventBus).isSameAs(eventBus2a)

        // then: separated event and message buses
        assertThat(eventBus1).isNotSameAs(messageBus1)

        // then: native buses are separated
        assertThat(messageBus1.implementation()).isSameAs(messageBus2.implementation())
        assertThat(messageBus1.implementation()).isNotSameAs(eventBus2.implementation())
    }

    @Test
    fun publishMessagesSyncAndAsync() {
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

    /**
     * Checks that an incorrectly annotated message consumer causes no Guice failure
     */
    @Test
    fun incorrectConsumerAnnotation() {
        // given:
        val injector = Guice.createInjector(EventBusModule())

        // when:
        injector.getInstance(MessageBus::class.java)
        injector.getInstance(MessageConsumerInvalidAnnotation::class.java)

        // then: no exception if we get here
        assertThat(true).isTrue()
    }


}