package uk.q3c.krail.eventbus.mbassador

import com.google.inject.Guice
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import net.engio.mbassy.bus.BusRuntime
import net.engio.mbassy.bus.IMessagePublication
import net.engio.mbassy.bus.MBassador
import net.engio.mbassy.bus.config.IBusConfiguration
import net.engio.mbassy.bus.publication.SyncAsyncPostCommand
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

/**
 * Created by David Sowerby on 07 Jan 2018
 */
class MBassadorEventBusIdentificationTest {
    private lateinit var provider: MBassadorMessageBusProvider
    private lateinit var consumer: MessageConsumerExplicitlySubscribedToGlobalMessageBus
    private lateinit var consumer2: MessageConsumerShouldDefaultToGlobalMessageBus



    @Before
    fun setup() {
        val injector = Guice.createInjector(EventBusModule())
        provider = injector.getInstance(MBassadorMessageBusProvider::class.java)
        consumer = injector.getInstance(MessageConsumerExplicitlySubscribedToGlobalMessageBus::class.java)
        consumer2 = injector.getInstance(MessageConsumerShouldDefaultToGlobalMessageBus::class.java)
    }


    @Before
    fun setUp() {
    }

    @Test
    fun implementationName() {
        assertThat(provider.get().implementationName()).isEqualTo("MBassador")
    }

    @Test
    fun implementation() {
        assertThat(provider.get().implementation()).isInstanceOf(MBassador::class.java)
    }

    @Test
    fun index() {
        assertThat(provider.get().index()).isEqualTo(1)
    }

    @Test
    fun scope() {
        assertThat(provider.get().scope()).isEqualTo("Singleton")
    }

    @Test
    fun busId() {
        assertThat(provider.get().busId()).isEqualTo("Global Message Bus")

    }


}

@Suppress("UNCHECKED_CAST")
val nativeBus: MBassador<Any> = mock(MBassador::class.java) as MBassador<Any>
@Suppress("UNCHECKED_CAST")
val mockCommand: SyncAsyncPostCommand<Any> = mock(SyncAsyncPostCommand::class.java) as SyncAsyncPostCommand<Any>
val aSyncPublication = mock(IMessagePublication::class.java)
val syncPublication = mock(IMessagePublication::class.java)

class TestEventBusModule : EventBusModule() {
    val mockRuntime: BusRuntime = mock(BusRuntime::class.java)

    override fun <T : Any?> createNativeBus(configuration: IBusConfiguration?): MBassador<T> {
        whenever(nativeBus.runtime).thenReturn(mockRuntime)
        whenever(mockRuntime.get<String>(IBusConfiguration.Properties.BusId)).thenReturn("wiggly")
        whenever(mockRuntime.add(any<String>(), any<Any>())).thenReturn(mockRuntime) // fluent add
        whenever(nativeBus.post(any())).thenReturn(mockCommand)
        whenever(mockCommand.asynchronously()).thenReturn(aSyncPublication)
        whenever(mockCommand.now()).thenReturn(syncPublication)
        @Suppress("UNCHECKED_CAST")
        return nativeBus as MBassador<T>
    }
}

/**
 * Verifies that the native bus (MBassador) implementation is correctly wrapped
 */
class MBassadorEventBusWrappingTest {
    private lateinit var provider: MBassadorMessageBusProvider
    private lateinit var provider1: MBassadorEventBusProvider


    @Before
    fun setup() {
        val injector = Guice.createInjector(TestEventBusModule())
        provider = injector.getInstance(MBassadorMessageBusProvider::class.java)
        provider1 = injector.getInstance(MBassadorEventBusProvider::class.java)
    }



    @Test
    fun hasPendingMessages() {
        // when:
        provider.get().hasPendingMessages()

        // then:
        verify(nativeBus).hasPendingMessages()
    }

    @Test
    fun publishASync() {
        // when:
        val status = provider.get().publishASync(TestMessage("Who me"))

        // then:
        verify(mockCommand).asynchronously()
        assertThat(status.status).isSameAs(aSyncPublication)
    }


    @Test
    fun publishSync() {
        // when:
        val status = provider.get().publishSync(TestMessage("Who me"))

        // then:
        verify(mockCommand).now()
        assertThat(status.status).isSameAs(syncPublication)
    }

    @Test
    fun subscribe() {
        // given:
        val a = Any()

        // when:
        provider.get().subscribe(a)

        // then:
        verify(nativeBus).subscribe(a)
    }

    @Test
    fun unsubscribe() {
        // given:
        val a = Any()

        // when:
        provider.get().unsubscribe(a)

        // then:
        verify(nativeBus).unsubscribe(a)
    }


}