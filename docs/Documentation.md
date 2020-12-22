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
 - `/world-edit wand <wand>` -> Sets wand to specified item
 - `/world-edit rotate <pos> <degrees> <axis>` -> Rotates selection `degrees` degrees about point `pos`. Axis must be `x`,
    `y` or `z`.
 - `/world-edit stack` -> Stacks selection once in direction player is looking in
 - `/world-edit stack <stackcount>` -> Stackes selection specified number of times in direction player is looking in
 - `/world-edit stack <stackcount> <direction>` -> Stacks selection specified number of times in direction specified by
    player
 - `/world-edit expand <pos> <magnitude>` -> Expands selection by whatever magnitude specified by player, from pos `pos`
 - `/world-edit clone <pos>` -> Clones selection to `pos`
 - `/world-edit move <pos>` -> Moves selection to `pos`



