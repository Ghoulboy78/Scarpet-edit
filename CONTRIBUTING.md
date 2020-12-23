# How to contribute:

## Contributors:

### Main contributors
Ghoulboy, gnembon, Firigion, BisUmTo and replaceitem.

If you have questions, these are the people to bother first of all.

## Contributions:

### Make a PR for your contribution

This applies to everyone, including admins. The PR needs at least 2 reviews before it can be merged.

### Adding new functions:

#### Block manipulating commands

If you're gonna add a new function that manipulates blocks in the world, you should first familiarize yourself with the
existing code, so you know what to do right off the bat, but here is the tl;dr, in case it was too confusing:

1. Define the function below all the other defined functions (i.e, underneath the comment which says `//Command functions`).
   This helps with legibility of your code later on.
2. Add the essential boilerplate code. This is necessary for the undo command to work properly with all commands:
   
    - Get the player as a variable. `world_edit.sc` is a player_scoped app, but this is necessary for printing messages
      to that specific player.
      
      `player=player()`

    - Your function probably needs to access the player's positions. This function will throw the appropriate errors if 
      the player doesn't have the positions defined. NB: This is useful, as you can use these positions however you want.
      
        `[pos1,pos2]=_get_player_positions(player);`

    - If you're setting blocks, you need to be able to undo that. Here's how:
        
      1. When running your function, just use `set_block(pos,block,replacement` to set the block in the world.
   
      2. Secondly, you have to save your command to the player history, so they can undo it. This is an O(n) operation, so
         don't worry about lag (If you don't understand that sentence, then don't worry about it either). To do this, just
         run the function `add_to_history(your_function_name, player)` and it will all be handled behind the scenes.
         
3. Your additions should take flags into account. If you want your command to support flags, there needs to be two versions of each command you add. One with flags, one with out them. For example, the fill command would look like this:
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
   Make sure your command processing function accepts a flags argument as the last parameter, and add that in the command syntax. For the versions without the flags, just use `null` as the flags.
   
   Whenever you use the `set_block()` function, put the flags from your command processing function at the last argument.
   The following flags currently exist:
   ```
   u     no blockupdates (handled by set_block)
   w     waterlog blocks that get placed inside water (handled by set_block)
   a     only replace air blocks (handled by set_block)
   e     copy/move entities as well
   b     copy/move biomes as well
   ```

4. Add a command that the player can input to call this function. If you are submitting a pr, then please don't worry 
   about this if you don't understand, the contributors can do it for you. Otherwise, define it below the existing ones, 
   and add extra arguments underneath as well. Existing arg types are there too, if you feel like using them.
   
#### Messages

If you want to print a message as an output to the player, then use the `_print(message_id,player))` function. Input the string in the format:
`message_id=  Your message` into the list which is under the `//translation` comment, under all the rest. If the message
requires variables to be put in (like number, etc), just use `%s` in the message to stand for that value, it will be taken 
care of by the rest of the app. NB: This message will appear in US english. If you want to translate for other languages,
you need to add the US english *and* your own language's option.

#### Other functions

If you're doing something that changes the player's stored data, please ensure that:
   1. It doesn't break anything that was there, ensure you test it all. This includes setting blocks, undoing, redoing, etc.
   2. If you have questions about the code, ask the main contributors.
