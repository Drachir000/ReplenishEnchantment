# Replenish Enchantment

This plugin adds an enchantment for hoes that automatically replants seeds when harvesting, making farming more efficient.

## Installation

* Download the latest version here: https://www.spigotmc.org/resources/replenish-enchantment.107292/
* Drag the .jar into your plugins/ folder
* Reboot your server
* Set up the config.yml and language.yml located in the /plugins/ReplenishEnchantment/ directory

## Usage

### Commands
* `/r-get BOOK` - gives you an Enchanted Book with the Replenish Enchantment on it
* `/r-give BOOK <Player>` - gives \<Player> an Enchanted Book with the Replenish Enchantment on it
* `/r-get HOE <Material> [<full-enchanted>]` - gives you a Hoe of the Material \<Material> with the Replenish Enchantment on it. When \<full-enchanted> is set and is true the hoe will also be enchanted with
    - Efficiency V
    - Fortune III
    - Mending
    - Unbreaking III
* `/r-give HOE <Player> <Material> [<full-enchanted>]` - gives \<Player> a Hoe of the Material \<Material> with the Replenish Enchantment on it. \<full-enchanted> works just like above
* `/r-get AXE <Material> [<full-enchanted>]` - gives you an Axe of the Material \<Material> with the Replenish Enchantment on it. \<full-enchanted> works just like above
* `/r-give AXE <Player> <Material> [<full-enchanted>]` - gives \<Player> an Axe of the Material \<Material> with the Replenish Enchantment on it. \<full-enchanted> works just like above

### Permissions
The Permissions can be viewed and edited in the config.yml located in the /plugins/ReplenishEnchantment/ directory

### Messages
The Messages can be viewed and edited in the language.yml located in the /plugins/ReplenishEnchantment/ directory

### Ingame
* Get the Enchanted Book via the commands above
* Apply the Enchantment onto a hoe, this can be done,
    - by combining a hoe or axe, with the Enchanted Book on an anvil (if enabled in the config.yml)
    - by dragging the Enchanted Book onto a hoe or axe in the inventory and clicking it in survival mode (if enabled in the config.yml)
* Or just get a hoe or axe with a command from above.
* Destroy some crops with this hoe or axe.

The enchantment only works in survival mode!

Currently, there is no way to get the enchantment in survival yet, but you can add your own ones.
Here are some examples:
* A Villager who trades a book and 32 emeralds for the enchanted book: 
    ````
    /summon minecraft:villager ~ ~ ~ {VillagerData:{type:plains,profession:farmer,level:2},Offers:{Recipes:[{buy:{id:book,Count:1},buyB:     {id:emerald,Count:32},sell:{id:enchanted_book,Count:1,tag:{StoredEnchantments:[{id:"replenishenchantment:replenish",lvl:1}],display:{Lore:   ['[{"text":"Replenish","italic":false,"color":"gray"}]']}}}}]}}
    ````
* You could also just add the Enchanted Book, or an enchanted tool to a Shopkeepers shop, if you have the [Shopkeepers](https://www.spigotmc.org/resources/shopkeepers.80756/) plugin installed.
* A Citizens NPC giving an Enchanted Book to the players every 15 minutes
  - first create a npc with ``/npc create <name>``
  - configure it just like you want it to be, for more information about Citizens look [here](https://wiki.citizensnpcs.co/Citizens_Wiki)
  - add the command like this:
    
    ``/npc cmd add r-get BOOK -o --cooldown 900``

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
<dependencies>
  <dependency>
    <groupId>de.drachir000.survival</groupId>
    <artifactId>replenishenchantment</artifactId>
    <version>1.0.0</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
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
If you want to give your players the Enchanted Book, or directly an enchanted hoe or axe, you can use the `buildBook()`, `buildHoe()` or `buildHoe()` method which returns an ItemStack depending on the arguments.
````java
// Get the api like above

Material hoeMaterial = // ... The Material of the Hoe (must be a hoe)
Material axeMaterial = // ... The Material of the Axe (must be a axe)
boolean fullEnchanted = // ... Whether the hoe or axe should have Efficiency V, Fortune III, Mending and Unbreaking III. If you only want some of theese you have to add them yourself.

ItemStack book = api.buildBook();
ItemStack hoe = api.buildHoe(hoeMaterial, fullEnchanted);
ItemStack axe = api.buildAxe(axeMaterial, fullEnchanted);

player.getInventory.addItem(book, hoe, axe);
````

* #### Other Item Utils
    - Applying the Enchantment:
    ````java
    // Get the api like above
    
    ItemStack item = // ...
    
    api.applyEnchantment(item); // For hoes and axes
    api.addStoredEnchant(item); // For enchantment storages like enchanted books
    
    // Do what ever you want with your enchanted Item
    ````
    - Checking for the Enchantment:
    ````java
    // Get the api like above
    
    ItemStack item = // ...
    
    if (isEnchanted(item)) { // For hoes and axes
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
6. [x] Adding Cocoa Beans
7. [x] Creating a public maven repo
8. [x] Reworking the commands
9. [ ] Adding option, to disable the whole enchantment part and enable replenish for every hoe/axe
10. [ ] Adding an option for a Crafting-Recipe

## License

[MIT](LICENSE)
