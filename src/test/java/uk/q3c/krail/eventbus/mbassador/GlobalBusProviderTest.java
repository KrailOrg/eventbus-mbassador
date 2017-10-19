package uk.q3c.krail.eventbus.mbassador;

import com.google.inject.Guice;
import com.google.inject.Injector;
import net.engio.mbassy.bus.MBassador;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by David Sowerby on 19 Oct 2017
 */

public class GlobalBusProviderTest {
    Injector injector;
    GlobalBusProvider provider;
    private MessageConsumer consumer;
    private MessageConsumer2 consumer2;


    @Before
    public void setup() {
        injector = Guice.createInjector(new GlobalBusModule());
        provider = injector.getInstance(GlobalBusProvider.class);
        consumer = injector.getInstance(MessageConsumer.class);
        consumer2 = injector.getInstance(MessageConsumer2.class);
    }

    @Test
    public void publish() throws Exception {
        // given
        MBassador<BusMessage> bus = provider.get();

        // when
        bus.publish(new TestMessage("publish"));
        bus.publishAsync(new TestMessage("publishAsync"));


        // then
        assertThat(consumer.msgs).containsExactly("publish", "publishAsync");
        assertThat(consumer2.msgs).containsExactly("publish", "publishAsync");
    }


}