# How to contribute:

## Contributors:

### Main contributors
Ghoulboy, gnembon, Firigion and replaceitem.

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
      1. Create an empty list at the top of the function:
        
        `affected=[]`
      2. When running your function, use the following format to set blocks and save them to that list at the same time.
         It is important that you save the block that was there before as well as the one that you just set after, for 
         the undo command to work.
          
         ```
         //Whenever you want to set a block (assume pos to the position in which you want to set the block)...
         preblock=block(pos);//saves the block that was there before
         if(set_block(pos,block,replacement)!=null,//this ensures that you only add blocks to undo list that were actually able to be set
            affected+=[pos,preblock,block(pos)]//This way the undo command ahs all the info necessary to undo the command
         );         
         //Continue with function...
         ```
      3. Lastly, you have to save your command to the player history, so they can undo it. This is an O(n) operation, so
         don't worry about lag (If you don't understand that sentence, then don't worry about it either). To do this, save
         the variable `affected` to a map, along with the name of the function, with the key `'type'`
         ```
         if(affected,//checking that there have actually been blocks set, or it breaks weirdly
            command={
               'type'->'function_name',//for undo history formatting
               'affected_positions'->affected
            };
            add_to_history(command, player)
         )
         ```
3. Add a command. If you are submitting a pr, then please don't worry about this if you don't understand, the contributors
   can do it for you. Otherwise, define it below the existing ones, and add extra arguments underneath as well. Existing
   ones are there too, if you feel like using them.
   
####Other functions

If you're doing something that changes the player's stored data, please ensure that:
   1. It doesn't break anything that was there, ensure you test it all. This includes setting blocks, undoing, redoing, etc.
   2. If you have questions about the code, ask the main contributors.
