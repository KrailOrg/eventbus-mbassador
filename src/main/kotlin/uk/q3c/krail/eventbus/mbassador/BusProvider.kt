package uk.q3c.krail.eventbus.mbassador

import com.google.inject.Inject
import com.google.inject.Singleton
import net.engio.mbassy.bus.MBassador
import uk.q3c.krail.eventbus.BusMessage
import uk.q3c.krail.eventbus.EventBusProvider
import uk.q3c.krail.eventbus.MessageBusProvider

/**
 * Provides a singleton message bus using MBassador
 *
 * Created by David Sowerby on 22 Oct 2017
 */
@Singleton
class MBassadorMessageBusProvider @Inject constructor(nativeBus: MBassador<BusMessage>) : MessageBusProvider {
    private val bus: MBassadorMessageBus = MBassadorMessageBus(nativeBus)

    override fun get(): MBassadorMessageBus {
        return bus
    }

}

/**
 * Provides a singleton event bus using MBassador
 *
 * Created by David Sowerby on 22 Oct 2017
 */
@Singleton
class MBassadorEventBusProvider @Inject constructor(nativeBus: MBassador<Any>) : EventBusProvider {
    private val bus: MBassadorEventBus = MBassadorEventBus(nativeBus)

    override fun get(): MBassadorEventBus {
        return bus
    }
}

