# Replenish Enchantment

This plugin adds an enchantment for hoes that automatically replants seeds when harvesting, making farming more efficient.

## Installation

* Download the latest version here: https://www.spigotmc.org/resources/replenish-enchantment.107292/
* Drag the .jar into your plugins/ folder
* Reboot your server
* Set up the config.yml and language.yml located in the /plugins/ReplenishEnchantment/ directory

## Usage

### Commands
* `/r-getbook` - gives you an Enchanted Book with the Replenish Enchantment on it
* `/r-givebook <Player>` - gives \<Player> an Enchanted Book with the Replenish Enchantment on it
* `/r-gethoe <Material> [<full-enchanted>]` - gives you a Hoe of the Material \<Material> with the Replenish Enchantment on it. When \<full-enchanted> is set and is true the hoe will also be enchanted with
    - Efficiency V
    - Fortune III
    - Mending
    - Unbreaking III
* `/r-givehoe <Player> <Material> [<full-enchanted>]` - gives \<Player> a Hoe of the Material \<Material> with the Replenish Enchantment on it. \<full-enchanted> works just like above

### Permissions
The Permissions can be viewed and edited in the config.yml located in the /plugins/ReplenishEnchantment/ directory

### Messages
The Messages can be viewed and edited in the language.yml located in the /plugins/ReplenishEnchantment/ directory

### Ingame
* Get the Enchanted Book via the commands above
* Apply the Enchantment onto a hoe, this can be done,
    - by combining a hoe, with the Enchanted Book on an anvil (if enabled in the config.yml)
    - by dragging the Enchanted Book onto a hoe in the inventory and clicking it in survival mode (if enabled in the config.yml)
* Or just get a hoe with a command from above.
* Destroy some crops with this hoe.

The enchantment only works in survival mode!

## Compatibility
| Minecraft version | Bukkit | Spigot | Paper | Purpur |
|-------------------|--------|--------|-------|--------|
| **..1.16**        | no     | no     | no    | no     |
| **1.17**          | yes    | yes    | no    | no     |
| **1.18**          | yes    | yes    | yes   | yes    |
| **1.19**          | yes    | yes    | yes   | yes    |

## API

### Include the API:
Add this to your pom.xml:
````xml
<!-- repositories -->
<repository>
    <id>github</id>
    <url>https://maven.pkg.github.com/Drachir000/ReplenishEnchantment</url>
</repository>

<!-- dependencies -->
<dependency>
    <groupId>de.drachir000.survival</groupId>
    <artifactId>replenishenchantment</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
</dependency>
````
### Using the API
First you have to get an Instance of the REAPI class.
```java
REAPI api = REAPI.getAPI();
```
* #### Registering your own custom enchantments
This Plugin overwrites the vanilla anvil mechanics. If you want your own custom enchantments to have level costs without your users having to register them manually in the config.yml you can do it as following:
````java
// Get the api like above

Enchantment custom_enchantment = // ... your custom enchantment

int itemModifier = 2;
int bookModifier = 1;

api.registerEnchantment(custom_enchantment, itemModifier, bookModifier);
````
The multipliers times the final level of the enchantment gives the level cost of this enchantment while combining two items on the anvil. The book-modifier is used when the right item is an enchantment book, the item-modifier otherwise.
In this case the itemModifier is 2 and the bookModifier 1. You can modify this just as you wish or let your users configure it. When you want to change this values just call this method with the new values again.

* #### Getting the Enchanted Book or an Enchanted Hoe
If you want to give your players the Enchanted Book, or directly an enchanted hoe, you can use the `buildBook()` or `buildHoe()` method which returns an ItemStack depending on the arguments.
````java
// Get the api like above

Material hoeMaterial = // ... The Material of the Hoe (must be a hoe)
boolean fullEnchanted = // ... Whether the hoe should have Efficiency V, Fortune III, Mending and Unbreaking III. If you only want some of theese you have to add them yourself.

ItemStack book = api.buildBook();
ItemStack hoe = api.buildHoe(hoeMaterial, fullEnchanted);

player.getInventory.addItem(book, hoe);
````

* #### Other Item Utils
    - Applying the Enchantment:
    ````java
    // Get the api like above
    
    ItemStack item = // ...
    
    api.applyEnchantment(item); // For hoes
    api.addStoredEnchant(item); // For enchantment storages like enchanted books
    
    // Do what ever you want with your enchanted Item
    ````
    - Checking for the Enchantment:
    ````java
    // Get the api like above
    
    ItemStack item = // ...
    
    if (isEnchanted(item)) { // For hoes
    if (hasStoredEnchant(item)) { // For enchantment storages like enchanted books
    
        // Do what ever you want with your enchanted Item
    
    }
    ````
    - Get the Enchantment
    ````java
    // Get the api like above
    
    Enchantment replenish_enchantment = api.getEnchantment();
    
    // Do what ever you want with the Replenish Enchantment
    ````
    - Updating the lore
    ````java
    // Get the api like above
    
    ItemStack item = // ...
    
    api.updateLore(item);
    
    // Do what ever you want with your item
    ````

## Support
If you have any problems, found a bug, or have improvement ideas feel free to open an issue.
I will do my best to help you out.

## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

## Planed
1. [x] Fix Book Lore
2. [x] Adding Spigot & Bukkit support
3. [x] Adding Purpur support
4. [x] Enabling the Updater
5. [x] Adding Cactus & Sugar Cane
6. [ ] Adding Cocoa Beans
7. [ ] Adding an option for a Crafting-Recipe

## License

[MIT](LICENSE)
