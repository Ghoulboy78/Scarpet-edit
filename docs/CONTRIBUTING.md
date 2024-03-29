# How to contribute:

## Contributors:

### Main contributors
Ghoulboy, gnembon, Firigion, BisUmTo, replaceitem, altrisi.

If you have questions, these are the people to bother first of all.

## Contributions:

### Make a PR for your contribution

This applies to everyone, including admins. The PR needs at least 2 reviews before it can be merged.

### Adding new functions:

#### Block manipulating commands

If you're gonna add a new function that manipulates blocks in the world with a player - utilisable function, you should 
first familiarize yourself with the existing code, so you know what to do right off the bat, but here is the tl;dr, in 
case it was too confusing:

1. Define the function below all the other defined functions (i.e, underneath the comment which says `//Command functions`).
   This helps with legibility of your code later on.
2. Add the essential boilerplate code. This is necessary for the undo command to work properly with all commands:
   
    - Get the player as a variable. `se.sc` is a player_scoped app, but this is necessary for printing messages
      to that specific player.
      
      `player=player()`

    - Your function probably needs to access the player's selected points. This function will throw the appropriate errors
      if the player doesn't have the positions defined. NB: This is useful, as you can use these positions however you
      want.
      
        `[pos1,pos2]=__get_current_selection(player);`

    - If you're setting blocks, you need to be able to undo that. Here's how:
        
      1. When running your function, just use `set_block(pos, block, replacement, flags, extras)` to set the block in the world.
   
      2. Secondly, you have to save your command to the player history, so they can undo it. This is an O(1) operation, 
         so don't worry about lag (If you don't understand that sentence, then don't worry about it either). To do this,
         just run the function `add_to_history('action_'+your_function_name, player)` and add that to the lang as a translation
         key, mapped to the en_us translation, which will be displayed to the player when they run `/undo history`. This
         is so people can translate the function name when converting to their language. The rest of the undo business 
         will be handled behind the scenes as long as you follow these steps.
         
3. If you need to set special blockstates or nbt values, you can do that with the `extra` argument of `set_block`. You
   can input a map with a key of `'state'` with the value of the map of blockstates, or a key of `'nbt'`, mapped to a map
   of the nbt. NB: You can simply input a map as the nbt, the `encode_nbt` function is called in `set_block`.
   
4. If it's the first time you are adding commands, you need to know how the commands preprocessor work. You have detailed
   instructions for it in [Command System](#command-system).
   
5. Your additions should take flags into account. If you want your command to set blocks, there needs to be two versions
   of each command you add. One with flags, one with out them. For example, the fill command would look like this:
   ```
   'fill <block>'
   'fill <block> <replacement>'
   ```
   then you need to copy those, and add the flags at the end:
   ```
   'fill <block>'
   'fill <block> <replacement>'
   'fill <block> f <flag>'
   'fill <block> <replacement> f <flag>'
   ```
   Make sure your command processing function accepts a flags argument as the last parameter, and add that in the command
   syntax. For the versions without the flags, pass use `null` as the `flags` argument.
   
   To access the flags as a map, run: `flags = _parse_flags(flags)`. Whenever you use the `set_block()` function, put the
   flags from your command processing function as the second to last argument. The following flags currently exist for you to use:
   ```
   u     no blockupdates (handled by set_block)
   w     waterlog blocks that get placed inside water (handled by set_block)
   p     only replace air blocks (handled by set_block)
   e     copy/move entities as well
   b     copy/move biomes as well (handled by set_block)
   a     don't paste air (handled by set_block)
   h     create hollow shapes
   d     "dry" out the placed set of blocks (remove water and waterlogged, handled by set_block)
   s     keep block states of replaced block, if new block matches (handled by set_block)
   g     when replacing air or water, some greenery gets repalced too (handled by set_block)
   l     when registering a brush with this flag, the brush will trace for liquids as well as blocks
   ```
   Biomes are handled by the `set_block` function, but you need to input the previous biome as a map in the `extra` 
   argument: `{'biome' -> biome}`, where the variable `biome` is the biome at the position you copied from. No need to 
   handle undoing, `set_block` does that on its own.

#### Brushes

If you are adding a new brush function, there is a simple syntax to follow to ensure it is compatible with the existing 
brush function utility. You must add your new function in the following fashion:

Add your function as an entry to the `global_brush_shapes` map variable, with the key being the string name of your function,
and the value being a lambda function with `(pos, args, flags)` as the arguments. This is where you can manipulate the 
world in whatever way you see fit, and must call the `add_to_history()` function (cos not all brush functions set blocks).
You can take whichever arguments you need from the `args` variable as long as you specify them in the input command.
You must also add a translation key for the action to the lang file. Finally, it's encouraged that devs add a list of parameters
to `global_brushes_parameters_map` to pretify the `brush info` command output. 

If this was too bulky and confusing too understand, here is a full example of  the `cube` function, which simply places
cubes:

1. First, we add an entry to the `global_brush_shapes` map variable which consists of the string `'cube'` mapped to the 
   block setting function:
      ```
   global_brush_shapes={
         ... //brush entries
         'cube'->_(pos, args, flags)->(//this can be like a template for new brushes as it's the simplest
            [block, size, replacement] = args;
            scan(pos,[size,size,size]/2, set_block(_, block, replacement, flags, {}));
            add_to_history('action_cube',player())
         ),
         ... //more entries
   }
      ```
   Here, we can see the lambda (`_(pos, args, flags)->`) which is called when you right click with the brush or use the
   `/shape` command.


2. We can see that we got three arguments: `[block, size, replacement] = args;`. What arguments the brush uses are defined
   in the argument signature. Here we have the default signature that includes the `help` info (see (#command-system)) and
   the duplicate command accepting flags. In this example `<replacement>` is a mandatory argument.
   ```
   base_commands_map = [
      ... //commands
      ['brush cube <block> <size> <replacement>', _(block, size_int, replacement) -> 
          brush('cube', null, block, size_int, replacement),
          [2, 'help_cmd_brush_cube', 'help_cmd_brush_generic', null]],
      ['brush cube <block> <size> <replacement> f <flag>', _(block, size_int, replacement, flags) -> 
          brush('cube', flags, block, size_int, replacement), false],
      ... //more commands
   ]
   ```
   If your brush can also be called as a shape, like it's the case for `cube`, add the corresponding set of commands by 
   copying the lines above and replacing `brush` with `shape`.


3. We can also see that we ran the `add_to_history` function with `'action_cube'`, not `'cube'`. This is because we need
   to then add an entry to the translations map (`global_default_lang`). This is so the action name can be translated to
   other languages when running the `/undo history` command. Add your translation key to
   the map, mapped to the en_us translation:
   ```
   global_default_lang = {
      ... //other entries
      'action_cube' -> 'cube',
      ... //more entries
   }
   ```

4. We finally add a list of the parameters the brush uses to `global_brushes_parameters_map` to be the display names when
  calling  `brush info`:
  ```
  global_brushes_parameters_map = {
    ... //other brushes
    'cube'-> ['block', 'size', 'replace'],
    ... //more brushes
  }
  ```

#### Messages

If you want to print a message as an output to the player, the easiest way is using the `_print(player, message_id, ...extra_args)`
function. This is important to be able to translate the message into other languages. Don't worry if you can't translate
your strings to other languages, but make sure that you follow these steps so that someone else can translate it. Input 
the string in the format:`'message_id' ->  'Your message',` into the map under the `//Translations` comment, under all 
the other output messages. Try to group your messages if all relate to the same function/command, so they are easier to 
find when translating or using them. If the message requires variables to be put in (like a number, or player, etc), just
use `%s` in the message to stand for that value, it will be taken care of by the rest of the app.

While the `_print()` function is useful in most of the cases, there are some situations where you can't work by directly
printing the message to the player. For those cases, there are two auxiliary functions for languages that can be useful 
in those situations:

- `_translate(message_id, ...extra_args)` Will give you the translated string directly, without even applying the `format()`
  to it, so you have full freedom over what you use it for
- `_error(player, message_id, ...extra_args` Will send the player the message and then immediately exit the function as 
  errored.

A message added with this method will appear in the default US English translation. If you want to translate to other 
languages, you just need to create a JSON file with your language's id (e.g. `it_it.json`), and add it into the `se.data/langs`
folder. When the file is in there, you'll be able to load it by using `/se lang [lang id]` ingame. The app will 
warn to the console if you try to load an incomplete language, including all missing keys so it's easier to translate.
While those are not available, it will use the default US English values for those.

#### Command System

In order to partially automate the help creation process, the command system in the app is different than the regular 
Carpet's command system. It is generated in a separate map and pre-proccessed before being passed to Carpet. You have to
add your commands into the `base_command_map` instead with the following format:

-  `[command_for_carpet, interpretation_for_carpet, false] (will hide it from help menu)`
-  `[command_for_carpet, interpretation_for_carpet, [optional_arguments_since, description, description_tooltip, description_action]]`

In words: the first two arguments are the usual key and value arguments in the `__config():'commands'` map when defining custom commands 
with scarpet the regular way. For example, they can be `'fill <block>'` and `'fill_with_blocks'`, where the latter is a user-defined 
function. The third element in the list is what defines the representation of the command in the help menu. Use `false` to hide the command
from the help menu (not every variation of a command needs it's individual entry), or see below for more info.

- `command_for_carpet`: it's the command "syntax" that will be passed to Carpet, the equivalent to the 
  key in regular commands map
- `interpretation_for_carpet`: it's how Carpet will process that command, the equivalent to value argument of a regular commands map
- `optional_arguments_since`: the index of the first optional argument in the command out of all arguments given. optional arguemtns will
  be printed as `[arg]`, and mandaroty ones as `<arg>` For isntance, setting it to `0` will make all arguments optional, while setting it to
  `2` means the first two arguments are mandatory. Set it to `-1` if all arguments are mandatory. This can be used to merge multiple commands
  into one help entry (for isntance, `help [page]` represents both the `help` and `help <page>` commands).
- `description`: The description of the command in the help menu. Must be a translation string (see [Messages](#messages))
  or a lambda (if you need arguments in the translation, `_()->_translate('string', ...args)`). (it can technically be 
  `null`, but the idea is to add a description)
- `description_tooltip`: An optional tooltip to show when the user is hovering the description. Can be `null`. If present,
  it must be a translation string or a lambda (if you need arguments in the translation)
- `description_action` An optional action to run when the player clicks the description. Can be `null`. If present, it must
  start with either `!` (to specify it will _run_ the action) or `?` (to specify it will _suggest_ the action). The command
  is automatically prefixed with `/world-edit ` (and a space)

The command suggestion will be derived from `command_for_carpet`: everything before the first `<`.

You should try to fit each entry in a single line (when viewed in the help menu) for proper pagination (until something 
is done).

#### Items with new functionality

Some items have built-in functionality, like the wand, but the user has the ability to add more items with actions associated
with them (like brushes and angel block, for instance). If you want to add a new item functionality (say, building wand, for 
example), then you need to take care of a few things to prevent one item from having multiple actions associated with it:
  * When registering an item for a new action, call `new_action_item(item, action)`. This will check if the item is already 
  registered for another action and call `_error()` if it is. Remember, that `exit`s, so there no need for an extra `return()` 
  call or anything, that function does the check for you.
  * When unregistering an item (not replacing it with a new one, like the wand does, actually deleting it), call 
  `remove_action_item(item, action)`, which will check that the requested item indeed has that action registered to it. It also 
  `exit`s if it fails. For isntance, `brush` uses it like this:
  ```C++
      if(
        action=='clear',
        remove_action_item(held_item, 'brush'); // delete old item from global_items_with_actions
        delete(global_brushes, held_item); // deregister item as brush
        ...
  ```
  * For these two functions to work properly, any new action you add needs a few things: 
      1. add your action's name to the `global_lang_keys` so it can be translated
      2. add your action's code name to `global_item_action_translation_key` so it can be requested from `global_lang_keys` (for example,
      the angel block's code name is `'angel'`, with a translation key `'action_item_angel'`, which gives the text `'angel block item'`)
      3. if your new action has a default tool or item (like the wand does), add it to `global_items_with_actions` along with its action
  * If registering a new item to your action just replaces the previous one, rather than adding a brand new one (think wand vs brush: one 
  replaces the old wand with a new one, while the other adds a new brush, leaving the old ones untouched), take good care to manually 
  delete the old item from `global_items_with_actions` just before adding the new one. Angel block for example does it like this:
  ```C++
  _set_angel_block_item() -> (
    new_action_item(item = player()~'holds':0, 'angel'); // add new item
    delete(global_items_with_actions, global_angel_block); // delete old one
    global_angel_block = item; // register new item as angel block
    ...
  ```
  This is extremely important so that old deregistered items can be reused for new actions by the player.


#### Other functions

If you're doing something that changes the player's stored data, please ensure that:
   1. It doesn't break anything that was there, ensure you test it all. This includes setting blocks, undoing, redoing, 
      etc.
   2. If you have questions about the code, ask the main contributors.
