# FabricProxy-Lite

This mod does the same thing as [FabricProxy](https://github.com/OKTW-Network/FabricProxy) but only suppors Velocity, and uses the Fabric-API to handle
Velocity packets.

This will have better compatibility with other mods.

## Important

**LuckPerms on Velocity requires you to enable `hackEarlySend` in the mod's config.**

Because Fabric-API can't send packets before QUERY_START event, so player info(UUID) will not ready at QUERY_START event.

Enabling `hackEarlySend` will use mixin to send packets to Velocity early.

## Setup

* Download mod
* Start server to generate config
* Set Velocity `player-info-forwarding-mode` to `modern` and `forwarding-secret`
* Set `secret` in `config/Fabric-Lite.toml` to match your Velocity config.toml
