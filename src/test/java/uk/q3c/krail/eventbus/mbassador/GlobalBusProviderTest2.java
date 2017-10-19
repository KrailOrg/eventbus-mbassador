package uk.q3c.krail.eventbus.mbassador;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.Test;
import uk.q3c.krail.eventbus.GlobalBusProvider;

/**
 * Created by David Sowerby on 19 Oct 2017
 */

public class GlobalBusProviderTest2 {
    Injector injector;
    GlobalBusProvider provider;
    private MessageConsumer3 consumer;


    @Test(expected = com.google.inject.ProvisionException.class)
    public void publish() throws Exception {
        injector = Guice.createInjector(new GlobalBusModule());
        provider = injector.getInstance(GlobalBusProvider.class);
        consumer = injector.getInstance(MessageConsumer3.class);
    }


}