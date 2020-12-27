//World edit

global_lang_ids = ['en_us','it_it'];//defining up here for command to work

//# New commands format:
//#   [command_for_carpet, interpretation_for_carpet, false] (will hide it from help menu)
//#   [command_for_carpet, interpretation_for_carpet, [optional_arguments_since, description, description_tooltip, description_action]]
//# optional_arguments_since is the position of the first arg to make optional (<arg> to [arg]). If none, use -1
//# 
//# Suggestion is derived from command_for_carpet, everything before the first `<`.
//# Command prefix (/world-edit ) is automatically added to description_action
//# description_action accepts both execute and suggest actions, by prefixing it with either `!` or `?` (needed)
//# Both description and description_tooltip must be a language string, or a lambda if string needs args.
//# Try to fit each entry in a single line (help menu) for proper pagination (until something is done).
base_commands_map = [
    ['', _()->_help(1), false],
    ['help', _()->_help(1), false],
    ['help <page>', '_help', [0, 'help_cmd_help', null, null]],
    ['lang', _()->_print(player(),'current_lang',global_lang), false],
    ['lang <lang>', _(lang)->(global_lang=lang), [0, 'help_cmd_lang', _()->_translate('help_cmd_lang_tooltip',global_lang_ids), null]],
    ['set <block>', ['set_in_selection',null,null], false],
    ['set <block> <replacement>', ['set_in_selection',null], [1, 'help_cmd_set', 'help_cmd_set_tooltip', null]],
    ['set <block> f <flag>', _(block,flag)->set_in_selection(block,null,flag), false], //TO-DO Help for flags
    ['set <block> <replacement> f <flag>', 'set_in_selection', false],
    ['undo', ['undo', 1], false],
    ['undo <moves>', 'undo', [0, 'help_cmd_undo', null, null]],
    ['undo all', ['undo', 0], [-1, 'help_cmd_undo_all', null, null]],
    ['undo history', 'print_history', [-1, 'help_cmd_undo_history', null, null]],
    ['redo', ['redo', 1], false],
    ['redo <moves>', 'redo', [0, 'help_cmd_redo', 'help_cmd_redo_tooltip', null]],
    ['redo all', ['redo', 0], [-1, 'help_cmd_redo_all', null, null]],
    ['wand', ['_set_or_give_wand',null], [-1, 'help_cmd_wand', null, null]],
    ['wand <wand>', '_set_or_give_wand', [-1, 'help_cmd_wand_2', null, null]],
    ['rotate <pos> <degrees> <axis>', 'rotate', [-1, 'help_cmd_rotate', 'help_cmd_rotate_tooltip', null]],//will replace old stuff if need be
    ['stack', ['stack',1,null,null], false],
    ['stack <count>', ['stack',null,null], false],
    ['stack <count> <direction>', ['stack',null], [0, 'help_cmd_stack', 'help_cmd_stack_tooltip', null]],
    ['stack f <flag>', _(flags)->stack(1,null,flags), false], //TODO here too Help for flags
    ['stack <count> f <flag>', _(stackcount,flags)->stack(1,null,flags), false],
    ['stack <count> <direction> f <flag>', 'stack', false],
    ['expand <pos> <magnitude>', 'expand', [-1, 'help_cmd_expand', 'help_cmd_expand_tooltip', null]],
    ['move <pos>', ['move',null], [-1, 'help_cmd_move', null, null]],
    ['move <pos> f <flags>', 'move', false], //TODO flags help
    ['copy clear_clipboard',_()->(global_clipboard=[];_print(player(),'clear_clipboard',player())),[-1,'help_cmd_clear_clipboard',null,null]],
    ['copy',['_copy',null, false],[-1,'help_cmd_copy',null,null]],
    ['copy force',['_copy',null, true],false],
    ['copy <pos>',['_copy', false],false],
    ['copy <pos> force',['_copy', true],false],
    ['paste',['paste', null, null],[-1,'help_cmd_paste',null,null]],
    ['paste f <flags>',_(flags)->paste(null, flags),false],//todo flags help
    ['paste <pos>',['paste', null],false],
    ['paste <pos> f <flags>','paste',false],//todo last flags help
    ['selection clear', 'clear_selection', false], //TODO help for this and below
    ['selection expand', _()->selection_expand(1), false],
    ['selection expand <amount>', 'selection_expand', false],
    ['selection move', _() -> selection_move(1, null), false],
    ['selection move <amount>', _(n)->selection_move(n, null), false],
    ['selection move <amount> <direction>', 'selection_move',false],
    ['flood <block>', ['flood_fill', null, null], false],
    ['flood <block> <axis>', ['flood_fill', null], [1, 'help_cmd_flood', 'help_cmd_flood_tooltip', null]],
    ['flood <block> f <flags>', _(block,flags)->flood_fill(block,null,flags), false],
    ['flood <block> <axis> f <flags>', 'flood_fill', false],
    ['brush clear', ['brush', 'clear', null], [-1, 'help_cmd_brush_clear', null, null]],
    ['brush list', ['brush', 'list', null], [-1, 'help_cmd_brush_list', null, null]],
    ['brush info', ['brush', 'info', null], [-1, 'help_cmd_brush_info', null, null]],
    ['brush cube <block> <size_int>', _(block, size_int) -> brush('cube', null, block, size_int, null), false],
    ['brush cube <block> <size_int> f <flags>', _(block, size_int, flags) -> brush('cube', flags, block, size_int, null), false],
    ['brush cube <block> <size_int> <replacement>', _(block, size_int, replacement) -> brush('cube', null, block, size_int, replacement), [2, 'help_cmd_brush_cube', 'help_cmd_brush_generic', null]],
    ['brush cube <block> <size_int> <replacement> f <flags>', _(block, size_int, replacement, flags) -> brush('cube', flags, block, size_int, replacement), false],
    ['brush cuboid <block> <x_int> <y_int> <z_int>', _(block, x_int, y_int, z_int) -> brush('cuboid', null, block, [x_int, y_int, z_int], null), false],
    ['brush cuboid <block> <x_int> <y_int> <z_int> f <flags>', _(block, x_int, y_int, z_int, flags) -> brush('cuboid', flags, block, [x_int, y_int, z_int], null), false],
    ['brush cuboid <block> <x_int> <y_int> <z_int> <replacement>', _(block, x_int, y_int, z_int, replacement) -> brush('cuboid', null, block, [x_int, y_int, z_int], replacement), [4, 'help_cmd_brush_cuboid', 'help_cmd_brush_generic', null]],
    ['brush cuboid <block> <x_int> <y_int> <z_int> <replacement> f <flags>', _(block, x_int, y_int, z_int, replacement, flags) -> brush('cuboid', flags, block, [x_int, y_int, z_int], replacement), false],
    ['brush sphere <block> <radius_int>', _(block, radius_int) -> brush('sphere', null, block, radius_int, null), false],
    ['brush sphere <block> <radius_int> f <flags>', _(block, radius_int, flags) -> brush('sphere', flags, block, radius_int, null), false],
    ['brush sphere <block> <radius_int> <replacement>', _(block, radius_int, replacement) -> brush('sphere', null, block, radius_int, replacement), [2, 'help_cmd_brush_sphere', 'help_cmd_brush_generic', null]],
    ['brush sphere <block> <radius_int> <replacement> f <flags>', _(block, radius_int, replacement, flags) -> brush('sphere', flags, block, radius_int, replacement), false],
    ['brush flood <block> <radius_int>', _(block, radius_int) -> brush('flood', null, block, radius_int, null), false],
    ['brush flood <block> <radius_int> f <flags>', _(block, radius_int, flags) -> brush('flood', flags, block, radius_int, null), false], 
    ['brush flood <block> <radius_int> <axis>', _(block, radius_int, axis) -> brush('flood', null, block, radius_int, axis), [2, 'help_cmd_brush_flood', 'help_cmd_brush_generic', null]],
    ['brush flood <block> <radius_int> <axis> f <flags>', _(block, radius_int, axis, flags) -> brush('flood', flags, block, radius_int, axis), false], 
    // we need a better way of changing 'settings'
    ['settings quick_select <bool>', _(b) -> global_quick_select = b, false]
];

// Proccess commands map for Carpet
global_commands_map = {};
for(base_commands_map,
    global_commands_map:(_:0) = _:1;
);
// Proccess commands map for help
global_help_commands = [];
for(base_commands_map,
    if(_:2, //Check it's not skipped (aka false)
        visible_command = _:0;
        suggestion = reduce(split(visible_command),if((_ == '<'),break(),_a+_),'');
        if((search_pos = _:2:0) != -1, // Proccess arguments
            current_pos = [-1, -1];
            visible_command = reduce(map(split(visible_command),
                if(_ == '<', current_pos:0 += 1; if(current_pos:0 >= search_pos, '[', _)
                  ,_ == '>', current_pos:1 += 1; if(current_pos:1 >= search_pos, ']', _) ,_ ) ),
                  _a+_,'');
        );
        global_help_commands += ['g - '+visible_command, suggestion, _:2:1, _:2:2, _:2:3];
    )
);


__config()->{
    'commands'-> global_commands_map,
    'arguments'->{
        'replacement'->{'type'->'blockpredicate'},
        'moves'->{'type'->'int','min'->1,'suggest'->[]},//todo decide on whether or not to add max undo limit
        'degrees'->{'type'->'int','suggest'->[]},
        'axis'->{'type'->'term','options'->['x','y','z']},
        'wand'->{'type'->'item','suggest'->['wooden_sword','wooden_axe']},
        'direction'->{'type'->'term','options'->['north','south','east','west','up','down']},
        'stack'->{'type'->'int','min'->1,'suggest'->[]},
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
        'lang'->{'type'->'term','options'->global_lang_ids},
        'page'->{'type'->'int','min'->1,'suggest'->[1,2,3]},
    }
};
//player globals

global_wand = 'wooden_sword';
global_history = [];
global_undo_history = [];
global_quick_select = true;
global_clipboard = [];

global_debug_rendering = false;
global_reach = 4.5;


//Extra boilerplate

global_affected_blocks=[];

//Block-selection

global_cursor = null;
global_highlighted_marker = null;
global_selection = {}; // holds ids of two official corners of the selection
global_markers = {};

_help(page) ->
(
    command = '/'+system_info('app_name')+' ';
    // Header
    print(format(_translate('help_header_prefix'), _translate('help_header_title'), _translate('help_header_suffix')));
    
    // Help entries are generated on top, in the commands map. Check docs there
    // Hardcoded help entries (info)
    // Syntax: [pre-arrow, pre-arrow command to suggest, post-arrow text, post-arrow tooltip (lang string/lambda), post-arrow action]
    help_entries = [
        [null, null, 'help_welcome', 'help_welcome_tooltip', null],
        [if(length(global_selection) > 1,_translate('help_your_selection')), '', if(length(global_selection) > 1,
                            _()->_translate('help_selection_bounds', global_selection:0, global_selection:1)
                            ,'help_make_selection'), null, '?wand '],
        [_translate('help_selected_wand'), 'wand ', _()->_translate('help_selected_wand_item',title(replace(global_wand, '_', ' '))), 'help_sel_wand_tooltip', '?wand '],
        [_translate('help_app_lang'), 'lang ', _()->_translate('help_app_lang_selected', global_lang), 'help_app_lang_tooltip', '?lang '],
        [_translate('help_list_title'), 'help', null, null]
    ];
    
    for(global_help_commands,
        help_entries += _;
    );
    remaining_to_display = 8;
    current_entry = ((page-1)*8);
    entry_number = length(help_entries);
    while(remaining_to_display != 0 && current_entry < entry_number, entry_number,
        entry = help_entries:current_entry;
        //Allow different desc actions
        if(slice(entry:4,0,1) == '!',
            description_action = '!'+command+slice(entry:4,1);
        , slice(entry:4,0,1) == '?',
            description_action = '?'+command+slice(entry:4,1);
        );
        if(entry:2 != null, arrow = 'd  -> ');
        //print(entry:2);
        print(format(entry:0, '?'+command+entry:1, arrow, 
            if(type(entry:2) == 'function', call(entry:2), _translate(entry:2)), 
            '^'+if(type(entry:3) == 'function', call(entry:3), _translate(entry:3)),description_action)
        );

        current_entry += 1;
        remaining_to_display += -1;
        description_action = arrow = null;
    );
    
    // Footer
    footer = format('');
    if (page < 10, // In case we reach this, make it still be centered
        footer += ' ';
    );
    footer += format(_translate('help_pagination_prefix'));
    if (page != 1,
        footer += format('d [<<]',
                    '^'+_translate('help_pagination_first'),
                    '!'+command+'help');
        footer += ' ';
        footer += format('d [<]',
                    '^'+_translate('help_pagination_prev',page-1),
                    '!'+command+'help '+(page-1));
    ,
        footer += format('g [<<]');
        footer += ' ';
        footer += format('g [<]');
    );
    footer += ' ';
    footer += format(_translate('help_pagination_page',page));
    if ((8*page) < entry_number,
        last_page = ceil((entry_number)/8);
        footer += format('d [>]',
                        '^'+_translate('help_pagination_next',page+1),
                        '!'+command+'help '+(page+1));
        footer += ' ';
        footer += format('d [>>]',
                        '^'+_translate('help_pagination_last',last_page),
                        '!'+command+'help '+last_page);
    ,
        footer += format('g [>]');
        footer += ' ';
        footer += format('g [>>]');
    );
    footer += format(_translate('help_pagination_suffix'));
    print(footer);
);

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
    [from, to] = _get_current_selection(player());
    point1 = _get_marker_position(global_selection:'from');
    point2 = _get_marker_position(global_selection:'to');
    p = player();
    if (p == null && direction == null, _error(player, 'move_selection_no_player_error'));
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
    [from, to] = _get_current_selection(player());
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

__on_tick() ->
(
    if (p = player(),
        // put your catchall checks here
        global_highlighted_marker = null;
        reach = if( (held = p~'holds':0)==global_wand, global_reach, has(global_brushes, held), global_brush_reach);
        new_cursor = if ( reach && p~'gamemode'!='spectator',
            if (marker = _trace_marker(p, global_reach), 
                global_highlighted_marker = marker;
                _get_marker_position(marker)
            , 
                _get_player_look_at_block(p, reach) )
        );
        if (global_cursor && new_cursor != global_cursor,
            draw_shape('box', 0, 'from', global_cursor, 'to', global_cursor+1, 'fill', 0xffffff22);
        );
        if (new_cursor,
             draw_shape('box', 50, 'from', new_cursor, 'to', new_cursor+1, 'fill', 0xffffff22);
        );
        global_cursor = new_cursor;
    )
);


__on_player_swings_hand(player, hand) ->
//__on_player_clicks_block(player, block, face) -> 
(
    if(player~'holds':0==global_wand,
        if (global_quick_select,
            _set_selection_point('from')
        , // else
            if (length(global_selection)<2,
                _set_selection_point(if(!global_selection, 'from', null ));
            )
        )
    )
);

__on_player_uses_item(player, item_tuple, hand) ->
(
    if( (held = player~'holds':0)==global_wand,
        if (global_quick_select,
            _set_selection_point('to')
        , // else
            if (length(global_selection)<2,
                //cancel selection
                clear_selection();
            ,
                //grab marker
                marker = global_highlighted_marker;
                if (marker,
                    selection_marker = first(global_selection, global_selection:_ == marker);
                    if (selection_marker,
                        // should be one really
                        clear_markers(global_selection:selection_marker);
                        delete(global_selection:selection_marker);
                    )
                )
            )
        ),
        has(global_brushes, held), // TODO: make it so that if brush is a block, placing is not processed
        pos = _get_player_look_at_block(player, global_brush_reach);
        _brush_action(pos, held)
    )
);

_trace_marker(player, distance) ->
(
    precision = 0.5;
    initial_position = pos(player)+[0,player~'eye_height',0];
    look_vec = player ~ 'look';
    marker_id = null;
    while(!marker_id, distance/precision,
        rnd_pos = map(initial_position, floor(_));
        markers = filter(values(global_markers), _:'pos' == rnd_pos);
        if (markers,
            marker_id = markers:0:'id',
        ,
            initial_position = initial_position + look_vec * precision;
            if (global_debug_rendering, particle('end_rod', initial_position, 1, 0, 0));
        );
    );
    marker_id
);

_set_selection_point(which) ->
(
    if (global_highlighted_marker,
        clear_markers(global_highlighted_marker);
    );
    which = which || if(has(global_selection:'from'), 'to', 'from');
    if (global_selection:which,
        clear_markers(global_selection:which)
    );
    marker = _create_marker(global_cursor, if(which =='from', 'lime_concrete', 'blue_concrete'));
    global_highlighted_marker = marker;

    global_selection:which = marker;
    if (!global_rendering_selection, _render_selection_tick());
);

global_rendering_selection = false;
_render_selection_tick() ->
(
    if (!global_selection,
        global_rendering_selection = false;
        return()
    );
    global_rendering_selection = true;
    active = (length(global_selection) == 1);

    start_marker = global_selection:'from';
    start = if(
        start_marker,  _get_marker_position(start_marker),
        global_cursor
    );

    end_marker = global_selection:'to';
    end = if(
        end_marker, _get_marker_position(end_marker),
        global_cursor
    );

    if (start && end,
        zipped = map(start, [_, end:_i]);
        from = map(zipped, min(_));
        to = map(zipped, max(_));
        draw_shape('box', if(active, 1, 12), 'from', from, 'to', to+1, 'line', 3, 'color', if(active, 0x00ffffff, 0xAAAAAAff));
        if (!end_marker,   draw_shape('box', 1, 'from', end, 'to', end+1, 'line', 1, 'color', 0x0000FFFF, 'fill', 0x0000FF55 ));
        if (!start_marker, draw_shape('box', 1, 'from', start, 'to', start+1, 'line', 1, 'color', 0x3fff00FF, 'fill', 0x3fff0055 ));
    );
    schedule(if(active, 1, 10), '_render_selection_tick');
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

_get_current_selection(player)->
(
    if( length(global_selection) < 2,
        _error(player, 'no_selection_error',player)
    );
    start = _get_marker_position(global_selection:'from');
    end = _get_marker_position(global_selection:'to');
    zipped = map(start, [_, end:_i]);
    [map(zipped, min(_)), map(zipped, max(_))]
);

//Misc functions

_set_or_give_wand(wand) -> (
    p = player();
    if(wand,//checking if player specified a wand to be added
        if((['tools', 'combat']~item_category(wand:0)) != null,
            global_wand = wand;
            _print(p, 'new_wand', wand:0);
            return(),
            //else, can't set as wand
            _error(p, 'invalid_wand')
        )
    );//else, if just ran '/world-edit wand' with no extra args
    //give player wand if hand is empty
    if(held_item_tuple = p~'holds' == null,
       slot = inventory_set(p, p~'selected_slot', 1, global_wand);
       return()
    );
    //else, set current held item as wand, if valid
    held_item = held_item_tuple:0;
    if( (['tools', 'combat']~item_category(held_item)) != null,
        global_wand = held_item;
        _print(p, 'new_wand', held_item),
       //else, can't set as wand
       _error(p, 'invalid_wand')
    )
);

global_flags = 'waehubp';

//FLAGS:
//w     waterlog block if previous block was water(logged) too
//a     don't paste air
//e     consider entities as well
//h     make shapes hollow
//u     set blocks without updates
//b     set biome
//p     only replace air


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

_parse_flags(flags) ->(
   symbols = split(flags);
   if(symbols:0 != '-', return({}));
   flag_set = {};
   for(split(flags),
       if(_~'[A-Z,a-z]',flag_set+=_);
   );
   flag_set;
);

//Config Parser

_parse_config(config) -> (
    if(type(config) != 'list', config = [config]);
    ret = {};
    for(config,
        if(_ ~ '^\\w+ ?= *.+$' != null,
            key = _ ~ '^\\w+(?= ?= *.+)';
            value = _ ~ str('(?<=%s ?= ?) *([^ ].*)',key);
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
            
            'help_header_prefix =      c ----------------- [ ',
            'help_header_title =       d World-Edit Help',
            'help_header_suffix =      c  ] -----------------',
            'help_welcome =            c Welcome to the World-Edit Scarpet app\'s help!',
            'help_welcome_tooltip =    y Hooray!',
            'help_your_selection =     c Your selection',
            'help_selection_bounds =   l %s to %s',
            'help_make_selection =     c Use your wand to select with a start and final position',
            'help_selected_wand =      c Selected wand', //Could probably be used in more places
            'help_selected_wand_item = l %s',
            'help_sel_wand_tooltip =   g Use the wand command to change',
            'help_app_lang =           c App Language ', //Could probably be used in more places
            'help_app_lang_selected =  l %s',
            'help_app_lang_tooltip =   g Use the lang command to change it',
            'help_list_title =         y Command list (without prefix):',
            'help_pagination_prefix =  c --------------- ',
            'help_pagination_page =    y Page %d ',
            'help_pagination_suffix =  c  ---------------',
            'help_pagination_first =   g Go to first page',
            'help_pagination_prev =    g Go to previous page (%d)',
            'help_pagination_next =    g Go to next page (%d)',
            'help_pagination_last =    g Go to last page (%d)',
            'help_cmd_help =           l Shows this help menu, or a specified page',
            'help_cmd_lang =           l Changes current app\'s language to [lang]',
            'help_cmd_lang_tooltip =   g Available languages are %s',
            'help_cmd_set =            l Set selection to block, filterable',
            'help_cmd_set_tooltip =    g You can use a tag in the replacement argument',
            'help_cmd_undo =           l Undoes last n moves, one by default',
            'help_cmd_undo_all =       l Undoes the entire action history',
            'help_cmd_undo_history =   l Shows the history of undone actions',
            'help_cmd_redo =           l Redoes last n undoes, one by default',
            'help_cmd_redo_tooltip =   g Also shows up in undo history',
            'help_cmd_redo_all =       l Redoes the entire undo history',
            'help_cmd_wand =           l Sets held item as wand or gives it if hand is empty',
            'help_cmd_wand_2 =         l Changes the current wand item',
            'help_cmd_rotate =         l Rotates [deg] about [pos]',
            'help_cmd_rotate_tooltip = g Axis must be x, y or z',
            'help_cmd_stack =          l Stacks selection n times in dir',
            'help_cmd_stack_tooltip =  g If not provided, direction is player\'s view direction by default',
            'help_cmd_expand =         l Expands sel [magn] from pos', //This is not understandable
            'help_cmd_expand_tooltip = g Expands the selection [magnitude] from [pos]',
            'help_cmd_move =           l Moves selection to <pos>',
            'help_cmd_flood =          l Flood fill with [block] starting at player\'s position',
            'help_cmd_set_flood =      g Flood will happen in plane perpendicular to [axis], if given',
            'help_cmd_clear_clipboard = l Clears player clipboard',
            'help_cmd_copy =           l Copies selection to player clipboard',
            'help_cmd_paste =          l Pastes from player clipboard',
            'help_cmd_brush_clear =    l Unregisters current item as brush',
            'help_cmd_brush_list =     l Lists all currently regiestered brushes and their actions',
            'help_cmd_brush_info =     l Gives detailed info of currently held brush',
            'help_cmd_brush_generic =  l Hold item to turn into brush',
            'help_cmd_brush_cube =     l Register brush to create cube of side length [size] out of [block]',
            'help_cmd_brush_cuboid =   l Register brush to create cuboid of dimensions [x] [y] and [z] out of [block]',
            'help_cmd_brush_sphere =   l Register brush to create sphere of radius [size] out of [block]',
            'help_cmd_brush_flood =    l Register brush to perfrm flood fill out of [block] starting on right clicked block',


            'filled =           gi Filled %d blocks',                                    // blocks number
            'no_undo_history =  w No undo history to show for player %s',                // player
            'many_undo =        w Undo history for player %s is very long, showing only the last ten items', // player
            'entry_undo_1 =     w %d: type: %s',                                         //index, command type
            'entry_undo_2       w     affected positions: %s',                           //blocks number
            'no_undo =          r No actions to undo for player %s',                     // player
            'more_moves_undo =  w Your number is too high, undoing all moves for %s',    // player
            'success_undo =     gi Successfully undid %d operations, filling %d blocks', // moves number, blocks number
            'no_redo =          r No actions to redo for player %s',                     // player
            'more_moves_redo =  w Your number is too high, redoing all moves for %s',    // player
            'success_redo =     gi Successfully redid %d operations, filling %d blocks', // moves number, blocks number

            'clear_clipboard =                wi Cleared player %s\'s clipboard',
            'copy_clipboard_not_empty =       ri Clipboard for player %s is not empty, use "/copy force" to overwrite existing clipboard data',//player
            'copy_force =                     ri Overwriting previous clipboard selection with new one',
            'copy_success =                   gi Successfully copied %s blocks and %s entities to clipboard',//blocks number, entity number
            'paste_no_clipboard =             ri Cannot paste, clipboard for player %s is empty',//player

            'current_lang =     gi Current language is: %s',                              //lang id. todo decide whether to hardcode this

            'move_selection_no_player_error = r To move selection in the direction of the player, you need to have a player',
            'no_selection_error =             r Missing selection for operation for player %s', //player
            'new_wand =                       wi %s is now the app\'s wand, use it with care.', //wand item
            'invalid_wand =                   r Wand has to be a tool or weapon',

            'bad_wand_brush_error =           r Your wand can\'t be a brush',
            'no_brush_error =                 r %s is not a brush',
            'new_brush =                      wi %s is now a brush with action %s',
            'brush_info =                     w %s has action %s bound to it with parameters %s and flags %s',
            'brush_replaced =                 w Replacing previous action for brush in %s',
            'brush_list_header =              bc === Current brushes are ===',
            'brush_empty_list =               gi No brushes registerd so far',
            'brush_extra_info =               ig For detailed info on a brush use /world-edit brush info,'


        ])
    );
    global_langs:_ = _parse_config(global_langs:_)
);

_translate(key, ... replace_list) -> (
    // print(player(),key+' '+replace_list);
    lang_id = global_lang;
    if(lang_id == null || !has(global_langs, lang_id),
        lang_id = global_lang_ids:0);
    if(key == null,
        null,
        str(global_langs:lang_id:key, replace_list)
    )
);

_print(player, key, ... replace) ->
    print(player, format(_translate(key, replace)));

_error(player, key, ... replace)->
    exit(print(player, format(_translate(key, replace))));


//Command processing functions

set_block(pos,block, replacement, flags, extra)->(//use this function to set blocks
    success=null;
    existing = block(pos);

    state = if(flags,{},null);
    if(flags~'w' && existing == 'water' && block_state(existing,'level') == '0',put(state,'waterlogged','true'));

    if(block != existing && (!replacement || _block_matches(existing, replacement)) && (!flags~'p' || air(pos)),
        postblock=if(flags && flags~'u',without_updates(set(existing,block,state)),set(existing,block,state)); //TODO remove "flags && " as soon as the null~'u' => 'u' bug is fixed
        prev_biome=biome(pos);
        if(flag~'b'&&extra:'biome',set_biome(pos,extra:'biome'));
        success=existing;
        global_affected_blocks+=[pos,existing,{'biome'->prev_biome}];
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

    if(length(global_affected_blocks)==0,exit(_print(player, 'filled',0)));//not gonna add empty list to undo ofc...
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
    if(length(history)==0||history==null,_error(player, 'no_undo_history', player));
    if(length(history)>10,_print(player, 'many_undo', player));
    total=min(length(history),10);//total items to print
    for(range(total),
        command=history:(length(history)-(_+1));//getting last 10 items in reverse order
        _print(player, 'entry_undo_1', (history~command)+1, command:'type');//printing twice so it goes on 2 separate lines
        _print(player, 'entry_undo_2', length(command:'affected_positions'))
    )
);

//Command functions

undo(moves)->(
    player = player();
    if(length(global_history)==0||global_history==null,_error(player, 'no_undo', player));//incase an op was running command, we want to print error to them
    if(length(global_history)<moves,_print(player, 'more_moves_undo', player);moves=0);
    if(moves==0,moves=length(global_history));
    for(range(moves),
        command = global_history:(length(global_history)-1);//to get last item of list properly

        for(command:'affected_positions',
            set_block(_:0,_:1,null,'b',_:2);//we dont know whether or not a new biome was set, so we have to store it here jic. If it wasnt, then nothing happens, cos the biome is the same
        );

        delete(global_history,(length(global_history)-1))
    );
    global_undo_history+=global_affected_blocks;//we already know that its not gonna be empty before this, so no need to check now.
    _print(player, 'success_undo', moves, length(global_affected_blocks));
    global_affected_blocks=[];
);

redo(moves)->(
    player=player();
    if(length(global_undo_history)==0||global_undo_history==null,_error(player,'no_redo',player));
    if(length(global_undo_history)<moves,_print(player, 'more_moves_redo', player);moves=0);
    if(moves==0,moves=length(global_undo_history));
    for(range(moves),
        command = global_undo_history:(length(global_undo_history)-1);//to get last item of list properly

        for(command,
            set_block(_:0,_:1,null,'b',_:2);
        );

        delete(global_undo_history,(length(global_undo_history)-1))
    );
    global_history+={'type'->'redo','affected_positions'->global_affected_blocks};//Doing this the hacky way so I can add custom goodbye message
    _print(player, 'success_redo', moves, length(global_affected_blocks));
    global_affected_blocks=[];
);

set_in_selection(block,replacement,flags)->
(
    player=player();
    [pos1,pos2]=_get_current_selection(player);
    volume(pos1,pos2,set_block(pos(_),block,replacement,flags,{}));
    add_to_history('fill', player)
);


flood_fill(block, axis, flags) ->
(
    player = player();
    start = player~'pos'; 
    if(block(start)==block, return());

    [pos1,pos2]=_get_current_selection(player);
    min_pos = map(pos1, min(_, pos2:_i));
    max_pos = map(pos1, max(_, pos2:_i));
    // test if inside selection
    _flood_tester(pos, outer(min_pos), outer(max_pos)) -> (
        all(pos, _ >= min_pos:_i) && all(pos, _ <= max_pos:_i)
    );
    _flood_generic(block, axis, start, flags);

);

_flood_generic(block, axis, start, flags) -> 
(

    // Define function to request neighbours perpendiular to axis
    if(
        axis==null, flood_neighbours(block) -> map(neighbours(block), pos(_)),
        axis=='x', flood_neighbours(block) -> [pos_offset(block, 'north'), pos_offset(block, 'south'), pos_offset(block, 'up'), pos_offset(block, 'down')],
        axis=='y', flood_neighbours(block) -> [pos_offset(block, 'north'), pos_offset(block, 'south'), pos_offset(block, 'east'), pos_offset(block, 'west')],
        axis=='z', flood_neighbours(block) -> [pos_offset(block, 'east'), pos_offset(block, 'west'), pos_offset(block, 'up'), pos_offset(block, 'down')]
    );

    interior_block = block(start);
    if(_flood_tester(start), set_block(start, block, null, flags, {}), return());
    
    visited = {start->null};
    queue = [start];
    
    while(length(queue)>0, 10000,

        current_pos = queue:0;
        delete(queue, 0);
        
        for(flood_neighbours(current_pos),
            current_neighbour = _;
            // check neighbours, add the non visited ones to the visited set
            if(!has(visited, current_neighbour),
                visited:current_neighbour = null;
                // if the block is not too far and is interior, delete it and add to queue to check neighbours later
                if( block(_)==interior_block && _flood_tester(_),
                    queue:length(queue) = current_neighbour;
                    set_block(current_neighbour, block, null, flags, {})
                );
            );
        );
    );

    add_to_history('flood', player())
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
        set_block(_,rotation_map:_,null,null,{})
    );

    add_to_history('rotate', player)
);

move(new_pos,flags)->(
    player=player();
    [pos1,pos2]=_get_current_selection(player);

    min_pos=map(pos1,min(_,pos2:_i));
    avg_pos=(pos1+pos2)/2;
    move_map={};
    translation_vector=new_pos-min_pos;
    flags=_parse_flags(flags);
    entities=if(flags~'e',entity_area('*',avg_pos,map(avg_pos-min_pos,abs(_))),[]);//checking here cos checking for entities is expensive, sp dont wanna do it unnecessarily

    volume(pos1,pos2,
        put(move_map,pos(_)+translation_vector,[block(_), biome(_)]);//not setting now cos still querying, could mess up and set block we wanted to query
        set_block(pos(_),if(flags~'w'&&block_state(_,'waterlogged')=='true','water','air'),null,null,{})//check for waterlog
    );

    for(move_map,
        set_block(_,move_map:_:0,null,flags,{'biome'->move_map:_:1});
    );

    for(entities,//if its empty, this just wont run, no errors
        nbt=parse_nbt(_~'nbt');
        old_pos=pos(_);
        pos=old_pos-min_pos+new_pos;
        delete(nbt,'Pos');//so that when creating new entity, it doesnt think it is in old location
        spawn(_~'type',pos,encode_nbt(nbt));
        if(move,modify(_,'remove'))
    );

    add_to_history('move', player)
);

stack(count,direction,flags) -> (
    player=player();
    translation_vector = pos_offset([0,0,0],if(direction,direction,player~'facing'));
    [pos1,pos2]=_get_current_selection(player);
    flags = _parse_flags(flags);

    translation_vector = translation_vector*map(pos1-pos2,abs(_)+1);

    loop(count,
        c = _;
        offset = translation_vector*(c+1);
        volume(pos1,pos2,
            pos = pos(_)+offset;
            set_block(pos,_,null,flags,{});
        );
    );

    add_to_history('stack', player);
);


expand(centre, magnitude)->(
    player=player();
    [pos1,pos2]=_get_current_selection(player);
    expand_map={};

    volume(pos1,pos2,
        if(air(_),//cos that way shrinkage retains more blocks and less garbage
            put(expand_map,(pos(_)-centre)*(magnitude-1)+pos(_),block(_))
        )
    );

    for(expand_map,
        set_block(_,expand_map:_,null,{})
    );
    add_to_history('expand',player)
);

_copy(centre, force)->(
    player = player();
    if(!centre,centre=pos(player));
    [pos1,pos2]=_get_current_selection(player);
    if(global_clipboard,
        if(force,
            _print(player,'copy_force');
            global_clipboard=[],
            _error(player,'copy_clipboard_not_empty')
        )
    );

    min_pos=map(pos1,min(_,pos2:_i));
    avg_pos=(pos1+pos2)/2;
    global_clipboard+=if(flags~'e',entity_area('*',avg_pos,map(avg_pos-min_pos,abs(_))),[]);//always gonna have

    volume(pos1,pos2,
        global_clipboard+=[centre-pos(_),block(_),biome(_)]//all the important stuff, can add flags later if we want
    );

    _print(player,'copy_success',length(global_clipboard)-1,length(global_clipboard:0))
);

paste(pos, flags)->(
    player=player();
    if(!pos,pos=pos(player));
    [pos1,pos2]=_get_current_selection(player);
    if(!global_clipboard,_error(player, 'paste_no_clipboard', player));
    flags=_parse_flags(flags);

    entities=global_clipboard:0;
    for(range(1,length(global_clipboard)-1),//cos gotta skip the entity one
        [pos_vector, old_block, old_biome]=global_clipboard:_;
        new_pos=pos+pos_vector;
        if(!(flags~'a'&&air(old_block)),
            set_block(new_pos, old_block, null, flags, {'biome'->old_biome})
        )
    );
    add_to_history('paste',player)
);


// Brush

global_brushes = {};
global_brush_reach = 100;

brush(action, flags, ...args) -> (
    player = player();
    held_item = player~'holds':0;
    if(held_item==global_wand, _error(player, 'bad_wand_brush_error'); return());
    
    if(
        action=='clear',
        if(has(global_brushes, held_item), 
            delete(global_brushes, held_item),
            _error(player, 'no_brush_error', held_item)
        ),
        action=='list', //TODO imprvove list with interactiveness
        if(global_brushes, 
            _print(player, 'brush_list_header');
            for(pairs(global_brushes), 
                print(player, str('%s: %s', _:0, _:1:0));
            );
            _print(player, 'brush_extra_info'),
            _print(player, 'brush_empty_list')
        ),
        action=='info', //TODO improve info with better descriptions
        if(has(global_brushes, held_item),
            _print(player, 'brush_info', held_item, params=global_brushes:held_item:0, params:1, params:2),
            _error(player, 'no_brush_error', held_item)
        ),
        // else, register new brush with given action
        if(has(global_brushes, held_item),
            _print(player, 'brush_replaced', held_item)
        );
        global_brushes:held_item = [action, args, flags];
    )
);

_brush_action(pos, brush) -> (
    [action, args, flags] = global_brushes:brush;
    call(action, pos, args, flags)
);

//TODO: missing support for hollow shapes
cube(pos, args, flags) -> (
    [block, size, replacement] = args;

    if(size == 1, 
        set_block(pos, block, replacement, flags, {}),

        half_size = (size-1)/2;
        volume(pos-half_size, pos+half_size,
            set_block(_, block, replacement, flags, {})
        );  
    );
    
    add_to_history('brush_cube',player())
);

//TODO: missing support for hollow shapes
cuboid(pos, args, flags) -> (
    [block, size, replacement] = args;

    half_size = (size-1)/2;
    volume(pos-half_size, pos+half_size,
        set_block(_, block, replacement, flags, {})
    );
    
    add_to_history('brush_cube',player())
);

_sq_distance(p1, p2) -> reduce(p1-p2, _a + _*_, 0);

//TODO: missing support for hollow shapes
sphere(pos, args, flags) -> (
    [block, radius, replacement] = args;

    if(radius == 1, 
        set_block(pos, block, replacement, flags, {}),

        volume(pos-radius, pos+radius,
            if(_sq_distance(pos, pos(_)) <= radius*radius,
                set_block(_, block, replacement, flags, {})
            );
        );  
    );
    
    add_to_history('brush_cube',player())
);

flood(pos, args, flag) -> (

    start = pos;
    [block, radius, axis] = args;
    if(block(start)==block, return());

    // test if inside sphere
    _flood_tester(pos, outer(start), outer(radius)) -> (
        _sq_distance(pos, start) <= radius*radius
    );

    _flood_generic(block, axis, start, flags);
);
