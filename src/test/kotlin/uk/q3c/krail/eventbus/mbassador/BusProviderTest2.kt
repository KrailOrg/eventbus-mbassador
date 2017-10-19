package uk.q3c.krail.eventbus.mbassador

import com.google.inject.Guice
import org.junit.Test

/**
 * Checks that an incorrectly annotated message consumer causes Guice Provisioning exception
 *
 * Created by David Sowerby on 19 Oct 2017
 */

class BusProviderTest2 {

    @Test(expected = com.google.inject.ProvisionException::class)
    @Throws(Exception::class)
    fun publish() {
        val injector = Guice.createInjector(EventBusModule())
        val provider = injector.getInstance(MBassadorMessageBusProvider::class.java)
        val consumer = injector.getInstance(MessageConsumerInvalidAnnotation::class.java)
    }


}