# FabricProxy-Lite

Same as [FabricProxy](https://github.com/OKTW-Network/FabricProxy) but only support velocity and using Fabric-API handle
velocity packet.

This will have the better compatibility with other mods.

## Setup

* Download mod
* Start server to generate config
* Setting velocity `player-info-forwarding-mode` to `modern` and `forwarding-secret`
* Setting `secret` in `config/FabricProxy-Lite.toml` match velocity config

## Config
Here are all the parameters that you can set in the `FabricProxy-Lite.toml` config file. Note that all properties require a value, but if the property isn't specified in the config file it'll just get populated by the default value. So the parameters that are marked as required needs a manually added value i.e. the Velocity forwarding secret.
| Name | Description | Default | Required |
|:--|:--|:-:|:-:|
| `hackOnlineMode` | | `true` | |
| `hackEarlySend` | Fabric-API can't send packet before QUERY_START event, so player info(UUID) will not ready at QUERY_START event. Setting `hackEarlySend` to `true` will use mixin for early send packet to velocity. <br><br> This is **required** by mods like i.e. LuckPerms.  | `false` | |
| `hackMessageChain` | This option fixes players being kicked for `Received chat packet with missing or invalid signature.` or `Chat message validation failure`, which only happens when the player switches to another server. <br><br> See [#30](/../../issues/30) for more info. | `true` | |
| `disconnectMessage` | The custom disconnect/kick message for users that aren't connecting through Velocity. | `"This server requires you to connect with Velocity."` | |
| `secret` | The Velocity forwarding secret. This should be the same random string as in Velocity's `forwarding.secret` file. <br><br> Optionally you could set the value by passing the `FABRIC_PROXY_SECRET` environmental variable. | | ✓ |
