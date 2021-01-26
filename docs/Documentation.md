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
First, ensure you are running fabric-carpet 1.4.22 or above, or the area selection will not work.

### Area selection

This is done by left-clicking with the wand (default is wooden sword)

It will pop up a grid, and it will follow your mouse (hovering 5 blocks in midair if need be) until you left click again.
Left clicking again will reselect the whole box.

### Commands:
 - `/world-edit fill <block>` -> Fills selected area with given block
 - `/world-edit fill <block> <replacement>` -> Same as above, but also replacing blocks
 - `/world-edit undo` -> Undoes last move performed by the player. This can be redone with `/world-edit redo`
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
 - `/world-edit stack <stackcount>` -> Stacks selection specified number of times in direction player is looking in
 - `/world-edit stack <stackcount> <direction>` -> Stacks selection specified number of times in direction specified by
    player
 - `/world-edit expand <pos> <magnitude>` -> Expands selection by whatever magnitude specified by the player, from pos `pos`
 - `/world-edit move <pos>` -> Moves selection to `pos`
 - `/world-edit copy` -> Copies selection to clipboard. By default, will not override the existing clipboard (can be changed
    by adding keyword `force`), and will also take the positions relative to position of player.
 - `/world-edit copy <pos>` -> Copies selection to clipboard, with positions relative to `pos`. This is significant when 
    pasting blocks, in terms of how it is pasted.
 - `/world-edit paste` -> Pastes selection relative to player position. Be careful in case you didn't choose a wise spot
    when making the selection.
 - `/world-edit paste <pos>` -> Pastes selection relative to `pos`
 - `/world-edit flood <block>` -> Performs a flood fill (fill connected volume) within the selection and starting at the 
    player's position. Can both "fill" what used to be air or replace some other block.
 - `/world-edit flood <block> <axis>` -> Flood fill will happen only perpendicular to an axis. Setting axis to `y`, for 
    instance, will fill the horizontal plane.
 - `/world-edit shape ...` -> Generates a shape centered around the player. See brushes for all options and parameters.

#### Brushes

`brushes` let you attach some actions or commands to specific items to use them at a distance. When registering an action
with `/world-edit brush <action> <arguments>`, the held item type will be converted into a brush and right clicking with
it will perform the registered action at the highlighted block. To view the currently registered brushes use `/world-edit brush list`.
To remove a brush from said list or get more info on the registered action for that brush, hold the corresponding item type
and use `/world-edit brush clear` or `/world-edit brush info`, respectively.

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
- `flood <block> <radius> [axis]` -> creates a flood fill starting in the target block and modifying all the connected blocks
    to become `block`, always staying within `radius` blocks of the starting point. The flood happens in the plane 
    perpendicular to `axis`, if given.
- `paste` -> pastes the current clipboard, using the targeted block as origin.
- `feature <feature>` -> places a feature (decoration) in the targeted location. Can fail, if natural feature would fail.
    DOES NOT SUPPORT `undo` functionality.

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
