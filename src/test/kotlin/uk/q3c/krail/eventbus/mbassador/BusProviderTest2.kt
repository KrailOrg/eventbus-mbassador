package uk.q3c.krail.eventbus.mbassador

import com.google.inject.Guice
import org.assertj.core.api.Assertions.assertThat

/**
 * Checks that an incorrectly annotated message consumer causes no Guice failure
 *
 * Created by David Sowerby on 19 Oct 2017
 */

class BusProviderTest2 {

    fun publish() {
        // given:
        val injector = Guice.createInjector(EventBusModule())

        // when:
        injector.getInstance(MBassadorMessageBusProvider::class.java)
        injector.getInstance(MessageConsumerInvalidAnnotation::class.java)

        // then: no exception if we get here
        assertThat(true).isTrue()
    }


}