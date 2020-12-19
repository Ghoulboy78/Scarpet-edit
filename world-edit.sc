//World edit

__config()->{
    'stay_loaded'->true,
    'scope'->'global',
    'commands'->{
        'fill <block>'->['fill',null],
        'fill <block> <replacement>'->'fill',
        'undo all'->['undo', 0, null],
        'undo last'->['undo', 1, null],
        'undo <moves>'->['undo',null],
        'undo <moves> <player>'->'undo'
    },
    'arguments'->{
        'replacement'->{'type'->'blockpredicate'},
        'moves'->{'type'->'int','min'->1,'suggest'->[]},//todo decide on whether or not to add max undo limit
        'player' -> {
           'type' -> 'term',
           'suggester' -> _(args) -> (
              nameset = {'Steve', 'Alex'};
              for(player('all'), nameset += _);
              keys(nameset)
           ),
        }
    }
};
//Setting up player data

global_player_data={};

create_player_data(player)->(
    global_player_data:player={
        'wand'->'wooden_sword',
        'history'->[],
        'positions'->[]
    }
);

for(player('all'),create_player_data(_));//todo change to a load player data command later when we figure out when/how to save to disk

__on_player_connects(player) ->(
    if(!has(global_player_data,player),
        create_player_data(player)
    )
);

//Block-selection

_select_pos(player,pos)->(//in case first position is not selected
    if(length(global_player_data:player:'positions')==0,
        global_player_data:player:'positions':0=pos;
        print('Set first position to '+pos),
        global_player_data:player:'positions':1=pos;
        print('Set second position to '+pos)
    )
);

__on_player_clicks_block(player, block, face) ->(
    if(player~'holds':0==global_player_data:player:'wand',
        global_player_data:player:'positions':0=pos(block);
        print('Set first position to '+pos(block))
    )
);

__on_player_breaks_block(player, block) ->(
    if(player~'holds':0==global_player_data:player:'wand',
        global_player_data:player:'positions':0=pos(block);
        without_updates(set(pos(block),block));
        print('Set first position to '+pos(block))
    )
);

__on_player_right_clicks_block(player, item_tuple, hand, block, face, hitvec)->(
    if(item_tuple:0==global_player_data:player:'wand',
        _select_pos(player, pos(block))
    )
);

__on_player_uses_item(player, item_tuple, hand) ->(
    if(item_tuple:0==global_player_data:player:'wand'&&query(player,'trace',5,'blocks')==null,//to make sure we r not triggering click block event
        pos=pos(block(pos(player)+[0,player~'eye_height',0]+player~'look'*5));//doing a pos(block(pos)) to get a properly rounded pos
        _select_pos(player, pos)
    )
);

//Command processing functions

set_block(pos,block,replacement)->(//use this function, by doing affected+=set_block(pos, block, replacement)
    success=null;
    existing = block(pos);
    if(block != existing && (!replacement || _block_matches(existing, replacement) ),
        set(existing,block);
        success=existing
    );
    success
);

_block_matches(existing, block_predicate) ->
(
    [name, block_tag, properties, nbt] = block_predicate;

    (name == null || name == existing) &&
    (block_tag == null || block_tags(existing, block_tag)) &&
    all(properties, block_state(existing, _) == properties:_) &&
    (!tag || tag_matches(block_data(existing), tag))
);

_get_player_positions(player)->(
    pos=global_player_data:player:'positions';
    if(length(pos)==0,
        exit(print(player,format('r No points selected for player '+player)))
    );
    start_pos=pos:0;
    end_pos=if(pos:1,pos:1,pos(player));
    [start_pos,end_pos]
);

add_to_history(command,player)->(

    affected_positions=command:'affected_positions';

    print(player,format('gi Filled '+length(affected_positions)+' blocks'));
    global_player_data:player:'history'+=command;
);

//Command functions

undo(moves, player)->(
    exec_player = player();//this is player running command, as opposed player whose moves are being undone (they may be the same, but jic they aren't)
    player=if(!player,player());
    if(exec_player!=player&&exec_player~'permission_level'<2,exit(print(exec_player,format('r You do not have permission to perform this command')))));//incase non-op tries to undo other player's moves. Same error as vanilla btw
    history=global_player_data:player:'history';
    if(length(history)==0,exit(print(exec_player,format('r No actions to undo for player '+player)));//incase an op was running command, we want to print error to them
    if(moves==0,moves=length(history));
    affected=0;
    for(range(moves),
        command = history:(moves-(_+1));//to get last item of list properly

        for(command:'affected_positions',
            affected+=set_block(_:0,_:2,null)!=null;//todo decide whether to replace all blocks or only blocks that were there before action (currently these are stored, but that may change if we dont want them to)
        );

        delete(history,(moves-(_+1)))
    );
    print(exec_player,format('gi Successfully undid '+moves+' operations, filling '+affected+' blocks'));
);

fill(block,replacement)->(
    player=player();
    [pos1,pos2]=_get_player_positions(player);
    affected=[];
    volume(pos1,pos2,preblock=block(_);if(set_block(_,block,replacement)!=null,affected+=[pos(_),preblock,block(_)]));
    if(affected,//not gonna add null action to undo history ofc...
        command={
            'type'->'fill',//may be useful later...
            'affected_positions'->affected
        };
        add_to_history(command, player)
    )
);


