# Replenish Enchantment

This plugin adds a enchantment for hoes that automatically replants seeds when harvesting, making farming more efficient.

## Installation

* Download the latest version here: https://www.spigotmc.org/resources/name.000000/
* Drag the .jar into your plugins/ folder
* Reboot your server
* Set up the config.yml and language.yml located in the /plugins/ReplenishEnchantment/ directory

## Usage

### Commands
* /r-getbook - gives you an Enchanted Book with the Replenish Enchantment on it
* /r-givebook <Player> - gives <Player> an Enchanted Book with the Replenish Enchantment on it
* /r-gethoe <Material> [<full-enchanted>] - gives you a Hoe of the Material <Material> with the Replenish Enchantment on it. When <full-enchanted> is set and is true the hoe will also be enchanted with
   1. Efficiency V
   2. Fortune III
   3. Mending
   4. Unbreaking III
* /r-givehoe <Player> <Material> [<full-enchanted>] - gives <Player> a Hoe of the Material <Material> with the Replenish Enchantment on it. <full-enchanted> works just like above

### Permissions
The Permissions can be viewed and edited in the config.yml located in the /plugins/ReplenishEnchantment/ directory

### Messages
The Messages can be viewed and edited in the language.yml located in the /plugins/ReplenishEnchantment/ directory

### Ingame
* Get the Enchanted Book via the commands above
* Apply the Enchantment onto a hoe, this can be done,
   1. by combining a hoe, with the Enchanted Book on an anvil (if enabled in the config.yml)
   2. by dragging the Enchanted Book onto a hoe in the inventory and clicking it in survival mode (if enabled in the config.yml)
* Or just get a hoe with a command from above.
* Destroy some crops with this hoe.

The enchantment only works in survival mode!

## API

```java
// TODO
```

## Support
If you have any problems, found a bug, or have improvement ideas feel free to open an issue.
I will do my best to help you out.

## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

## Todo list

* Improve this file
   1. Add API Usage
   2. Add command usage examples
   3. Add planed section
   4. Add compatibility section
* Check Plugin-Compatibility
* Create a Wiki
* bStats
* Publish the Plugin

## License

[MIT](LICENSE)
