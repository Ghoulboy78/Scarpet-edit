//World edit

__config()->{
    'commands'->{
        'fill <block>'->['fill',null],
        'fill <block> <replacement>'->'fill',
        'undo all'->['undo', 0],
        'undo'->['undo', 1],
        'undo <moves>'->'undo',
        'redo all'->['redo', 0],
        'redo'->['redo', 1],
        'redo <moves>'->'redo',
        'undo history'->'print_history',
        'wand <wand>'->_(wand)->(global_wand=wand:0),
        'rotate <pos> <degrees> <axis>'->'rotate',//will replace old stuff if need be
        'stack'->['stack',1,null],
        'stack <stackcount>'->['stack',null],
        'stack <stackcount> <direction>'->'stack',
        'clone <pos>'->['clone',false],
        'move <pos>'->['clone',true],
        'selection clear' -> 'clear_selection',
        'selection expand' -> _() -> selection_expand(1),
        'selection expand <amount>' -> 'selection_expand',
        'selection move' -> _() -> selection_move(1, null),
        'selection move <amount>' -> _(n) -> selection_move(n, null),
        'selection move <amount> <direction>' -> 'selection_move',
    },
    'arguments'->{
        'replacement'->{'type'->'blockpredicate'},
        'moves'->{'type'->'int','min'->1,'suggest'->[]},//todo decide on whether or not to add max undo limit
        'degrees'->{'type'->'int','suggest'->[]},
        'axis'->{'type'->'term','options'->['x','y','z']},
        'wand'->{'type'->'item','suggest'->['wooden_sword','wooden_axe']},
        'direction'->{'type'->'term','options'->['north','south','east','west','up','down']},
        'stackcount'->{'type'->'int','min'->1,'suggest'->[]},
        'flags'->{'type'->'text'},
        'amount'->{'type'->'int'},
    }
};
//player globals

global_wand = 'wooden_sword';
global_history = [];
global_undo_history = [];


//Extra boilerplate

global_affected_blocks=[];

//Block-selection

global_selection = [];
global_selection_markers = [];

clear_markers() -> for(global_selection_markers, modify(_, 'remove'));

_create_marker(pos, block) ->
(
    marker = create_marker(null, pos+0.5, block, false);
    modify(marker, 'effect', 'glowing', 72000, 0, false, false);
    marker
);

clear_selection() ->
(
    global_selection = [];
);

selection_move(amount, direction) ->
(
    [from, to, point1, point2] = _get_current_selection_details(null);
    p = player();
    if (p == null && direction == null, exit('To move selection in the direction of the player, you need to have a player'));
    translation_vector = if(direction == null, get_look_direction(p)*amount, pos_offset([0,0,0],direction, amount));
    clear_markers();
    point1 = point1 + translation_vector;
    point2 = point2 + translation_vector;
    global_selection = [point1, point2];
    global_selection_markers = [_create_marker(point1, 'lime_concrete'), _create_marker(point2, 'blue_concrete')];
);

selection_expand(amount) ->
(
    [from, to, point1, point2] = _get_current_selection_details(null);
    for (range(3),
        size = to:_-from:_+1;
        c_amount = if (size >= -amount, amount, floor(size/2));
        if (point1:_ > point2:_, c_amount = - c_amount);
        point1:_ += -c_amount;
        point2:_ +=  c_amount;
    );
    global_selection = [point1, point2];
    clear_markers();
    global_selection_markers = [_create_marker(point1, 'lime_concrete'), _create_marker(point2, 'blue_concrete')];
);

__on_player_swings_hand(player, hand) ->
(
    if(player~'holds':0==global_wand,
        if (global_selection && length(global_selection)>1, clear_selection() );
        if (!global_selection, _set_start_point(player), _set_end_point(player) );
    )
);

_set_start_point(player) -> 
(
    clear_markers();
    start_pos = _get_player_look_at_block(player, 4.5);
    global_selection = [start_pos];
    marker = _create_marker(start_pos, 'lime_concrete');
    global_selection_markers = [marker];
    if (!global_rendering, _render_selection_tick(player~'name'));
);

_set_end_point(player) ->
(
    end_pos = _get_player_look_at_block(player, 4.5);
    global_selection:1 = end_pos;
    marker = _create_marker(end_pos, 'blue_concrete');
    global_selection_markers += marker;
    if (!global_rendering, _render_selection_tick(player~'name'));
);

global_rendering = false;
_render_selection_tick(player_name) ->
(
    p = player(player_name);
    if (!global_selection || !p,
        global_rendering = false;
        clear_markers();
        return()
    );
    global_rendering = true;
    active = (length(global_selection) == 1);
    [from, to, point1, point2] = _get_current_selection_details(p);
    if (active, draw_shape('box', 1, 'from', point2, 'to', point2+1, 'line', 1, 'color', 0x0000FFFF, 'fill', 0x0000FF55 ));
    draw_shape('box', if(active, 1, 40), 'from', from, 'to', to+1, 'line', 3, 'color', if(active, 0x00ffffff, 0xAAAAAAff));
    schedule(if(active, 1, 20), '_render_selection_tick', player_name);
);

_get_player_look_at_block(player, range) ->
(
    block = query(player, 'trace', range, 'blocks');
    if (block,
        pos(block)
    ,
        map(pos(player)+player~'look'*range+[0, player~'eye_height', 0], floor(_));
    )
);

_get_current_selection(player) -> slice(_get_current_selection_details(player), 0, 2);

_get_current_selection_details(player)->
(
    pos=global_selection;
    if(length(pos)==0,
        exit('Missing selection for operation')
    );
    end_pos= if (
        length(pos)==2,     pos:1,
        player == null,     exit('Operation require selection to be specified'),
        _get_player_look_at_block(player, 4.5)
    );
    zipped = map(pos:0, [_, end_pos:_i]);
    [map(zipped, min(_)), map(zipped, max(_)), pos:0, end_pos]
);

//Misc functions

get_look_direction(player) -> (
    look = player~'look';
    mi = reduce(look,if(abs(look:_a)<=abs(_),_i,_a),0);
    dir = [0,0,0];
    dir:mi = look:mi/abs(look:mi);
    dir;
);

//Command processing functions

set_block(pos,block,replacement)->(//use this function to set blocks
    success=null;
    existing = block(pos);
    if(block != existing && (!replacement || _block_matches(existing, replacement) ),
        postblock=set(existing,block);
        success=existing;
        global_affected_blocks+=[pos,postblock,existing];//todo decide whether to replace all blocks or only blocks that were there before action (currently these are stored, but that may change if we dont want them to)
    );
    bool(success)//cos undo uses this
);

_block_matches(existing, block_predicate) ->
(
    [name, block_tag, properties, nbt] = block_predicate;

    (name == null || name == existing) &&
    (block_tag == null || block_tags(existing, block_tag)) &&
    all(properties, block_state(existing, _) == properties:_) &&
    (!tag || tag_matches(block_data(existing), tag))
);


add_to_history(function,player)->(

    if(length(global_affected_blocks)==0,return());//not gonna add empty list to undo ofc...
    command={
        'type'->function,
        'affected_positions'->global_affected_blocks
    };

    print(player,format('gi Filled '+length(global_affected_blocks)+' blocks'));
    global_affected_blocks=[];
    global_history+=command;
);

print_history()->(
    player=player();
    history = global_history;
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
    history=global_history;
    if(length(history)==0||history==null,exit(print(player,format('r No actions to undo for player '+player))));
    if(length(history)<moves,print(player,'Too many moves to undo, undoing all moves for '+player);moves=0);
    if(moves==0,moves=length(history));
    for(range(moves),
        command = history:(length(history)-1);//to get last item of list properly

        for(command:'affected_positions',
            set_block(_:0,_:2,null)//todo decide whether to replace all blocks or only blocks that were there before action (currently these are stored, but that may change if we dont want them to)
        );

        delete(history,(length(history)-1))
    );
    global_undo_history+=global_affected_blocks;//we already know that its not gonna be empty before this, so no need to check now.
    print(player,format('gi Successfully undid '+moves+' operations, filling '+length(global_affected_blocks)+' blocks'));
    global_affected_blocks=[];
);

redo(moves)->(
    player=player();
    history=global_undo_history;
    if(length(history)==0||history==null,exit(print(player,format('r No actions to redo for player '+player))));
    if(length(history)<moves,print(player,'Too many moves to redo, redoing all moves for '+player);moves=0);
    if(moves==0,moves=length(history));
    for(range(moves),
        command = history:(length(history)-1);//to get last item of list properly

        for(command,
            set_block(_:0,_:2,null);
        );

        delete(history,(length(history)-1))
    );
    global_history+={'type'->'redo','affected_positions'->global_affected_blocks};//Doing this the hacky way so I can add custom goodbye message
    print(player,format('gi Successfully redid '+moves+' operations, filling '+length(global_affected_blocks)+' blocks'));
    global_affected_blocks=[];
);

fill(block,replacement)->(
    player=player();
    [pos1,pos2]=_get_current_selection(player);
    volume(pos1,pos2,set_block(pos(_),block,replacement));

    add_to_history('fill', player)
);

rotate(centre, degrees, axis)->(
    player=player();
    [pos1,pos2]=_get_current_selection(player);

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
        set_block(_,rotation_map:_,null)
    );

    add_to_history('rotate', player)
);

clone(new_pos, move)->(
    player=player();
    [pos1,pos2]=_get_current_selection(player);

    min_pos=map(pos1,min(_,pos2:_i));
    clone_map={};
    translation_vector=new_pos-min_pos;

    volume(pos1,pos2,
        put(clone_map,pos(_)+translation_vector,block(_));//not setting now cos still querying, could mess up and set block we wanted to query
        set_block(pos(_),if(has(block_state(_),'waterlogged'),'water','air'),null)//check for waterlog
    );

    for(clone_map,
        set_block(_,clone_map:_,null)
    );

    add_to_history(if(move,'move','clone'), player)
);

stack(count,direction) -> (
    player=player();
    translation_vector = if(direction == null, get_look_direction(player), pos_offset([0,0,0],direction));
    [pos1,pos2]=_get_current_selection(player);
    clone_map={};
    translation_vector = translation_vector*map(pos1-pos2,abs(_)+1);


    loop(count,
        c = _;
        offset = translation_vector*(c+1);
        volume(pos1,pos2,
            set_block(pos(_)+offset,_,null);
        );
    );
    add_to_history('stack', player)
);
