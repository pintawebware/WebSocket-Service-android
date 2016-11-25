## WebSocket Autobahn Service on Android

This repo contains a module that allows to perform Call, Publish and Subscribe Wamp requests over a secured connection (wss protocol) with [Autobahn WebSocket][1] library.
If websocket close connection there is periodic task (heartbeat) to make reconnection in indicated interval.

### Usage

Make connection to websocket, response callback registration needed:

```java
WsManager.getWsManager()
        .setPort(String port)
        .setLog(boolean isLogOn)
        .setHeartBeat(long heartBeatPeriodInMillis)
        .connect(Context context);
```

Make disconnect in websocket, response callback un-registration not needed:

```java
WsManager.getWsManager().disconnect(Context context);
```

Register callback to receive responses from websocket:

```java
WsManager.getWsManager().registerCallback(Context context, WsCallbackListeners wsCallbackListeners);
```

Unregister callback to receive responses from websocket:

```java
WsManager.getWsManager().unregisterCallback(Context context);
```

[1]: http://autobahn.ws
