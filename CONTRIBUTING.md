# How to contribute:

## Contributors:

### Main contributors
Ghoulboy, gnembon, Firigion, BisUmTo and replaceitem.

If you have questions, these are the people to bother first of all.

## Contributions:

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
         
3. Flags (@replaceitem, if u want put them here, if not put them underneath #### Other functions). This is docs for if they
   wanna add their own flags, idk hwo ur gonna implement
         
4. Add a command that the player can input to call this function. If you are submitting a pr, then please don't worry 
   about this if you don't understand, the contributors can do it for you. Otherwise, define it below the existing ones, 
   and add extra arguments underneath as well. Existing arg types are there too, if you feel like using them.
   
#### Other functions

If you're doing something that changes the player's stored data, please ensure that:
   1. It doesn't break anything that was there, ensure you test it all. This includes setting blocks, undoing, redoing, etc.
   2. If you have questions about the code, ask the main contributors.
