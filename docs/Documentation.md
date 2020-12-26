# WorldEdit for scarpet (docs)

## Description

This is a culmination of many various unofficial WE projects, with the initiative started by gnembon:
![img.png](img.png)

The main contributors to this project are:
 - Ghoulboy     (GitHub: Ghoulboy78, Discord: Ghoulboy#7234)
 - gnembon      (GitHub: gnembon, Discord: gnembon#2170)
 - Firigion     (GitHub: Firigion, Discord: Firigion#7498)
 - replaceitem  (GitHub: replaceitem, Discord: replaceitem#9118)
 - BisUmTo      (GitHub: BisUmTo, Discord: BisUmTo#8383)

## How to use
First of all, ensure you are running fabric-carpet 1.4.22 or above, or the area selection will not work.

### Area selection

This is done by left-clicking with the wand (default wooden sword)

It will pop up a grid, and it will follow your mouse (hovering 5 blocks in midair if need be) until you left click again.
Left clicking again will reselect the whole box.

### Commands:
 - `/world-edit fill <block>` -> Fills selected area with given block
 - `/world-edit fill <block> <replacement>` -> Same as above, but also replacing blocks
 - `/world-edit undo` -> Undoes last move performed by player. This can be redone with `/world-edit redo`
 - `/world-edit undo all` -> Undoes entire undo history
 - `/world-edit undo <moves>` -> Undoes specific number of moves
 - `/world-edit undo history` -> Displays entire undo history
 - `/world-edit redo` -> Redoes a move undone by the player. Also shows up in undo history
 - `/world-edit redo all` -> Redoes all undone moves
 - `/world-edit redo <moves>` -> Redoes specific number of moves
 - `/world-edit wand` -> Sets wand to held item, or if your hand is empty, gives you the wand.
 - `/world-edit wand <wand>` -> Sets wand to specified item
 - `/world-edit rotate <pos> <degrees> <axis>` -> Rotates selection `degrees` degrees about point `pos`. Axis must be `x`,
    `y` or `z`.
 - `/world-edit stack` -> Stacks selection once in direction player is looking in
 - `/world-edit stack <stackcount>` -> Stackes selection specified number of times in direction player is looking in
 - `/world-edit stack <stackcount> <direction>` -> Stacks selection specified number of times in direction specified by
    player
 - `/world-edit expand <pos> <magnitude>` -> Expands selection by whatever magnitude specified by player, from pos `pos`
 - `/world-edit move <pos>` -> Moves selection to `pos`
 - `/world-edit copy` -> Copies selection to clipboard. By default, will not override the existing clipboard (can be changed
    by adding keyword `force`), and will also take the positions relative to position of player.
 - `/world-edit copy <pos>` -> Copies selection to clipboard, with positions relative to `pos`. This is significant when 
    pasting blocks, in terms of how it is pasted.
 - `/world-edit paste` -> Pastes selection relative to player position. Be careful incase you didnt' choose a wise spot
    when making the selection.
 - `/world-edit paste <pos>` -> Pastes selection relative to `pos`
 - `/world-edit flood <block>` -> Performs a flood fill (fill connex volume) within the selection and starting at the player's position. Can both "fill"
 what used to be air or replace some other block.
 - `/world-edit flood <block> <axis>` -> Flood fill will happen only perpendicular to iven axis. Setting axis to `y`, for isntance, will fill the horizontal plane.

#### Flags

All the above commands which can set (not save) blocks, can also take flags. eg: `/fill <block> f <flags>`. Flags syntax
is the same as in original WE, i.e a dash before the flag. All available flags will be suggested, but not all will have
an effect in each case.

Available flags:

 - `-u` -> Places blocks without block updates
 - `-w` -> Waterlogs blocks placed in water or in other waterlogged blocks
 - `-p` -> Only replaces air blocks when setting an area
 - `-e` -> Copies/moves entities from old location to new location. Technically, a new entity is generated with same data
    and position within the structure as the old one, so all that changes is UUID. Undoing will not remove these entities
 - `-b` -> Copies old biomes to new location.
 - `-a` -> Pasting a structure will not paste the air blocks within the structure.

