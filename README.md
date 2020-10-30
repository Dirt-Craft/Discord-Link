# Discord-Link
Handles gamechats on the DirtCraft Discord.

Requires:
- ~~LuckPerms~~
- Sponge-Discord-Lib
- UltimateChat

Todo:
 - More usage of schedulers (Gamechat sender, Discord-Command sender, etc. to bulk up on send events.)
 - Integrate some parts into  Sponge-Discord-Lib, or merge the two (Like how we do it with spigot/bungee?)
 - Create an API and separate from implementation, so plugins can abstractly use it, and random class changes won't break shit.
 - Move verification commands over to the bungee version, allowing network-wide verification