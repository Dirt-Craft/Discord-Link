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
 - Discord commands in the dm processor (dms you back. Would require a redesign of the bot system, and custom senders etc.)
 - !kits
 
Changelog
  - 1.4.0
    - Updated to new JDA 4.2.0 release
    - General Codebase Refactors
    - Made command output nicer (Especially !help)
    - Added DM command parsing (Currently only does console commands ATM, logged.)
    - Added game construction event to tracked boot stages (boot embeds)
    - No longer initializes if JDA cannot be loaded, or any of the other deps.
    - Made only notify, help, shutdown commands can run pre-boot.
    - Made main class extend boot handler to stop bogus accusations of poor programming
    - Made message delete calls exceptionless to reduce log noise.
    - Stopped storing Role, Channels, Guild etc. Store long id's instead.
    - Made bot DM me on exception.
    - All commands delete the input message now
    - Added a changelog.
  
  - 1.3.1
    - !halt command - nukes the server
    - !unlink command - removes user from verified database
    - !sync command - /lp sync for admins
    - !ranks command - shows ranks
    - Load plugin at the earliest possible stage to allow for boot cancellation in the event of a thread-lock.
    - Add server watchdog, DMs listed people if a game-stage exceeds the time threshold. Fully customizable.
    - Add boot stage embeds so people can see the server boot in realtime.
    - Support for upcoming proxy module
    - Hide vanished players from leave/join messages, !list
    - New command feedback system, Stacks messages and sends all at once, eliminating embed spam and instead fills each embed before sending.
    
  - Prior
    - check git history pleb. I can't be bothered going back further, changelog starts from here.