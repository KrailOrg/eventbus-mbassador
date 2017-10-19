package uk.q3c.krail.eventbus.mbassador

import com.google.inject.Guice
import com.google.inject.Injector
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import uk.q3c.krail.eventbus.EventBusProvider
import uk.q3c.krail.eventbus.MessageBusProvider

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
        assertThat(injector.getInstance(MessageBusProvider::class.java)).isInstanceOf(MBassadorMessageBusProvider::class.java)
        assertThat(injector.getInstance(EventBusProvider::class.java)).isInstanceOf(MBassadorEventBusProvider::class.java)
    }

    @Test
    fun separation() {

        // when:

        var pEvent = injector.getInstance(EventBusProvider::class.java)
        var pMessage = injector.getInstance(MessageBusProvider::class.java)

        // then:

        assertThat(pEvent).isNotSameAs(pMessage)
        assertThat(pEvent.get().implementation()).isNotSameAs(pMessage.get().implementation())

    }


}