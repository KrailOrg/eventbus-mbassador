package uk.q3c.krail.eventbus.mbassador

import com.google.inject.Guice
import com.google.inject.Injector
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import uk.q3c.krail.eventbus.EventBus
import uk.q3c.krail.eventbus.MessageBus

/**
 * Created by David Sowerby on 07 Jan 2018
 */
class EventBusModuleTest {

    private lateinit var injector: Injector

    @Before
    fun setup() {
        injector = Guice.createInjector(EventBusModule())
    }

    @Test
    fun bindings() {
        assertThat(injector.getInstance(MessageBus::class.java)).isInstanceOf(MBassadorMessageBus::class.java)
        assertThat(injector.getInstance(EventBus::class.java)).isInstanceOf(MBassadorEventBus::class.java)
    }

    @Test
    fun separation() {

        // when:

        val pEvent = injector.getProvider(EventBus::class.java)
        val pMessage = injector.getProvider(MessageBus::class.java)

        // then:

        assertThat(pEvent).isNotSameAs(pMessage)
        assertThat(pEvent.get().implementation()).isNotSameAs(pMessage.get().implementation())

    }


}