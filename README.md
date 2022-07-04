# FabricProxy-Lite

Same as [FabricProxy](https://github.com/OKTW-Network/FabricProxy) but only support velocity and using Fabric-API handle
velocity packet.

This will have the better compatibility with other mods.

## Important

**LuckPerms need enable `hackEarlySend` in config.**

Because Fabric-API can't send packet before QUERY_START event, so player info(UUID) will not ready at QUERY_START event.

Enable `hackEarlySend` will use mixin for early send packet to velocity.

## Setup

* Download mod
* Start server to generate config
* Setting velocity `player-info-forwarding-mode` to `modern` and `forwarding-secret`
* Setting `secret` in `config/FabricProxy-Lite.toml` match velocity config
