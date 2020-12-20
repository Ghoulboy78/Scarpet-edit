//World edit

__config()->{
    'stay_loaded'->true,
    'scope'->'player',
    'commands'->{
        'fill <block>'->['fill',null],
        'fill <block> <replacement>'->'fill',
        'undo all'->['undo', 0],
        'undo last'->['undo', 1],
        'undo <moves>'->'undo',
        'undo history'->'print_history',
        'wand <wand>'->_(wand)->(global_player_data:'wand'=wand),
        'rotate <pos> <degrees> <axis>'->'rotate',//will replace old stuff if need be
        'clone <pos>'->['clone',false],
        'move <pos>'->['clone',true]
    },
    'arguments'->{
        'replacement'->{'type'->'blockpredicate'},
        'moves'->{'type'->'int','min'->1,'suggest'->[]},//todo decide on whether or not to add max undo limit
        'degrees'->{'type'->'int','suggest'->[]},
        'axis'->{'type'->'term','options'->['x','y','z']},
        'wand'->{'type'->'item','suggest'->['wooden_sword','wooden_axe']}
    }
};
//Setting up player data

global_player_data={};

create_player_data()->(
    global_player_data={
        'wand'->'wooden_sword',
        'history'->[],
        'positions'->[]
    }
);

create_player_data();//todo change to a load player data command later when we figure out when/how to save to disk

__on_player_connects(player) ->(
    if(!global_player_data,
        create_player_data()
    )
);

//Block-selection

_select_pos(player,pos)->(//in case first position is not selected
    if(length(global_player_data:'positions')==0,
        global_player_data:'positions':0=pos;
        print('Set first position to '+pos),
        global_player_data:'positions':1=pos;
        print('Set second position to '+pos)
    )
);

__on_player_clicks_block(player, block, face) ->(
    if(player~'holds':0==global_player_data:'wand',
        global_player_data:'positions':0=pos(block);
        print('Set first position to '+pos(block))
    )
);

__on_player_breaks_block(player, block) ->(//incase we made an oopsie with a non-sword want item
    if(player~'holds':0==global_player_data:'wand',
        global_player_data:'positions':0=pos(block);
        without_updates(set(pos(block),block));
        print('Set first position to '+pos(block))
    )
);

__on_player_uses_item(player, item_tuple, hand) ->(
    if(item_tuple:0==global_player_data:'wand'&&(block=query(player,'trace',128,'blocks')),
        _select_pos(player, pos(block))
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
    pos=global_player_data:'positions';
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
    global_player_data:'history'+=command;
);

print_history()->(
    player=player();
    history = global_player_data:'history';
    if(length(history)==0||history==null,print(player,'No undo history to show for player '+player));
    if(length(history)>10,print('Undo history for player '+player+' is very long, showing only the last ten items'));
    total=min(length(history),10);//total items to print
    for(range(total),
        command=history:(length(history)-(_+1));//getting last 10 items in reverse order
        print(player,str(
            '%s: type: %s\n'+
            '    affected positions: %s',
            history~command+1,command:'type',length(command:'affected_positions')
        ))
    )

);

//Command functions

undo(moves)->(
    player = player();
    history=global_player_data:'history';
    if(length(history)==0||history==null,exit(print(player,format('r No actions to undo for player '+player))));//incase an op was running command, we want to print error to them
    if(length(history)<moves,print(player,'Too many moves to undo, undoing all moves for '+player);moves=0);
    if(moves==0,moves=length(history));
    affected=0;
    for(range(moves),
        command = history:(length(history)-1);//to get last item of list properly

        for(command:'affected_positions',
            affected+=set_block(_:0,_:2,null)!=null;//todo decide whether to replace all blocks or only blocks that were there before action (currently these are stored, but that may change if we dont want them to)
        );

        delete(history,(length(history)-1))
    );
    print(player,format('gi Successfully undid '+moves+' operations, filling '+affected+' blocks'));
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

rotate(centre, degrees, axis)->(
    player=player();
    [pos1,pos2]=_get_player_positions(player);
    affected=[];
    rotation_map={};
    rotation_matrix=[];
    if( axis=='x',
        rotation_matrix=[
            [1,0,0],
            [0,cos(degrees),-sin(degrees)],
            [0,sin(degrees),cos(degrees)]
        ],
        axis=='y',
        rotation_matrix=[
            [cos(degrees),0,sin(degrees)],
            [0,1,0],
            [-sin(degrees),0,cos(degrees)]
        ],//axis=='z'
        rotation_matrix=[
            [cos(degrees),-sin(degrees),0],
            [sin(degrees),cos(degrees),0]
            [0,0,1],
        ]
    );

    volume(pos1,pos2,
        block=block(_);//todo rotating stairs etc.
        prev_pos=pos(_);
        subtracted_pos=prev_pos-centre;
        rotated_matrix=rotation_matrix*[subtracted_pos,subtracted_pos,subtracted_pos];//cos matrix multiplication dont work in scarpet, yet...
        new_pos=[];
        for(rotated_matrix,
            new_pos+=reduce(_,_a+_,0)
        );
        new_pos=new_pos+centre;
        put(rotation_map,new_pos,block)//not setting now cos still querying, could mess up and set block we wanted to query
    );

    for(rotation_map,
        preblock=block(_);
        if(set_block(_,rotation_map:_,null)!=null,affected+=[_,block(_),preblock])
    );
    if(affected,
        command={
            'type'->'rotate',
            'affected_positions'->affected
        };
        add_to_history(command, player)
    )
);

clone(new_pos, move)->(
    player=player();
    [pos1,pos2]=_get_player_positions(player);
    affected=[];
    min_pos=[//i feel like theres a smarter way to do this.
        min(pos1:0,pos2:0),
        min(pos1:1,pos2:1),
        min(pos1:2,pos2:2)
    ];
    clone_map={};
    translation_vector=new_pos-min_pos;

    volume(pos1,pos2,
        put(clone_map,pos(_)+translation_vector,block(_))//not setting now cos still querying, could mess up and set block we wanted to query
        set(_,if(has(block_state(_),'waterlogged'),'water','air'));//check for waterlog
    );

    for(clone_map,
        preblock=block(_);
        if(set_block(_,clone_map:_,null)!=null,affected+=[_,block(_),preblock])
    );
    if(affected,
        command={
            'type'->if(move,'move','clone'),
            'affected_positions'->affected
        };
        add_to_history(command, player)
    )
);

