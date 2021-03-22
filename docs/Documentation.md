# Scarpet-Edit (docs)

## Description

This is a culmination of many various unofficial projects attempting to recreate a World-Edit-like functionality, with 
the initiative started by gnembon:
![img.png](img.png)

The main contributors to this project are:
 - Ghoulboy     (GitHub: Ghoulboy78, Discord: Ghoulboy#7234)
 - gnembon      (GitHub: gnembon, Discord: gnembon#2170)
 - Firigion     (GitHub: Firigion, Discord: Firigion#7498)
 - replaceitem  (GitHub: replaceitem, Discord: replaceitem#9118)
 - BisUmTo      (GitHub: BisUmTo, Discord: BisUmTo#8383)
 - altrisi	 (GitHub: altrisi, Discord: altrisi#9772)

## How to use
If you are running an outdated version of fabric-carpet, WorldEdit for scarpet won't work. Currently, the oldest compatible
version is: carpet 1.4.22

### Area selection

This is done by left-clicking with the wand (default is wooden sword)

It will pop up a grid, and it will follow your mouse (hovering 5 blocks in midair if need be) until you left click again.
Left clicking again will reselect the whole box.

### Commands:
 - `/se fill <block> [replacement]` -> Fills selection with `<block>`. If `[replacement]` is given, it only fills
    replacing that block or block tag.
 - `/se undo [moves]` -> Undoes last move performed by player or as many as specified by `[moves]`. This can be
    redone with `/se redo`.
 - `/se undo all` -> Undoes entire undo history. Careful!
 - `/se undo history` -> Displays entire undo history.
 - `/se redo [moves]` -> Redoes `[moves]` amount of moves previously undone by the player, or one if not specified.
    Also shows up in undo history so you can re- undo them.
 - `/se redo all` -> Redoes all undone moves. (These can be re- undone by regular /undo command)
 - `/se wand <wand>` -> Sets wand to held item, or if your hand is empty, gives you the wand. With optional `[wand]`
    argument, it sets wand to specified item
 - `/se rotate <pos> <degrees> <axis>` -> Rotates selection `degrees` degrees about point `pos`. Axis must be `x`,
    `y` or `z`. NB: This will look funky if you do not use multiples of 45 or 90.
 - `/se stack [stackcount] [direction]` -> Stacks selection in the direction the player is looking if not otherwise
    specfied. By defaults, it stacks one time.
 - `/se expand <pos> <magnitude>` -> Expands selection by whatever magnitude specified by player, from pos `pos`
 - `/se move <pos>` -> Moves selection to `pos`
 - `/se copy` -> Copies selection to clipboard. By default, will not override the existing clipboard (can be changed
    by adding keyword `force`), and will also take the positions relative to position of player.
 - `/se copy <pos>` -> Copies selection to clipboard, with positions relative to `pos`. This is significant when 
    pasting blocks, in terms of how it is pasted.
 - `/se paste` -> Pastes selection relative to player position. Be careful in case you didn't choose a wise spot
    when making the selection.
 - `/se paste <pos>` -> Pastes selection relative to `pos`
 - `/se flood <block>` -> Performs a flood fill (fill connex volume) within the selection and starting at the 
    player's position. Can both "fill"
 what used to be air or replace some other block.
 - `/se flood <block> <axis>` -> Flood fill will happen only perpendicular to an axis. Setting axis to `y`, for 
    instance, will fill the horizontal plane.
 - `/se structure list` -> Lists all available structures. Currently, they are all in the same file as the lang
    files, this may change soon. You can add other structure files, and they will load properly 
 - `/se structure load <structure name> <pos?>` -> Loads a structure relative to `pos`, or relative to player 
    position if not specified.
 - `/se structure save <name> entities?|force?` -> Saves current selection to a `.nbt` file compatible with vanilla 
    structure blocks. `entities` will make it save entities, and `force` will override an existing structure with the same
    name.
 - `/se structure save <name> clipboard force?` -> Saves current clipboard to a `.nbt` file compatible with vanilla 
    structure blocks.`force` will override an existing structure with the same name. Gives an error if no clipboard is 
    present. Will also copy entities.
 - `/se structure delete <name>` -> Deletes a structure file called `name`.
 - `/se copy [pos]` -> Copies selection to clipboard setting the origin of the structure at `[pos]`, if given, or
    the curren player position, if not. By default, will not override the existing clipboard (can be changed by adding
    keyword `force`), and will also take the positions relative to position of player.
 - `/se paste [pos]` -> Pastes selection relative to player position or to `[pos]`, if given. Be careful incase 
    you didnt' choose a wise spot making the selection.
 - `/se flood <block>` -> Performs a flood fill (fill connex volume) within the selection and starting at the 
    player's position. Can both "fill" used to be air or replace some other block.
 - `/se flood <block> [axis]` -> Flood fill will happen only perpendicular to iven axis. Setting axis to `y`, for
    instance, will fill the horizontal plane.
 - `/se walls <block> [sides] [replacement]` -> Creates walls on the sides specified around the selection, defaults
    to ony vertical walls (`xz`).
 - `/se outline <block> [replacement]` -> Outlines the selection with `<block>`.
 - `/se shape ...` -> Generates a shape centered arround the palyer. See brushes for all options and parameters.
 - `/se drain [radius]` -> Drains the liquid the player is standing on. By default, it does so on a given radius, but you can set that with the optional parameter. If
   you don't specify a radius and are standing in a seleciton, it will drain within the selection only. It supports the `-g` flag, try it!
 - `/se walls <block> [sides] [replacement]` -> Creates walls on the sides specified around the selection, defalts to ony vertical walls (`xz`).
 - `/se outline <block> [replacement]` -> Outlines the selection with `<block>`.
 - `/se shape ...` -> Generates a shape centered arround the palyer. See brushes for all options and parameters.
 - `/se angel [new|clear]` -> Angel block: click in mid air to place a (stone) block you can the build off. Without arguments, it gives the player the item registered
  as angel block. With `new`, it will register the currently held item as angel block item. With `clear` clears the current angel block item; you will have to register a new one
  to use it.
 - `/se up <distance>` -> Teleports you up `<distance>` ammount of blocks. Places a block under you if there was nothing
   there, so you don't fall and can start building right away.

#### Brushes

`brushes` let you attach some actions or commands to specific items to use them at a distance. When registering an action
with `/se brush <action> <arguments>`, the held item type will be converted into a brush and right clicking with
it will perform the registered action at the highlighted block. To view the currently registered brushes use `/se brush list`.
To remove a brush from said list or get more info on the registered action for that brush, hold the corresponding item type
and use `/se brush clear` or `/se brush info`, respectively.

The available actions for brushes are:
- `cube <block> <size> [replacement]` -> creates a cube out of `block` and with side length `size`, replacing only blocks
    that match `replacement` (block or tag), if given.
- `cuboid <block> <x> <y> <z> [replacement]` -> creates a cuboid out of `block` and with side lengths `x`, `y` and `z`, 
    replacing only blocks that match `replacement` (block or tag), if given.
- `sphere <block> <radius> [replacement]` -> creates a sphere out of `block` and with `radius`, replacing only blocks that
    match `replacement` (block or tag), if given.
- `ellipsoid <block> <x_radius> <y_radius> <z_radius> [replacement]` -> creates an ellipsoid with different radii on each
    axis, replacing only blocks that match `replacement` (block or tag), if given.
- `cylinder <block> <radius> <height> [axis] [replacement]` -> creates a cylinder out of `block` and with `radius` and 
    `height` along `axis` (if given; else, defaults to `y` for a vertical cylinder), replacing only blocks that match 
    `replacement` (block or tag), if given.
- `cone <block> <radius> <height> [signed_axis] [replacement]` -> creates a cone out of `block` and with `radius` and 
    `height` along `signed_axis` (if given; else, defaults to `+y` for a vertical cone pointing up), replacing only blocks
    that match `replacement` (block or tag), if given.
- `prism_polygon <block> <radius> <height> <vertices> [axis] [rotation] [replacement]` -> generates a prism with the base
    of a regular polygon inscribed in a circle of `radius` with `vertices` amount of sides and height `height` along `axis`.
    Optionally, it can be rotated from its base orientation.
- `prism_star <block> <outer_radius> <inner_radius> <height> <vertices> [axis] [rotation] [replacement]` -> generates a 
    star whose points touch a circle of radius `outer_radius` with `vertices` amount of points. Said star is the base 
    for a prism of height `height` along `axis`. Optionally, it can be rotated from its base orientation.
- `line <block> [length] [replacement]` -> creates a line out of `block` between the player and the clicked block, replacing
    only blocks that match `replacement` (block or tag), if given. If `length` is given, the length of the line is fixed,
    and it only uses the clicked block to get the direction of the line.
- `flood <block> <radius> [axis]` -> creates a flood fill starting in the target block and modifying all the connex blocks to become `block`, always staying within `radius` blocks of the starting point. The flood happens in the plane perpendicular to `axis`, if given.
- `drain <radius>` -> Drains the liquids connected to the liquid block you click, up to `<radius>` blocks away.
- `hollow [radius]` -> hollows out the continuos blob of blocks starting in the clicked block, going no further from the starting point than `[radius]` (defaults to 50).
- `outline <block> [radius]` -> outlines the continuos blob of blocks startig in the clicked block with `[block]`, going no further from the starting point than `[radius]` (defaults to 50). If `force` is not true, it will only place blocks replacing air.
- `paste` -> pastes the current clipboard, using the targeted block as origin.
- `feature <fearure>` -> places a feature (decoration) in the targeted location. Can fail, if natural feature would fail. DOES NOT SUPPORT `undo` functionality.
- `spray <block> [size] [count] [replacement]` -> creates a spray paint effect: projects `[count]` (100 by default) random rays around the volume the player is looking at in a cone with `[size]` (12 degrees, by default) angle aperture and places `<block>` in a random patter.
- `spray held_item [size] [count] [replacement]` -> same as abve, but it uses the item held _in the offhand_ intead of a set item. Useful when changing block often, to avoid needing to create multiple brushes.

All brush functions can be appended with flags, same as fill commands, adding `f -<flags>` at the end of the regular commands.

#### Flags

All the above commands which can set (not save) blocks, can also take flags. eg: `/fill <block> f <flags>`. Flags syntax
is the same as in original WE, i.e a dash before the flag. All available flags will be suggested, but not all will have
an effect in each case.

Available flags:

 - `-u` -> Places blocks without block updates
 - `-w` -> Water-logs blocks placed in water or in other waterlogged blocks, air included
 - `-d` -> Removes water and waterlogged state from placed blocks. Applies before `-w`
 - `-p` -> Only replaces air blocks when setting an area
 - `-e` -> Copies/moves entities from old location to new location. Technically, a new entity is generated with same data
    and position within the structure as the old one, so all that changes is UUID. Undoing will not remove these entities
 - `-b` -> Copies old biomes to new location.
 - `-a` -> Pasting a structure will not paste the air blocks within the structure.
 - `-s` -> Preserves block states when setting blocks
 - `-g` -> When replacing air or water, greenery corresponding to each medium will be replaced too
 - `-h` -> When creating a shape, makes it hollow
 - `-l` -> When used to register a brush, the brush will targer liquids as well as blocks, instead of going right through them to the block behind.
