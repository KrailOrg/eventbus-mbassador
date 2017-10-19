package uk.q3c.krail.eventbus.mbassador

import net.engio.mbassy.bus.MBassador
import net.engio.mbassy.bus.config.IBusConfiguration
import org.slf4j.LoggerFactory
import uk.q3c.krail.eventbus.*
import java.util.concurrent.TimeUnit

class MBassadorMessageBus(nativeBus: MBassador<BusMessage>) : MBassadorGeneralEventBus<BusMessage>(nativeBus), MessageBus
class MBassadorEventBus(nativeBus: MBassador<Any>) : MBassadorGeneralEventBus<Any>(nativeBus), EventBus

open class MBassadorGeneralEventBus<T>(private val nativeBus: MBassador<T>) : GeneralEventBus<T> {
    private val log = LoggerFactory.getLogger(this.javaClass.name)
    override fun implementationName(): String {
        return nativeBus.runtime.get(EventBusModule.BUS_IMPLEMENTATION)
    }

    override fun implementation(): MBassador<T> {
        return nativeBus
    }

    override fun index(): Int {
        return nativeBus.runtime.get(EventBusModule.BUS_INDEX)
    }

    override fun scope(): String {
        return nativeBus.runtime.get(EventBusModule.BUS_SCOPE)
    }

    override fun busId(): String {
        return nativeBus.runtime.get(IBusConfiguration.Properties.BusId)
    }


    override fun hasPendingMessages(): Boolean {
        return nativeBus.hasPendingMessages()
    }

    override fun publishASync(message: T): MessageStatus {
        return MessageStatus(nativeBus.post(message).asynchronously())
    }

    override fun publishASync(timeout: Long, timeUnit: TimeUnit, message: T): MessageStatus {
        return MessageStatus(nativeBus.post(message).asynchronously())
    }

    override fun publishSync(message: T): MessageStatus {
        return MessageStatus(nativeBus.post(message).now())
    }

    override fun subscribe(listener: Any) {
        nativeBus.subscribe(listener)
        log.debug("{} subscribed to {}", listener.toString(), busId())
    }

    override fun unsubscribe(listener: Any): Boolean {
        return nativeBus.unsubscribe(listener)
    }
}

/**
 * Created by David Sowerby on 24 Oct 2017
 */
//class MBassadorGeneralEventBus<T> @Inject constructor(nativeBus: MBassador<T>) : AbstractMBassadorEventBus<T>(nativeBus)

