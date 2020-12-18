//World edit

__config()->{
    'stay_loaded'->true,
    'scope'->'global',
    'commands'->{
        'fill <block>'->['fill',null],
        'fill <block> <replacement>'->'fill'
    },
    'arguments'->{
        'replacement'->{'type'->'blockpredicate'};
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

for(player('all'),create_player_data(_));//todo change to a load player data command later when we save to disk

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

add_to_history(command,player)->(

    affected_positions=command:'affected_positions';

    print(player,format('gi Filled '+length(affected_positions)+' blocks'));

    put(global_player_data:player:'history',null,command)
);

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
        exit(print(player,format('r No points selected for player '+player))),
        print(pos)
    );
    start_pos=pos:0;
    end_pos=if(pos:1,pos:1,pos(player));
    [start_pos,end_pos]
);


//Command functions

fill(block,replacement)->(
    player=player();
    [pos1,pos2]=_get_player_positions(player);
    print([pos1,pos2]);
    affected=[];
    volume(pos1,pos2,if(set_block(_,block,replacement),affected+=block(_)));
    command={
        'type'->'fill',
        'affected_positions'->affected
    };
    add_to_history(command, player)
);


