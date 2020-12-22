//World edit

global_lang_ids = ['en_us','it_it'];//defining up here for command to work

__config()->{
    'commands'->{
        'fill <block>'->['fill',null,null],
        'fill <block> <replacement>'->['fill',null],
        'fill <block> f <flag>'->_(block,flags)->fill(block,null,flags),
        'fill <block> <replacement> f <flag>'->'fill',

        'undo'->['undo', 1],
        'undo all'->['undo', 0],
        'undo <moves>'->'undo',
        'undo history'->'print_history',

        'redo'->['redo', 1],
        'redo all'->['redo', 0],
        'redo <moves>'->'redo',
        'undo history'->'print_history',
        'wand' -> '_set_or_give_wand',
        'wand <wand>'->_(wand)->(global_wand=wand:0),

        'rotate <pos> <degrees> <axis>'->'rotate',//will replace old stuff if need be

	    'stack'->['stack',1,null,null],
        'stack <stackcount>'->['stack',null,null],
        'stack <stackcount> <direction>'->['stack',null],
        'stack f <flag>'->_(flags)->stack(1,null,flags),
        'stack <stackcount> f <flag>'->_(stackcount,flags)->stack(1,null,flags),
        'stack <stackcount> <direction> f <flag>'->'stack',

        'expand <pos> <magnitude>'->'expand',

        'clone <pos>'->['clone',false,null],
        'clone <pos> f <flags>'->_(pos,flags)->clone(pos,false,flags),

        'move <pos>'->['clone',true,null],
        'move <pos> f <flags>'->_(pos,flags)->move(pos,true,flags),

        'selection clear' -> 'clear_selection',
        'selection expand' -> _() -> selection_expand(1),
        'selection expand <amount>' -> 'selection_expand',
        'selection move' -> _() -> selection_move(1, null),
        'selection move <amount>' -> _(n) -> selection_move(n, null),
        'selection move <amount> <direction>' -> 'selection_move',
        'settings quick_select <bool>' -> _(b) -> global_quick_select = b,
        'lang <lang>'->_(lang)->(global_lang=lang)
    },
    'arguments'->{
        'replacement'->{'type'->'blockpredicate'},
        'moves'->{'type'->'int','min'->1,'suggest'->[]},//todo decide on whether or not to add max undo limit
        'degrees'->{'type'->'int','suggest'->[]},
        'axis'->{'type'->'term','options'->['x','y','z']},
        'wand'->{'type'->'item','suggest'->['wooden_sword','wooden_axe']},
        'direction'->{'type'->'term','options'->['north','south','east','west','up','down']},
        'stackcount'->{'type'->'int','min'->1,'suggest'->[]},
        'flag' -> {
            'type' -> 'term',
            'suggester' -> _(args) -> (
                typed = if(args:'flag', args:'flag', typed = '-');
                if(typed~'^-' == null, return());
                ret = [];
                for(global_flags_list,
                    if(length(_) == length(typed)+1 && _~typed != null, ret += _)
                );
                ret
            ),
            //'options' -> global_flags_list
        },
        'amount'->{'type'->'int'},
        'magnitude'->{'type'->'float','suggest'->[1,2,0.5]},
        'lang'->{'type'->'term','options'->global_lang_ids}
    }
};
//player globals

global_wand = 'wooden_sword';
global_history = [];
global_undo_history = [];
global_quick_select = true;


//Extra boilerplate

global_affected_blocks=[];

//Block-selection

global_selection = {}; // holds ids of two official corners of the selection
global_markers = {};

clear_markers(... ids) ->
(
    if (!ids, ids = keys(global_markers));
    for (ids,
        (e = entity_id(_)) && modify(e, 'remove');
        delete(global_markers:_);
    )
);

_create_marker(pos, block) ->
(
    marker = create_marker(null, pos+0.5, block, false);
    modify(marker, 'effect', 'glowing', 72000, 0, false, false);
    modify(marker, 'fire', 32767);
    id = marker ~ 'id';
    global_markers:id = {'pos' -> pos, 'id' -> id};
    id;
);

_get_marker_position(marker_id) -> global_markers:marker_id:'pos';

clear_selection() ->
(
    for(values(global_selection), clear_markers(_));
    global_selection = {};
);

selection_move(amount, direction) ->
(
    [from, to] = _get_current_selection();
    point1 = _get_marker_position(global_selection:'from');
    point2 = _get_marker_position(global_selection:'to');
    p = player();
    if (p == null && direction == null, exit(_translate('move_selection_no_player_error', [])));
    translation_vector = if(direction == null, get_look_direction(p)*amount, pos_offset([0,0,0],direction, amount));
    clear_markers(global_selection:'from', global_selection:'to');
    point1 = point1 + translation_vector;
    point2 = point2 + translation_vector;
    global_selection = {
        'from' -> _create_marker(point1, 'lime_concrete'),
        'to' -> _create_marker(point2, 'blue_concrete')
    };
);

selection_expand(amount) ->
(
    [from, to] = _get_current_selection();
    point1 = _get_marker_position(global_selection:'from');
    point2 = _get_marker_position(global_selection:'to');
    for (range(3),
        size = to:_-from:_+1;
        c_amount = if (size >= -amount, amount, floor(size/2));
        if (point1:_ > point2:_, c_amount = - c_amount);
        point1:_ += -c_amount;
        point2:_ +=  c_amount;
    );
    clear_markers(global_selection:'from', global_selection:'to');
    global_selection = {
        'from' -> _create_marker(point1, 'lime_concrete'),
        'to' -> _create_marker(point2, 'blue_concrete')
    };
);

__on_player_swings_hand(player, hand) ->
(
    if(player~'holds':0==global_wand,
        if (length(global_selection)<2,
            // finish selection
            if (!global_selection, _set_start_point(player), _set_end_point(player) );
        ,
            // selection is already made
            if (global_quick_select,
                clear_selection();
                _set_start_point(player)
            )
        )
    )
);

_set_start_point(player) ->
(
    clear_markers();
    start_pos = _get_player_look_at_block(player, 4.5);
    marker = _create_marker(start_pos, 'lime_concrete');
    global_selection = {'from' -> marker};
    if (!global_rendering, _render_selection_tick(player~'name'));
);

_set_end_point(player) ->
(
    end_pos = _get_player_look_at_block(player, 4.5);
    marker = _create_marker(end_pos, 'blue_concrete');
    global_selection:'to' = marker;
);

global_rendering = false;
_render_selection_tick(player_name) ->
(
    if (!global_selection,
        global_rendering = false;
        clear_markers(); // remove all selections, points etc. // debatable
        return()
    );
    global_rendering = true;
    p = player(player_name);
    active = (length(global_selection) == 1);

    start_marker = global_selection:'from';
    start = if(
        start_marker,  _get_marker_position(start_marker),
        p,             _get_player_look_at_block(p, 4.5)
    );

    end_marker = global_selection:'to';
    end = if(
        end_marker,              _get_marker_position(end_marker),
        p, _get_player_look_at_block(p, 4.5)
    );

    if (start && end,
        zipped = map(start, [_, end:_i]);
        from = map(zipped, min(_));
        to = map(zipped, max(_));
        draw_shape('box', if(active, 1, 40), 'from', from, 'to', to+1, 'line', 3, 'color', if(active, 0x00ffffff, 0xAAAAAAff));
        if (!end_marker,   draw_shape('box', 1, 'from', end, 'to', end+1, 'line', 1, 'color', 0x0000FFFF, 'fill', 0x0000FF55 ));
        if (!start_marker, draw_shape('box', 1, 'from', start, 'to', start+1, 'line', 1, 'color', 0xbfff00FF, 'fill', 0xbfff0055 ));
    );
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

_get_current_selection()->
(
    if( length(global_selection) < 2,
        exit(_translate('no_selection_error', []))
    );
    start = _get_marker_position(global_selection:'from');
    end = _get_marker_position(global_selection:'to');
    zipped = map(start, [_, end:_i]);
    [map(zipped, min(_)), map(zipped, max(_))]
);

//Misc functions

_set_or_give_wand() -> (
    p = player;
    //give player wand if hand is empty
    if(held_item_tuple = p~'holds' == null, 
       slot = invenroty_set(p, p~'selected_slot', 1, global_wand);
       return()
    );
    //else, set current held item as wand, if valid
    held_item = held_item_tuple:0;
    if( (['tools', 'weapons']~item_category(held_item)) != null,
        global_wand = held_item;
        print(p, str('%s is now the app\'s wand, use it with care.', held_item)),
       //else, can't set as wand
       print(p, 'Wand has to be a tool or weapon')
    )
);

//Config Parser

_parse_config(config) -> (
    if(type(config) != 'list', config = [config]);
    ret = {};
    for(config,
        if(_ ~ '^\\w+ ?= *.+$' != null,
            key = _ ~ '^\\w+(?= ?= *.+)';
            value = _ ~ ('(?<='+key+' ?= ?) *([^ ].*)');
            ret:key = value
        )
    );
    ret
);

//Translations

global_lang=null;//default en_us
global_langs = {};
for(global_lang_ids,
    global_langs:_ = read_file(_, 'text');
    if(global_langs:_ == null,
        write_file(_, 'text', global_langs:_ = [
            'language_code =    en_us',
            'language =         english',

            'filled =           gi Filled %d blocks',                                    // blocks number
            'no_undo_history =  w No undo history to show for player %s',                // player
            'many_undo =        w Undo history for player %s is very long, showing only the last ten items', // player
            'entry_undo =       w %d: type: %s\\n    affected positions: %s',             // index, command type, blocks number
            'no_undo =          r No actions to undo for player %s',                     // player
            'more_moves_undo =  w Your number is too high, undoing all moves for %s',     // player
            'success_undo =     gi Successfully undid %d operations, filling %d blocks', // moves number, blocks number

            'move_selection_no_player_error = To move selection in the direction of the player, you need to have a player',
            'no_selection_error =             Missing selection for operation',
        ])
    );
    global_langs:_ = _parse_config(global_langs:_)
);
_translate(key, replace_list) -> (
    // print(player(),key+' '+replace_list);
    lang_id = global_lang;
    if(lang_id == null || !has(global_langs, lang_id),
        lang_id = global_lang_ids:0);
    str(global_langs:lang_id:key, replace_list)
);
_print(player, key, ... replace) -> print(player, format(_translate(key, replace)));


//Command processing functions

set_block(pos,block,replacement,flags)->(//use this function to set blocks
    success=null;
    existing = block(pos);

    state = if(flags,{},null);
    if(flags~'w' && existing == 'water' && block_state(existing,'level') == '0',put(state,'waterlogged','true'));

    if(block != existing && (!replacement || _block_matches(existing, replacement)),
        postblock=if(flags && flags~'u',without_updates(set(existing,block,state));print('u'),set(existing,block,state)); //TODO remove "flags && " as soon as the null~'u' => 'u' bug is fixed
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

    _print(player,'filled',length(global_affected_blocks));
    global_affected_blocks=[];
    global_history+=command;
);

print_history()->(
    player=player();
    history = global_history;
    if(length(history)==0||history==null,_print(player, 'no_undo_history', player));
    if(length(history)>10,_print(player, 'many_undo', player));
    total=min(length(history),10);//total items to print
    for(range(total),
        command=history:(length(history)-(_+1));//getting last 10 items in reverse order
        _print(player, 'entry_undo', history~command+1,command:'type', length(command:'affected_positions'))
    )
);

//Command functions

undo(moves)->(
    player = player();
    history= global_history;
    if(length(history)==0||history==null,exit(_print(player, 'no_undo', player)));//incase an op was running command, we want to print error to them
    if(length(history)<moves,_print(player, 'more_moves_undo', player);moves=0);
    if(moves==0,moves=length(history));
    for(range(moves),
        command = history:(length(history)-1);//to get last item of list properly

        for(command:'affected_positions',
            set_block(_:0,_:2,null,null)//todo decide whether to replace all blocks or only blocks that were there before action (currently these are stored, but that may change if we dont want them to)
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
            set_block(_:0,_:2,null,null);
        );

        delete(history,(length(history)-1))
    );
    global_history+={'type'->'redo','affected_positions'->global_affected_blocks};//Doing this the hacky way so I can add custom goodbye message
    print(player,format('gi Successfully redid '+moves+' operations, filling '+length(global_affected_blocks)+' blocks'));
    global_affected_blocks=[];
    _print(player, 'success_undo', moves, affected);
);

fill(block,replacement,flags)->(
    player=player();
    [pos1,pos2]=_get_current_selection();
    volume(pos1,pos2,set_block(pos(_),block,replacement,flags));
    add_to_history('fill', player)
);

rotate(centre, degrees, axis)->(
    player=player();
    [pos1,pos2]=_get_current_selection();

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
        set_block(_,rotation_map:_,null,null)
    );

    add_to_history('rotate', player)
);

clone(new_pos, move,flags)->(
    player=player();
    [pos1,pos2]=_get_current_selection();

    min_pos=map(pos1,min(_,pos2:_i));
    clone_map={};
    translation_vector=new_pos-min_pos;

    volume(pos1,pos2,
        put(clone_map,pos(_)+translation_vector,block(_));//not setting now cos still querying, could mess up and set block we wanted to query
        if(move,set_block(pos(_),if(_ == 'water' && block_state(_,'level') == '0' || block_state(_,'waterlogged')=='true','water','air'),null,null),null)//check for waterlog
    );

    for(clone_map,
        set_block(_,clone_map:_,null,flags)
    );

    add_to_history(if(move,'move','clone'), player)
);

stack(count,direction,flags) -> (
    time = time();
    player=player();
    translation_vector = pos_offset([0,0,0],if(direction,direction,player~'facing'));
    [pos1,pos2]=_get_current_selection();
    flags = _parse(flags);

    translation_vector = translation_vector*map(pos1-pos2,abs(_)+1);

    loop(count,
        c = _;
        offset = translation_vector*(c+1);
        volume(pos1,pos2,
            pos = pos(_)+offset;
            set_block(pos,_,null,flags);
        );
    );

    add_to_history('stack', player);
    print(time()-time);
);


expand(centre, magnitude)->(
    player=player();
    [pos1,pos2]=_get_current_selection();
    expand_map={};
    min_pos=map(pos1,min(_,pos2:_i));
    max_pos=map(pos1,max(_,pos2:_i));


    step=max(1,magnitude)-1;
    volume(pos1,pos2,
        if(block(_)!='air',//cos that way shrinkage retains more blocks and less garbage
            put(expand_map,(pos(_)-centre)*(magnitude-1)+pos(_),block(_))
        )
    );

    for(expand_map,
        set_block(_,expand_map:_,null)
    );
    add_to_history('expand',player)
);



global_flags = 'waehu';

//FLAGS:
//w     waterlog block is previous block was water(logged) too
//a     only replace air
//e     consider entities as well
//h     make shapes hollow
//u     set blocks without updates


_permutation(str) -> (
    if(type(str) == 'string', str = split('',str));
    if(length(str) == 0, return([]));
    ret = {};
    for(str,
        ret += (e = _);
        substr = copy(str);
        delete(substr,_i);
        for(_permutation(substr), ret += e + _);
    );
    keys(ret)
);
global_flags_list = map(_permutation(global_flags), '-'+_);

_parse(flags) ->(
   symbols = split(flags);
   if(symbols:0 != '-', return({}));
   flag_set = {};
   for(split(flags),
       if(_~'[A-Z,a-z]',flag_set+=_);
   );
   flag_set;
);
