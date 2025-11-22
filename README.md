# QuickMark Server Plugin

A Spigot/Paper plugin that enables network communication for clients using the **[QuickMark](https://modrinth.com/mod/quickmark)** Fabric mod.
It allows the mod to exchange structured packets between players without relying on chat messages.

## 📥 Installation

1. Install on a Spigot or Paper server (1.21.7+ recommended).
2. Download the latest version:
   [Modrinth](https://modrinth.com/plugin/quickmark_pl) or [Github](https://github.com/UnknKriod/QuickMarkPlug/releases)
3. Place the `.jar` file into the `plugins` folder.
4. Restart the server.

No configuration is required.

## 🔌 Purpose

QuickMark supports two network channels:

1. **Plugin-based communication (this plugin)**
   Used automatically if the plugin is installed.
   Provides reliable and fast packet exchange between clients.

2. **Chat-based fallback**
   Used only if no plugin is detected.
   The mod sends messages starting with `quickmark://` that must not be blocked by chat filters.

Installing this plugin disables fallback mode and ensures stable communication.

## ✔ Compatibility

* Works only with players who have the QuickMark Fabric mod installed.
* Does not modify gameplay or require server-side rendering.
* No commands or permissions required.

## ❗ If You Do Not Use This Plugin

If you rely on fallback communication, make sure:

* Chat filters, anti-cheat plugins or formatting tools **do not remove or modify messages starting with `quickmark://`**.

If such messages are filtered, marker and team synchronization will stop working.

## 🧩 Related Projects

* Mod: [Modrinth](https://modrinth.com/mod/quickmark) [GitHub](https://github.com/UnknKriod/quickmark)
* Plugin: [Modrinth](https://modrinth.com/plugin/quickmark_pl)

## ⚙ Development

Build with standard Spigot plugin workflow. No additional dependencies required.
