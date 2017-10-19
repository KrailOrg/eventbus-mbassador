# eventbus-mbassador

[ ![Download](https://api.bintray.com/packages/dsowerby/maven/eventbus-mbassador/images/download.svg) ](https://bintray.com/dsowerby/maven/eventbus-mbassador/_latestVersion)

A Guice enabled implementation of the [mbassador](https://github.com/bennidi/mbassador) high performance event bus for use with [an implementation-independent API](https://github.com/davidsowerby/eventbus-api)

There are two singleton instances available, one which expects messages (that is, implementations of `BusMessage`), and the other which accepts `Object`


# Limitations

The publish element is implementation independent, and subscribe can be independent.  Handler implementations, however, but still require MBassador specific annotations 


### Build configuration

Available from JCenter only

**Gradle**

```
compile 'uk.q3c.krail:eventbus:x.x.x.x'
```

**Maven**

```
<dependency>
  <groupId>uk.q3c.krail</groupId>
  <artifactId>eventbus</artifactId>
  <version>x.x.x.x</version>
  <type>pom</type>
</dependency>
```

# Use

## Instantiation
Include `EventBusModule` as part of your Guice injector creation.  Inject the providers where you need them (usually only to publish messages):

- `MessageBusProvider`, or
- `EventBusProvider`

and call `provider.get()`

## Consumers / Subscribers

A class must be annotated with MBassador's `@Listener` annotation to receive events.  Without any further annotation, this will subscribe the class to the `MessageBus`
The class can also annotated with ``@SubscribeTo``, which enables explicit subscription to either `MessageBus`, `EventBus` or both

```java
@Listener
@SubscribeTo({GlobalMessageBus.class, GlobalEventBus.class})
public class WigglyThing {
}

```

## Handlers and Other Features

For a full description of the use of handlers, and the many other features available, see the [MBassador documentation](https://github.com/bennidi/mbassador)


