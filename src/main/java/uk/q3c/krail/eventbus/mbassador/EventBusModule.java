/*
 *
 *  * Copyright (c) 2016. David Sowerby
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  * the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 *  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 *  * specific language governing permissions and limitations under the License.
 *
 */

package uk.q3c.krail.eventbus.mbassador;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.listener.Listener;
import uk.q3c.krail.eventbus.BusMessage;
import uk.q3c.krail.eventbus.EventBusProvider;
import uk.q3c.krail.eventbus.MessageBusProvider;

/**
 * Configures MBassador EventBus and MessageBus implementations for Singleton scope.
 * All classes annotated with {@link Listener} are subscribed to the bus.
 * <p>
 * Created by David Sowerby on 08/03/15.
 */
public class EventBusModule extends AbstractModule {
    public final static String BUS_SCOPE = "bus_scope";
    public final static String BUS_INDEX = "bus_index";
    public final static String BUS_IMPLEMENTATION = "bus_implementation";

    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        /**
         * Use of scope specific interfaces/providers is recommended in place of using annotated constructor parameters.
         * This avoids an annotated constructor parameter in a super-class being ignored / overridden in a sub-class
         */

        final Provider<MBassadorMessageBusProvider> messageBusProviderProvider = this.getProvider(MBassadorMessageBusProvider.class);
        final Provider<MBassadorEventBusProvider> eventBusProviderProvider = this.getProvider(MBassadorEventBusProvider.class);


        bindListener(new ListenerAnnotationMatcher(), new BusTypeListener(messageBusProviderProvider, eventBusProviderProvider));
        bindPublicationErrorHandlers();
        bindMessageBusConfiguration();
        bindMessageBusProvider();
        bindEventBusProvider();


    }

    protected void bindMessageBusProvider() {
        bind(MessageBusProvider.class).to(MBassadorMessageBusProvider.class);
    }

    protected void bindEventBusProvider() {
        bind(EventBusProvider.class).to(MBassadorEventBusProvider.class);
    }


    protected void bindMessageBusConfiguration() {
        bind(IBusConfiguration.class).toInstance(singletonBusConfig());
    }

    @Provides
    protected MBassador<BusMessage> providesMBassadorWithBusMessage(IBusConfiguration configuration, IPublicationErrorHandler publicationErrorHandler) {
        configuration.addPublicationErrorHandler(publicationErrorHandler);
        configuration.setProperty(IBusConfiguration.Properties.BusId, "Global Message Bus");
        MBassador<BusMessage> nativeBus = createNativeBus(configuration);
        nativeBus.getRuntime()
                .add(EventBusModule.BUS_SCOPE, "Singleton")
                .add(EventBusModule.BUS_INDEX, 1)
                .add(EventBusModule.BUS_IMPLEMENTATION, "MBassador");
        return nativeBus;
    }

    @Provides
    protected MBassador<Object> providesMBassadorWithAny(IBusConfiguration configuration, IPublicationErrorHandler publicationErrorHandler) {
        configuration.addPublicationErrorHandler(publicationErrorHandler);
        configuration.setProperty(IBusConfiguration.Properties.BusId, "Global Event Bus");
        MBassador<Object> nativeBus = createNativeBus(configuration);
        nativeBus.getRuntime()
                .add(EventBusModule.BUS_SCOPE, "Singleton")
                .add(EventBusModule.BUS_INDEX, 1)
                .add(EventBusModule.BUS_IMPLEMENTATION, "MBassador");
        return nativeBus;
    }

    protected <T> MBassador<T> createNativeBus(IBusConfiguration configuration) {
        return new MBassador<T>(configuration);
    }

    /**
     * Override this method to provide alternative or additional bindings.
     */
    protected void bindPublicationErrorHandlers() {
        bind(IPublicationErrorHandler.class).to(DefaultEventBusErrorHandler.class);
    }

    /**
     * Override this to define your configuration.  Refer to the MBassador documentation at
     * https://github.com/bennidi/mbassador/wiki/Configuration for more information about the configuration itself.
     *
     * @return configuration object for the SingletonBus
     */
    protected IBusConfiguration singletonBusConfig() {
        final IBusConfiguration config = new BusConfiguration().addFeature(Feature.SyncPubSub.Default())
                .addFeature(Feature.AsynchronousHandlerInvocation.Default())
                .addFeature(Feature.AsynchronousMessageDispatch.Default());
        return config;
    }

    /**
     * Matches classes annotated with {@link Listener}
     */
    private static class ListenerAnnotationMatcher extends AbstractMatcher<TypeLiteral<?>> {
        @Override
        public boolean matches(TypeLiteral<?> t) {
            Class<?> rawType = t.getRawType();
            return rawType.isAnnotationPresent(Listener.class);
        }
    }

    private static class BusTypeListener implements TypeListener {
        private Provider<MBassadorMessageBusProvider> messageBusProviderProvider;
        private Provider<MBassadorEventBusProvider> eventBusProviderProvider;

        /**
         * We need a "provider provider" because this listener is invoked before injector is fully resolved
         *
         * @param eventBusProviderProvider
         */
        public BusTypeListener(Provider<MBassadorMessageBusProvider> messageBusProviderProvider, Provider<MBassadorEventBusProvider> eventBusProviderProvider) {
            this.messageBusProviderProvider = messageBusProviderProvider;
            this.eventBusProviderProvider = eventBusProviderProvider;
        }

        /**
         * The logic for auto subscribing can be changed by providing an alternative implementation of
         * EventBusAutoSubscriber, but it has to be created using 'new' here, because the Injector is not yet complete
         *
         * @param type
         * @param encounter
         * @param <I>
         */
        public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
            encounter.register(new DefaultEventBusAutoSubscriber(messageBusProviderProvider.get(), eventBusProviderProvider.get()));
        }
    }
}
