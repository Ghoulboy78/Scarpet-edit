//World edit for scarpet

import('math','_round');

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
    ['lang', ['_change_lang',null], false],
    ['lang <lang>', '_change_lang', [0, 'help_cmd_lang', _()->_translate('help_cmd_lang_tooltip',_get_lang_list()), null]],
    ['set <block>', ['set_in_selection',null,null], false],
    ['set <block> <replacement>', ['set_in_selection',null], [1, 'help_cmd_set', 'help_cmd_set_tooltip', null]],
    ['set <block> f <flag>', _(block,flag)->set_in_selection(block,null,flag), false], //TO-DO Help for flags
    ['set <block> <replacement> f <flag>', 'set_in_selection', false],
    ['walls <block>', ['walls', 'xz', null, null], false],
    ['walls <block> <sides>', ['walls', null, null], false],
    ['walls <block> <sides> <replacement>', ['walls', null], [1, 'help_cmd_walls', 'help_cmd_walls_tooltip', null]],
    ['walls <block> f <flags>', _(block,flags)->walls(block,'xz',null,flags), false],
    ['walls <block> <sides> f <flags>', _(block,sides,flags)->walls(block,sides,null,flags), false],
    ['walls <block> <sides> <replacement> f <flags>', 'walls', false],
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
    ['stack <count> f <flag>', _(count,flags)->stack(count,null,flags), false],
    ['stack <count> <direction> f <flag>', 'stack', false],
    ['expand <pos> <magnitude>', 'expand', [-1, 'help_cmd_expand', 'help_cmd_expand_tooltip', null]],
    ['move <pos>', ['move',null], [-1, 'help_cmd_move', null, null]],
    ['move <pos> f <flag>', 'move', false], //TODO flags help
    ['copy clear_clipboard',_()->(global_clipboard=[];_print(player(),'clear_clipboard',player())),[-1,'help_cmd_clear_clipboard',null,null]],
    ['copy',['_copy',null, false],[-1,'help_cmd_copy',null,null]],
    ['copy force',['_copy',null, true],false],
    ['copy <pos>',['_copy', false],false],
    ['copy <pos> force',['_copy', true],false],
    ['paste',['paste', null, null],[-1,'help_cmd_paste',null,null]],
    ['paste f <flag>',_(flags)->paste(null, flags),false],//todo flags help
    ['paste <pos>',['paste', null],false],
    ['paste <pos> f <flag>','paste',false],//todo last flags help
    ['selection clear', 'clear_selection', false], //TODO help for this and below
    ['selection expand', _()->selection_expand(1), false],
    ['selection expand <amount>', 'selection_expand', false],
    ['selection move', _() -> selection_move(1, null), false],
    ['selection move <amount>', _(n)->selection_move(n, null), false],
    ['selection move <amount> <direction>', 'selection_move',false],
    ['flood <block>', ['flood_fill', null, null], false],
    ['flood <block> <axis>', ['flood_fill', null], [1, 'help_cmd_flood', 'help_cmd_flood_tooltip', null]],
    ['flood <block> f <flag>', _(block,flags)->flood_fill(block,null,flags), false],
    ['flood <block> <axis> f <flag>', 'flood_fill', false],
    ['brush clear', ['brush', 'clear', null], [-1, 'help_cmd_brush_clear', null, null]],
    ['brush list', ['brush', 'list', null], [-1, 'help_cmd_brush_list', null, null]],
	['brush info', ['brush', 'info', null], false],
    ['brush info <brush>', _(brush)-> brush('info', null, brush), [0, 'help_cmd_brush_info', null, null]],
    ['brush reach', ['brush', 'reach', null], false],
    ['brush reach <length>', _(length)-> brush('reach', null, length), [0, 'help_cmd_brush_reach', null, null]],
    ['brush cube <block> <size>', _(block, size_int) -> brush('cube', null, block, size_int, null), false],
    ['brush cube <block> <size> f <flag>', _(block, size_int, flags) -> brush('cube', flags, block, size_int, null), false],
    ['brush cube <block> <size> <replacement>', _(block, size_int, replacement) -> brush('cube', null, block, size_int, replacement),
        [2, 'help_cmd_brush_cube', 'help_cmd_brush_generic', null]],
    ['brush cube <block> <size> <replacement> f <flag>', _(block, size_int, replacement, flags) -> brush('cube', flags, block, size_int, replacement), false],
    ['brush cuboid <block> <x_size> <y_size> <z_size>', _(block, x_int, y_int, z_int) -> brush('cuboid', null, block, [x_int, y_int, z_int], null), false],
    ['brush cuboid <block> <x_size> <y_size> <z_size> f <flag>', _(block, x_int, y_int, z_int, flags) -> brush('cuboid', flags, block, [x_int, y_int, z_int], null), false],
    ['brush cuboid <block> <x_size> <y_size> <z_size> <replacement>', _(block, x_int, y_int, z_int, replacement) -> brush('cuboid', null, block, [x_int, y_int, z_int], replacement),
        [4, 'help_cmd_brush_cuboid', 'help_cmd_brush_generic', null]],
    ['brush cuboid <block> <x_size> <y_size> <z_size> <replacement> f <flag>', _(block, x_int, y_int, z_int, replacement, flags) -> brush('cuboid', flags, block, [x_int, y_int, z_int], replacement), false],
    ['brush sphere <block> <radius>', _(block, radius) -> brush('sphere', null, block, radius, null), false],
    ['brush sphere <block> <radius> f <flag>', _(block, radius, flags) -> brush('sphere', flags, block, radius, null), false],
    ['brush sphere <block> <radius> <replacement>', _(block, radius, replacement) -> brush('sphere', null, block, radius, replacement),
        [2, 'help_cmd_brush_sphere', 'help_cmd_brush_generic', null]],
    ['brush sphere <block> <radius> <replacement> f <flag>', _(block, radius, replacement, flags) -> brush('sphere', flags, block, radius, replacement), false],
    ['brush ellipsoid <block> <x_radius> <y_radius> <z_radius>', _(block, xr, yr, zr) -> brush('ellipsoid', null, block, [xr, yr, zr], null), false],
    ['brush ellipsoid <block> <x_radius> <y_radius> <z_radius> f <flag>', _(block, xr, yr, zr, flags) -> brush('ellipsoid', flags, block, [xr, yr, zr], null), false],
    ['brush ellipsoid <block> <x_radius> <y_radius> <z_radius> <replacement>', _(block, xr, yr, zr, replacement) -> brush('ellipsoid', null, block, [xr, yr, zr], replacement),
        [2, 'help_cmd_brush_sphereellipsoid', 'help_cmd_brush_generic', null]],
    ['brush ellipsoid <block> <x_radius> <y_radius> <z_radius> <replacement> f <flag>', _(block, xr, yr, zr, replacement, flags) -> brush('ellipsoid', flags, block, [xr, yr, zr], replacement), false],
    ['brush cylinder <block> <radius> <height>', _(block, radius, height) -> brush('cylinder', null, block, radius, height, 'y', null), false],
    ['brush cylinder <block> <radius> <height> f <flag>', _(block, radius, height, flags) -> brush('cylinder', flags, block, radius, height, 'y', null), false],
    ['brush cylinder <block> <radius> <height> <axis>', _(block, radius, height, axis) -> brush('cylinder', null, block, radius, height, axis, null), false],
    ['brush cylinder <block> <radius> <height> <axis> f <flag>', _(block, radius, height, axis, flags) -> brush('cylinder', flags, block, radius, height, axis, null), false],
    ['brush cylinder <block> <radius> <height> <axis> <replacement>', _(block, radius, height, axis, replacement) -> brush('cylinder', null, block, radius, height, axis, replacement),
        [2, 'help_cmd_brush_cylinder', 'help_cmd_brush_generic', null]],
    ['brush cylinder <block> <radius> <height> <axis> <replacement> f <flag>', _(block, radius, height, axis, replacement, flags) -> brush('cylinder', flags, block, radius, height, axis, replacement), false],
    ['brush cone <block> <radius> <height>', _(block, radius, height) -> brush('cone', null, block, radius, height, '+y', null), false],
    ['brush cone <block> <radius> <height> f <flag>', _(block, radius, height, flags) -> brush('cone', flags, block, radius, height, '+y', null), false],
    ['brush cone <block> <radius> <height> <saxis>', _(block, radius, height, axis) -> brush('cone', null, block, radius, height, axis, null), false],
    ['brush cone <block> <radius> <height> <saxis> f <flag>', _(block, radius, height, axis, flags) -> brush('cone', flags, block, radius, height, axis, null), false],
    ['brush cone <block> <radius> <height> <saxis> <replacement>', _(block, radius, height, axis, replacement) -> brush('cone', null, block, radius, height, axis, replacement),
        [2, 'help_cmd_brush_cone', 'help_cmd_brush_generic', null]],
    ['brush cone <block> <radius> <height> <saxis> <replacement> f <flag>', _(block, radius, height, axis, replacement, flags) -> brush('cone', flags, block, radius, height, axis, replacement), false],
    ['brush flood <block> <radius>', _(block, radius) -> brush('flood', null, block, radius, null), false],
    ['brush flood <block> <radius> f <flag>', _(block, radius, flags) -> brush('flood', flags, block, radius, null), false],
    ['brush flood <block> <radius> <axis>', _(block, radius, axis) -> brush('flood', null, block, radius, axis), 
        [2, 'help_cmd_brush_flood', 'help_cmd_brush_generic', null]],
    ['brush flood <block> <radius> <axis> f <flag>', _(block, radius, axis, flags) -> brush('flood', flags, block, radius, axis), false],
    ['brush line <block>', _(block) -> brush('line', null, block, null, null), false],
    ['brush line <block> f <flag>', _(block, flags) -> brush('line', flags, block, null, null), false],
    ['brush line <block> <length> ', _(block, length) -> brush('line', null, block, length, null), false],
    ['brush line <block> <length> f <flag>', _(block, length, flags) -> brush('line', flags, block, length,  null), false],
    ['brush line <block> <length> <replacement>', _(block, length, replacement) -> brush('line', null, block, length, replacement),
        [2, 'help_cmd_brush_line', 'help_cmd_brush_generic', null]],
    ['brush line <block> <length> <replacement> f <flag>', _(block, length, replacement, flags) -> brush('line', flags, block, length, replacement), false],
    ['brush paste ', _() -> brush('paste_brush', null), [-1, 'help_cmd_brush_paste', 'help_cmd_brush_generic', null]],
    ['brush paste f <flag>',  _(flags) -> brush('paste_brush', flags), false],
    ['brush prism_polygon <block> <radius> <height> <vertices>', 
        _(block, radius, height, n_points) -> brush('prism_polygon', null, block, radius, height, n_points, 'y', 0, null), false],
    ['brush prism_polygon <block> <radius> <height> <vertices> f <flag>',
       _(block, radius, height, n_points, flags) -> brush('prism_polygon', flags, block, radius, height, n_points, 'y', 0, null), false],
    ['brush prism_polygon <block> <radius> <height> <vertices> <axis>', 
       _(block, radius, height, n_points, axis) -> brush('prism_polygon', null, block, radius, height, n_points, axis, 0, null), false],
    ['brush prism_polygon <block> <radius> <height> <vertices> <axis> f <flag>',
       _(block, radius, height, n_points, axis, flags) -> brush('prism_polygon', flags, block, radius, height, n_points, axis, 0, null), false],
    ['brush prism_polygon <block> <radius> <height> <vertices> <axis> <degrees>',
       _(block, radius, height, n_points, axis, rotation) -> brush('prism_polygon', null, block, radius, height, n_points, axis, rotation, null), false],
    ['brush prism_polygon <block> <radius> <height> <vertices> <axis> <degrees> f <flag>',
       _(block, radius, height, n_points, axis, rotation, flags) -> brush('prism_polygon', flags, block, radius, height, n_points, axis, rotation, null), false],
    ['brush prism_polygon <block> <radius> <height> <vertices> <axis> <degrees> <replacement>',
       _(block, radius, height, n_points, axis, rotation, replacement) -> brush('prism_polygon', null, block, radius, height, n_points, axis, rotation, replacement), 
       [4, 'help_cmd_brush_polygon', 'help_cmd_brush_generic', null]],
    ['brush prism_polygon <block> <radius> <height> <vertices> <axis> <degrees> <replacement> f <flag>',
       _(block, radius, height, n_points, axis, rotation, replacement, flags) -> brush('prism_polygon', flags, block, radius, height, n_points, axis, rotation, replacement), false],
    ['brush prism_star <block> <outer_radius> <inner_radius> <height> <vertices>',
        _(block, outer_radius, inner_radius, height, n_points) -> brush('prism_star', null, block, outer_radius, inner_radius, height, n_points, 'y', 0, null), false],
    ['brush prism_star <block> <outer_radius> <inner_radius> <height> <vertices> f <flag>',
       _(block, outer_radius, inner_radius, height, n_points, flags) -> brush('prism_star', flags, block, outer_radius, inner_radius, height, n_points, 'y', 0, null), false],
    ['brush prism_star <block> <outer_radius> <inner_radius> <height> <vertices> <axis>',
       _(block, outer_radius, inner_radius, height, n_points, axis) -> brush('prism_star', null, block, outer_radius, inner_radius, height, n_points, axis, 0, null), false],
    ['brush prism_star <block> <outer_radius> <inner_radius> <height> <vertices> <axis> f <flag>',
       _(block, outer_radius, inner_radius, height, n_points, axis, flags) -> brush('prism_star', flags, block, outer_radius, inner_radius, height, n_points, axis, 0, null), false],
    ['brush prism_star <block> <outer_radius> <inner_radius> <height> <vertices> <axis> <degrees>',
       _(block, outer_radius, inner_radius, height, n_points, axis, rotation) -> brush('prism_star', null, block, outer_radius, inner_radius, height, n_points, axis, rotation, null), false],
    ['brush prism_star <block> <outer_radius> <inner_radius> <height> <vertices> <axis> <degrees> f <flag>',
       _(block, outer_radius, inner_radius, height, n_points, axis, rotation, flags) -> brush('prism_star', flags, block, outer_radius, inner_radius, height, n_points, axis, rotation, null), false],
    ['brush prism_star <block> <outer_radius> <inner_radius> <height> <vertices> <axis> <degrees> <replacement>',
       _(block, outer_radius, inner_radius, height, n_points, axis, rotation, replacement) -> brush('prism_star', null, block, outer_radius, inner_radius, height, n_points, axis, rotation, replacement), 
       [5, 'help_cmd_brush_star', 'help_cmd_brush_generic', null]],
    ['brush prism_star <block> <outer_radius> <inner_radius> <height> <vertices> <axis> <degrees> <replacement> f <flag>',
       _(block, outer_radius, inner_radius, height, n_points, axis, rotation, replacement, flags) -> brush('prism_star', flags, block, outer_radius, inner_radius, height, n_points, axis, rotation, replacement), false],
    ['brush feature <feature> ', _(feature) -> brush('feature', null, feature), [-1, 'help_cmd_brush_feature', 'help_cmd_brush_generic', null]],
    ['brush spray <block>', _(block) -> brush('spray', null, block, 12, 100, null), false],
    ['brush spray <block> <size_degrees>', _(block, size) -> brush('spray', null, block, size, 100, null), false],
    ['brush spray <block> <size_degrees> <count>', _(block, size, count) -> brush('spray', null, block, size, count, null), false],
    ['brush spray <block> f <flag>', _(block, flag) -> brush('spray', flag, block, 12, 100, null), false],
    ['brush spray <block> <size_degrees> f <flag>', _(block, size, flag) -> brush('spray', flag, block, size, 100, null), false],
    ['brush spray <block> <size_degrees> <count> f <flag>', _(block, size, count, flag) -> brush('spray', flag, block, size, count, null), false],
    ['brush spray held_item', _() -> brush('spray', null, null, 12, 100, null), false],
    ['brush spray held_item <size_degrees>', _(size) -> brush('spray', null, null, size, 100, null), false],
    ['brush spray held_item <size_degrees> <count>', _(size, count) -> brush('spray', null, null, size, count, null), false],
    ['brush spray held_item f <flag>', _(flag) -> brush('spray', flag, null, 12, 100, null), false],
    ['brush spray held_item <size_degrees> f <flag>', _(size, flag) -> brush('spray', flag, null, size, 100, null), false],
    ['brush spray held_item <size_degrees> <count> f <flag>', _(size, count, flag) -> brush('spray', flag, null, size, count, null), false],
    ['brush spray <block> <size_degrees> <count> <replacement>', _(block, size, count, replacement) -> brush('spray', null, block, size, count, replacement), [1, 'help_cmd_spray', 'help_cmd_brush_generic', null]],
    ['brush spray <block> <size_degrees> <count> <replacement> f <flag>', _(block, size, count, replacement, flag) -> brush('spray', flag, block, size, count, replacement), false],
    ['brush spray held_item <size_degrees> <count> <replacement>', _(size, count, replacement) -> brush('spray', null, null, size, count, replacement), [0, 'help_cmd_spray_held', 'help_cmd_brush_generic', null]],
    ['brush spray held_item <size_degrees> <count> <replacement> f <flag>', _(size, count, replacement, flag) -> brush('spray', flag, null, size, count, replacement), false],

    ['shape cube <block> <size>', _(block, size_int) -> cube(player()~'pos', [block, size_int, null], null), false],
    ['shape cube <block> <size> f <flag>', _(block, size_int, flags) -> cube(player()~'pos', [block, size_int, null], flags), false],
    ['shape cube <block> <size> <replacement>', _(block, size_int, replacement) -> cube(player()~'pos', [block, size_int, replacement], null),
        [2, 'help_cmd_shape_cube', null, null]],
    ['shape cube <block> <size> <replacement> f <flag>', _(block, size_int, replacement, flags) -> cube(player()~'pos', [block, size_int, replacement], flags), false],
    ['shape cuboid <block> <x_size> <y_size> <z_size>', _(block, x_int, y_int, z_int) -> cuboid(player()~'pos', [block, [x_int, y_int, z_int], null], null), false],
    ['shape cuboid <block> <x_size> <y_size> <z_size> f <flag>', _(block, x_int, y_int, z_int, flags) -> cuboid(player()~'pos', [block, [x_int, y_int, z_int], null], flags), false],
    ['shape cuboid <block> <x_size> <y_size> <z_size> <replacement>', _(block, x_int, y_int, z_int, replacement) -> cuboid(player()~'pos', [block, [x_int, y_int, z_int], replacement], null),
        [4, 'help_cmd_brush_cuboid', 'help_cmd_brush_generic', null]],
    ['shape cuboid <block> <x_size> <y_size> <z_size> <replacement> f <flag>', _(block, x_int, y_int, z_int, replacement, flags) -> cuboid(player()~'pos', [block, [x_int, y_int, z_int], replacement], flags), false],
    ['shape sphere <block> <radius>', _(block, radius) -> sphere(player()~'pos', [block, radius, null], null), false],
    ['shape sphere <block> <radius> f <flag>', _(block, radius, flags) -> sphere(player()~'pos', [block, radius, null], flags), false],
    ['shape sphere <block> <radius> <replacement>', _(block, radius, replacement) -> sphere(player()~'pos', [block, radius, replacement], null),
        [2, 'help_cmd_brush_sphere', 'help_cmd_brush_generic', null]],
    ['shape sphere <block> <radius> <replacement> f <flag>', _(block, radius, replacement, flags) -> sphere(player()~'pos', [block, radius, replacement], flags), false],
    ['shape ellipsoid <block> <x_radius> <y_radius> <z_radius>', _(block, xr, yr, zr) -> ellipsoid( player()~'pos', [block, [xr, yr, zr], null], null), false],
    ['shape ellipsoid <block> <x_radius> <y_radius> <z_radius> f <flag>', _(block, xr, yr, zr, flags) -> ellipsoid( player()~'pos', [block, [xr, yr, zr], null], flags), false],
    ['shape ellipsoid <block> <x_radius> <y_radius> <z_radius> <replacement>', _(block, xr, yr, zr, replacement) -> ellipsoid( player()~'pos', [block, [xr, yr, zr], replacement], null),
        [2, 'help_cmd_brush_sphereellipsoid', 'help_cmd_brush_generic', null]],
    ['shape ellipsoid <block> <x_radius> <y_radius> <z_radius> <replacement> f <flag>', _(block, xr, yr, zr, replacement, flags) -> ellipsoid( player()~'pos', [block, [xr, yr, zr], replacement], flags), false],
    ['shape cylinder <block> <radius> <height>', _(block, radius, height) -> cylinder(player()~'pos', [block, radius, height, 'y', null], null), false],
    ['shape cylinder <block> <radius> <height> f <flag>', _(block, radius, height, flags) -> cylinder(player()~'pos', [block, radius, height, 'y', null], flags), false],
    ['shape cylinder <block> <radius> <height> <axis>', _(block, radius, height, axis) -> cylinder(player()~'pos', [block, radius, height, axis, null], null), false],
    ['shape cylinder <block> <radius> <height> <axis> f <flag>', _(block, radius, height, axis, flags) -> cylinder(player()~'pos', [block, radius, height, axis, null], flags), false],
    ['shape cylinder <block> <radius> <height> <axis> <replacement>', _(block, radius, height, axis, replacement) -> cylinder(player()~'pos', [block, radius, height, axis, replacement], null),
        [2, 'help_cmd_brush_cylinder', 'help_cmd_brush_generic', null]],
    ['shape cylinder <block> <radius> <height> <axis> <replacement> f <flag>', _(block, radius, height, axis, replacement, flags) -> cylinder(player()~'pos', [block, radius, height, axis, replacement], flags), false],
    ['shape cone <block> <radius> <height>', _(block, radius, height) -> cone(player()~'pos', [block, radius, height, '+y', null], null), false],
    ['shape cone <block> <radius> <height> f <flag>', _(block, radius, height, flags) -> cone(player()~'pos', [block, radius, height, '+y', null], flags), false],
    ['shape cone <block> <radius> <height> <saxis>', _(block, radius, height, axis) -> cone(player()~'pos', [block, radius, height, axis, null], null), false],
    ['shape cone <block> <radius> <height> <saxis> f <flag>', _(block, radius, height, axis, flags) -> cone(player()~'pos', [block, radius, height, axis, null], flags), false],
    ['shape cone <block> <radius> <height> <saxis> <replacement>', _(block, radius, height, axis, replacement) -> cone(player()~'pos', [block, radius, height, axis, replacement], null), 
        [2, 'help_cmd_brush_cone', 'help_cmd_brush_generic', null]],
    ['shape cone <block> <radius> <height> <saxis> <replacement> f <flag>', _(block, radius, height, axis, replacement, flags) -> cone(player()~'pos', [block, radius, height, axis, replacement], flags), false],
    ['shape prism_polygon <block> <radius> <height> <vertices>', 
        _(block, radius, height, n_points) -> prism_polygon(player()~'pos', [block, radius, height, n_points, 'y', 0, null], null), false],
    ['shape prism_polygon <block> <radius> <height> <vertices> f <flag>',
       _(block, radius, height, n_points, flags) -> prism_polygon(player()~'pos', [block, radius, height, n_points, 'y', 0, null], flags), false],
    ['shape prism_polygon <block> <radius> <height> <vertices> <axis>',
       _(block, radius, height, n_points, axis) -> prism_polygon(player()~'pos', [block, radius, height, n_points, axis, 0, null], null), false],
    ['shape prism_polygon <block> <radius> <height> <vertices> <axis> f <flag>',
       _(block, radius, height, n_points, axis, flags) -> prism_polygon(player()~'pos', [block, radius, height, n_points, axis, 0, null], flags), false],
    ['shape prism_polygon <block> <radius> <height> <vertices> <axis> <degrees>',
       _(block, radius, height, n_points, axis, rotation) -> prism_polygon(player()~'pos', [block, radius, height, n_points, axis, rotation, null], null), false],
    ['shape prism_polygon <block> <radius> <height> <vertices> <axis> <degrees> f <flag>',
       _(block, radius, height, n_points, axis, rotation, flags) -> prism_polygon(player()~'pos', [block, radius, height, n_points, axis, rotation, null], flags), false],
    ['shape prism_polygon <block> <radius> <height> <vertices> <axis> <degrees> <replacement>',
       _(block, radius, height, n_points, axis, rotation, replacement) ->prism_polygon(player()~'pos', [block, radius, height, n_points, axis, rotation, replacement], null), 
       [4, 'help_cmd_brush_polygon', 'help_cmd_brush_generic', null]],
    ['shape prism_polygon <block> <radius> <height> <vertices> <axis> <degrees> <replacement> f <flag>',
       _(block, radius, height, n_points, axis, rotation, replacement, flags) -> prism_polygon(player()~'pos', [block, radius, height, n_points, axis, rotation, replacement], flags), false],
    ['shape prism_star <block> <outer_radius> <inner_radius> <height> <vertices>',
        _(block, outer_radius, inner_radius, height, n_points) -> prism_star(player()~'pos', [block, outer_radius, inner_radius, height, n_points, 'y', 0, null], null), false],
    ['shape prism_star <block> <outer_radius> <inner_radius> <height> <vertices> f <flag>',
       _(block, outer_radius, inner_radius, height, n_points, flags) -> prism_star(player()~'pos', [block, outer_radius, inner_radius, height, n_points, 'y', 0, null], flags), false],
    ['shape prism_star <block> <outer_radius> <inner_radius> <height> <vertices> <axis>',
       _(block, outer_radius, inner_radius, height, n_points, axis) -> prism_star(player()~'pos', [block, outer_radius, inner_radius, height, n_points, axis, 0, null], null), false],
    ['shape prism_star <block> <outer_radius> <inner_radius> <height> <vertices> <axis> f <flag>',
       _(block, outer_radius, inner_radius, height, n_points, axis, flags) -> prism_star(player()~'pos', [block, outer_radius, inner_radius, height, n_points, axis, 0, null], flags), false],
    ['shape prism_star <block> <outer_radius> <inner_radius> <height> <vertices> <axis> <degrees>',
       _(block, outer_radius, inner_radius, height, n_points, axis, rotation) -> prism_star(player()~'pos', [block, outer_radius, inner_radius, height, n_points, axis, rotation, null], null), false],
    ['shape prism_star <block> <outer_radius> <inner_radius> <height> <vertices> <axis> <degrees> f <flag>',
       _(block, outer_radius, inner_radius, height, n_points, axis, rotation, flags) -> prism_star(player()~'pos', [block, outer_radius, inner_radius, height, n_points, axis, rotation, null], flags), false],
    ['shape prism_star <block> <outer_radius> <inner_radius> <height> <vertices> <axis> <degrees> <replacement>',
       _(block, outer_radius, inner_radius, height, n_points, axis, rotation, replacement) ->prism_star(player()~'pos', [block, outer_radius, inner_radius, height, n_points, axis, rotation, replacement], null), 
       [4, 'help_cmd_brush_polygon', 'help_cmd_brush_generic', null]],
    ['shape prism_star <block> <outer_radius> <inner_radius> <height> <vertices> <axis> <degrees> <replacement> f <flag>',
       _(block, outer_radius, inner_radius, height, n_points, axis, rotation, replacement, flags) -> prism_star(player()~'pos', [block, outer_radius, inner_radius, height, n_points, axis, rotation, replacement], flags), false],

    // we need a better way of changing 'settings'
    ['settings quick_select <bool>', _(b) -> global_quick_select = b, false],

    ['structure list',['structure',null,'list',null],false],//todo help for this
    ['structure load <structure>',['structure','load',null],false],
    ['structure load <structure> f <flag>',_(s,f)->structure(s, 'load', {'flags'->f}),false],
    ['structure load <structure> <pos>',_(s,p)->structure(s,'load',{'pos'->p}),false],
    ['structure load <structure> <pos> f <flag>',_(s,p,f)->structure(s,'load',{'pos'->p,'flags'->f})],
    ['structure delete <structure>',['structure','delete',null],false],
    ['structure save <name>',['structure','save',null],false],
    ['structure save <name> force',['structure','save',{'force'->true}],false],
    ['structure save <name> entities',['structure','save',{'include_entities'->true}],false],
    ['structure save <name> entities force',['structure','save',{'force'->true,'include_entities'->true}],false],
    ['structure save <name> clipboard',['structure','save_clipboard',{'clipboard'->true}],false],
    ['structure save <name> clipboard force',['structure','save_clipboard',{'clipboard'->true,'force'->true}],false],
    ['structure copy <name>',['structure','copy',null],false],
    ['structure copy <name> force',['structure','copy',{'force'->true}],false],
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

get_features_list() -> (
    features = plop():'features';
    put(features, null, , 'extend');
    put(features, null, plop():'scarpet_custom', 'extend');
    features;
);

__config()->{
    'commands'-> global_commands_map,
    'arguments'->{
        'replacement'->{'type'->'blockpredicate'},
        'moves'->{'type'->'int','min'->1,'suggest'->[1]},//todo decide on whether or not to add max undo limit
        'degrees'->{'type'->'int','suggest'->[0,90]},
        'axis'->{'type'->'term','options'->['x','y','z']},
        'saxis'->{'type'->'term', 'options'->['+x', '-x', '+y', '-y', '+z', '-z']},
        'sides'->{'type'->'term', 'options'->['x', 'y', 'z', 'xy', 'xz', 'yz', 'xyz']},
        'wand'->{'type'->'item','suggest'->['wooden_sword','wooden_axe']},
        'direction'->{'type'->'term','options'->['north','south','east','west','up','down']},
        'count'->{'type'->'int','min'->1,'suggest'->[]},
        'flag' -> {
            'type' -> 'term',
            'suggester' -> _(args) -> (
                typed = if(args:'flag', args:'flag', '-');
                typed_list = split(typed);
                checked_list = [];
                for(typed_list,
                    if((global_flags~_ == null || checked_list~_ != null) && (_i != 0 || _ != '-'),
                        return([]);
                    );
                    put(checked_list,length(checked_list),_);
                );
                filtered_flags = filter(global_flags,!(typed_list~_));
                map(filtered_flags,typed+_);
            ),
        },
        'amount'->{'type'->'int'},
        'magnitude'->{'type'->'float','suggest'->[1,2,0.5]},
        'lang'->{'type'->'term','suggester'->_(ignored)->_get_lang_list()},
        'page'->{'type'->'int','min'->1,'suggest'->[1,2,3]},
        'name'->{'type'->'string','suggest'->[]},
        'structure'->{
            'type'->'term',
            'suggester' -> _(args) -> map(list_files('structures','nbt'),_-'structures/'),
        },
        'radius'->{'type'->'int','min'->1,'suggest'->[5, 10, 30]},
        'height'->{'type'->'int','min'->1,'suggest'->[5, 10, 30]},
        'size'->{'type'->'int','min'->1,'suggest'->[5, 10, 30]},
        'length'->{'type'->'int','min'->1,'suggest'->[5, 10, 30]},
        'vertices'->{'type'->'int', 'min'->3 ,'suggest'->[3, 5, 7]},
        'feature'->{'type'->'term','options'-> get_features_list()},
		'brush'->{'type'->'term', 'suggester'->_(ignered)->keys(global_brushes)},
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
                            _()->_translate('help_selection_bounds', _get_marker_position(global_selection:'from'), _get_marker_position(global_selection:'to'))
                            ,'help_make_selection'), null, '?wand '],
        [_translate('help_selected_wand'), 'wand ', _()->_translate('help_selected_wand_item',title(replace(global_wand, '_', ' '))), 'help_sel_wand_tooltip', '?wand '],
        [_translate('help_app_lang'), 'lang ', _()->_translate('help_app_lang_selected', _translate('language')), 'help_app_lang_tooltip', '?lang '],
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

global_flags = ['w','a','e','h','u','b','p','d','s','g'];

//FLAGS:
//w     waterlog block if previous block was water(logged) too
//a     don't paste air
//e     consider entities as well
//h     make shapes hollow
//u     set blocks without updates
//b     set biome
//p     only replace air
//d     "dry" out the pasted structure (remove water and waterlogged)
//s     keep block states of replaced block, if new block matches
//g     when replacing air or water, some greenery gets repalced too


_parse_flags(flags) ->(
    if(!flags, return({}));
    symbols = split(flags);
    if(symbols:0 != '-', return({}));
    flag_set = {};
    for(split(flags),
        if(_~'[A-Z,a-z]',flag_set+=_);
    );
    flag_set;
);

volume_blocks(pos1,pos2)->(
    retlist=[];
    volume(pos1,pos2,retlist+=_);
    retlist
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

global_lang_id='en_us';//default en_us (and fill it below)
global_lang_keys = global_default_lang = {
    'language_code' ->    'en_us',
    'language' ->         'English',

    'help_header_prefix' ->       'c ----------------- [ ',
    'help_header_title' ->        'd World-Edit Help',
    'help_header_suffix' ->       'c  ] -----------------',
    'help_welcome' ->             'c Welcome to the World-Edit Scarpet app\'s help!',
    'help_welcome_tooltip' ->     'y Hooray!',
    'help_your_selection' ->      'c Your selection',
    'help_selection_bounds' ->    'l From %s to %s',
    'help_make_selection' ->      'c Use your wand to select with a start and final position',
    'help_selected_wand' ->       'c Selected wand', //Could probably be used in more places
    'help_selected_wand_item' ->  'l %s',
    'help_sel_wand_tooltip' ->    'g Use the wand command to change',
    'help_app_lang' ->            'c App Language ', //Could probably be used in more places, todo decide whether to hardcode this
    'help_app_lang_selected' ->   'l %s',
    'help_app_lang_tooltip' ->    'g Use the lang command to change it',
    'help_list_title' ->          'y Command list (without prefix):',
    'help_pagination_prefix' ->   'c --------------- ',
    'help_pagination_page' ->     'y Page %d ',
    'help_pagination_suffix' ->   'c  ---------------',
    'help_pagination_first' ->    'g Go to first page',
    'help_pagination_prev' ->     'g Go to previous page (%d)',
    'help_pagination_next' ->     'g Go to next page (%d)',
    'help_pagination_last' ->     'g Go to last page (%d)',
    'help_cmd_help' ->            'l Shows this help menu, or a specified page',
    'help_cmd_lang' ->            'l Changes current app\'s language to [lang]',
    'help_cmd_lang_tooltip'->     'g Available languages are %s',
    'help_cmd_set' ->             'l Set selection to block, filterable',
    'help_cmd_set_tooltip' ->     'g You can use a tag in the replacement argument',
    'help_cmd_undo' ->            'l Undoes last n moves, one by default',
    'help_cmd_undo_all' ->        'l Undoes the entire action history',
    'help_cmd_undo_history' ->    'l Shows the history of undone actions',
    'help_cmd_redo' ->            'l Redoes last n undoes, one by default',
    'help_cmd_redo_tooltip' ->    'g Also shows up in undo history',
    'help_cmd_redo_all' ->        'l Redoes the entire undo history',
    'help_cmd_wand' ->            'l Sets held item as wand or gives it if hand is empty',
    'help_cmd_wand_2' ->          'l Changes the current wand item',
    'help_cmd_rotate' ->          'l Rotates [deg] about [pos]',
    'help_cmd_rotate_tooltip' ->  'g Axis must be x, y or z',
    'help_cmd_stack' ->           'l Stacks selection n times in dir',
    'help_cmd_stack_tooltip' ->   'g If not provided, direction is player\s view direction by default',
    'help_cmd_expand' ->          'l Expands sel [magn] from pos', //This is not understandable
    'help_cmd_expand_tooltip' ->  'g Expands the selection [magnitude] from [pos]',
    'help_cmd_move' ->            'l Moves selection to <pos>',
    'help_cmd_walls' ->           'l Set walls of the selection',
    'help_cmd_walls_tooltip' ->   'l Use [sides] to choose which sides to generate',
    'help_cmd_brush_clear' ->     'l Unregisters current item as brush',
    'help_cmd_brush_list' ->      'l Lists all currently regiestered brushes and their actions',
    'help_cmd_brush_info' ->      'l Gives detailed info of currently held brush',
    'help_cmd_brush_generic' ->   'l Hold item to turn into brush',
    'help_cmd_brush_cube' ->      'l Register brush to create cube of side length [size] out of [block]',
    'help_cmd_brush_cuboid' ->    'l Register brush to create cuboid of dimensions [x] [y] and [z] out of [block]',
    'help_cmd_brush_sphere' ->    'l Register brush to create sphere of radius [size] out of [block]',
    'help_cmd_brush_ellipsoid' -> 'l Register brush to create ellipsoid with radii [x_radius], [y_radius] and [z_radius] out of [block]',
    'help_cmd_brush_cylinder' ->  'l Register brush to create cylinder with [radius] and [height] along [axis] out of [block]',
    'help_cmd_brush_cone' ->      'l Register brush to create cylinder with [radius] and [height] along [axis] in the direciton given by the sign',
    'help_cmd_brush_polygon' ->   'l Register brush to create polygon prism with [vertices] ammount of sides',
    'help_cmd_brush_star' ->      'l Register brush to create star prism with [vertices] ammount of points',
    'help_cmd_brush_line' ->      'l Register brush to create line from player to where you click of [length], if given',
    'help_cmd_brush_flood' ->     'l Register brush to perfrm flood fill out of [block] starting on right clicked block',
    'help_cmd_brush_paste' ->     'l Register brush to paste current clipboard with origin on targeted block',
    'help_cmd_brush_feature' ->   'l Register brush to plop feature',
    'help_cmd_spray' -> 	  	  'l Register brush to spray paint with given radius an point count',
    'help_cmd_spray_held' -> 	  'l Register brush to spray paint out of block held in the offhand',

    'filled' ->                   'gi Filled %d blocks',                                    // blocks number
    'no_undo_history' ->          'w No undo history to show for player %s',                // player
    'many_undo' ->                'w Undo history for player %s is very long, showing only the last ten items', // player
    'entry_undo_1' ->             'w %d: type: %s',                                         //index, command type
    'entry_undo_2 ' ->            'w     affected positions: %s',                           //blocks number
    'no_undo' ->                  'r No actions to undo for player %s',                     // player
    'more_moves_undo' ->          'w Your number is too high, undoing all moves for %s',    // player
    'success_undo' ->             'gi Successfully undid %d operations, filling %d blocks', // moves number, blocks number
    'no_redo' ->                  'r No actions to redo for player %s',                     // player
    'more_moves_redo' ->          'w Your number is too high, redoing all moves for %s',    // player
    'success_redo' ->             'gi Successfully redid %d operations, filling %d blocks', // moves number, blocks number


    'clear_clipboard' ->          'wi Cleared player %s\'s clipboard',
    'copy_clipboard_not_empty' -> 'ri Clipboard for player %s is not empty, use "/copy force" to overwrite existing clipboard data',//player
    'copy_force' ->               'ri Overwriting previous clipboard selection with new one',
    'copy_success' ->             'gi Successfully copied %s blocks and %s entities to clipboard',//blocks number, entity number
    'paste_no_clipboard' ->       'ri Cannot complete action, clipboard for player %s is empty',//player

    'langs_changed' ->            'gi Language changed to %s.',                             //language we changed to
    'langs_completeness' ->       'gi Note: %s translation is only %s%% translated: %s missing strings',//language, percent of present translations, no. of missing translations
    'langs_availables' ->         'y Available languages are:',
    'langs_add_more_tip' ->       'y Add more languages to the %s folder to use them! Get them from our repo!', //The folder
    'langs_not_found' ->          'r Language file for %s not found',

    'move_selection_no_player_error' -> 'r To move selection in the direction of the player, you need to have a player',
    'no_selection_error' ->             'r Missing selection for operation for player %s', //player
    'new_wand' ->                       'wi %s is now the app\'s wand, use it with care.', //wand item
    'invalid_wand' ->                   'r Wand has to be a tool or weapon',

   
	'brush_item_tooltip' ->				'^g Click to get one!',
    'brush_info_title' ->               'y Brush registered to ',
	'brush_info_action' ->				'b \ Action: ',
	'brush_info_params' ->				'b \ Parameters: ',
	'brush_info_params_tooltip' ->		'^g See help to understand what each parameter is',
	'brush_info_flags' ->				'b \ Flags: ',
	'brush_info_no_flags' ->			'w no flags',
    'brush_replaced' ->                 'w Replacing previous action for brush in %s',
	'brush_new' ->						'w Registerd new %s brush to %s', //item, action
    'brush_list_header' ->              'bc === Current brushes are ===',
    'brush_empty_list' ->               'gi No brushes registerd so far',
    'brush_extra_info' ->               'ig For detailed info on a brush click the [i] icon',
    'brush_new_reach' ->                'w Brush reach was set to %d blocks',
    'brush_reach' ->                    'w Brush reach is currently %d blocks', // reach
	'no_brush_error'->					'r %s in not a brush', //item
    'no_longer_brush' -> 				'w %s is no longer a brush',

    'structure_list' ->                'w List of structures:',
    'saved_structure' ->               'w Saved structure as %s.nbt',                                 //structure name
    'existing_structure' ->            'r Existing file %s.nbt, use \'force\' to overwrite',          //structure name
    'structure_overwrite' ->           'ri Overwriting %s.nbt with a new structure',                  //structure name
    'structure_delete_success' ->      'gi Successfully deleted %s.nbt',                              //structure name
    'structure_delete_fail' ->         'ri Failed to delete %s.nbt, no such file exists',             //structure name
    'structure_load_fail' ->           'ri Failed to load %s.nbt, no such file exists',               //structure name
    //Block-setting actions
    'action_cube'->                'cube',
    'action_cuboid' ->             'cuboid',
    'action_ellipsoid' ->          'ellipsoid',
    'action_sphere' ->             'sphere',
    'action_cylinder' ->           'cylinder',
    'action_cone' ->               'cone',
    'action_line' ->               'line',
    'action_prism_polygon' ->      'prism_polygon',
    'action_prism_star' ->         'prism_star',
    'action_structure_paste' ->    'structure_paste',
    'action_set' ->                'set',
    'action_flood' ->              'flood',
    'action_rotate' ->             'rotate',
    'action_move' ->               'move',
    'action_stack' ->              'stack',
    'action_expand' ->             'expand',
    'action_paste' ->              'paste',
    'action_spray' -> 			   'spray',
};
task(_()->write_file('langs/en_us','json',global_default_lang)); // Make a template for translators. Async cause why not. Maybe make an async section at the bottom?


_get_lang_list() -> (
  filter(map(list_files('langs','json'), slice(_,6)), !(_~' ')); // Any JSON files in /langs/ that doesn't have spaces
);

_change_lang(lang)->(
    languages = _get_lang_list();
    if(lang == null,
        _print(player(),'langs_availables');
        for(languages,
            print(format('g - ','c '+_));
        );
        if(player()~'permission_level' == 4, //Don't print to people that can't access it
            _print(player(),'langs_add_more_tip',system_info('app_name')+'.data/langs/');
        );
    , lang == 'en_us', // Use builtin
        global_lang_id=lang;
        global_lang_keys=global_default_lang;
        _print(player(),'langs_changed',_translate('language'));
    , languages ~ lang,
        global_lang_id=lang;
        global_lang_keys=read_file('langs/'+lang,'json');
        _print(player(),'langs_changed',_translate('language'));
        task(_()->( //Checking translation completeness - async in case langs get long enough in the future, or a better check is done
            missing_translations_list=[];       // Currently this doesn't check for keys present in the translation but not in the default, etc
            for(global_default_lang,           // although that is workarounded in the percentage calculation
                if(!has(global_lang_keys,_),
                    missing_translations_list+=_;
                )
            );
            missing = length(missing_translations_list);
            if(missing,
                logger('warn',
                    '[World-Edit Scarpet] Current translation for '+_translate('language')+' is incomplete. Missing keys: \n'
                        +join('\n- ',missing_translations_list));
                logger('warn', '[World-Edit Scarpet] Until fixed, default language (english) keys will be used');
                _print(player(),'langs_completeness',_translate('language'),round(100 - missing/(length(global_lang_keys)+missing)*100),missing);
            );
        ));
    ,
        _print(player(), 'langs_not_found',lang);
    )
);

_translate(key, ... replace_list) ->
    _translate_internal(key, replace_list);

_translate_internal(key, replace_list) -> (
    if(has(global_lang_keys, key || key == null),
        string = global_lang_keys:key
    ,
        string = global_default_lang:key;
    );
    if(key == null,
        null,
        str(string, replace_list)
    )
);

_print(player, key, ... replace) ->
    print(player, format(_translate_internal(key, replace)));

_error(player, key, ... replace)->
    exit(print(player, format(_translate_internal(key, replace))));

// Brush

global_brushes = {};
global_brush_reach = 100;
global_brushes_parameters_map = {
	'cube'-> ['block', 'size', 'replacement'],
	'cuboid'-> ['block', 'size', 'replacement'],
	'shpere' -> ['block', 'radius', 'replacement'],
	'ellipsoid' -> ['block', 'radii', replacement],
	'cylinder' -> ['block', 'radius', 'height', 'axis', 'replacement'],
	'cone' -> ['block', 'radius', 'height', 'saxis', 'replacement'],
	'prism_polygon' -> ['block', 'radius', 'height', 'vertices', 'axis', 'rotation', 'replacement'],
	'prism_star' -> ['block', 'outer_radius', 'inner_radius', 'height', 'vertices', 'axis', 'rotation', 'replacement'],
	'line' -> ['block', 'length', 'replacement'],
	'flood' -> ['block', 'radius', 'axis'],
	'feature' -> ['what'],
	'spray' -> ['block', 'size', 'count', 'replacement'],
};

brush(action, flags, ...args) -> (
    player = player();
    held_item = player~'holds':0;
    if(held_item==global_wand, _error(player, 'bad_wand_brush_error'));

    if(
        action=='clear',
        if(has(global_brushes, held_item),
            delete(global_brushes, held_item);
            _print(player, 'no_longer_brush', held_item),
            _error(player, 'no_brush_error', held_item)
        ),
        action=='list', //TODO imprvove list with interactiveness
        if(global_brushes,
        	print(player, '');
            _print(player, 'brush_list_header');
            for(pairs(global_brushes),
				print(player, format(
					str('lb \ %s: ', item=_:0),
					_translate('brush_item_tooltip'), 
					str('!/give %s %s',player, item),
					str('w %s ', _:1:0),
					'db [i]',
					str('^g Click for more info on %s brush', item),
					str('!/world-edit brush info %s', item)
				));
            );
            _print(player, 'brush_extra_info'),
            _print(player, 'brush_empty_list')
        ),
        action=='info', //TODO improve info with better descriptions
        if(args, held_item=args:0);
        if(has(global_brushes, held_item),
        	print(player, '');
        	print(player, format( _translate('brush_info_title'), str('bl %s', held_item), _translate('brush_item_tooltip'), str('!/give %s %s',player, held_item) ));
			print(player, format( _translate('brush_info_action'), str('w %s', (params=global_brushes:held_item):0) ));
			if( (param_names=global_brushes_parameters_map:(params:0)) == null,
				 print(player, format( _translate('brush_info_params'), str('w %s%s','',params:1), _translate('brush_info_params_tooltip') )),
				 _print(player, 'brush_info_params');
				 for(param_names, print(player, str('  %s: %s', _, params:1:_i)))
			);
			print(player, format( _translate('brush_info_flags'), if(params:2, str('w %s', params:2), _translate('brush_info_no_flags') ) )),
			// if it's not a brush
            _error(player, 'no_brush_error', held_item)
        ),
        action=='reach',
        if(args,
            global_brush_reach = args:0;
            _print(player, 'brush_new_reach', args:0),
            _print(player, 'brush_reach', global_brush_reach)
        ),
        // else, register new brush with given action
        if(has(global_brushes, held_item),
            _print(player, 'brush_replaced', held_item)
        );
        global_brushes:held_item = [action, args, flags];
        _print(player, 'brush_new', action, held_item);

        if(action=='feature', print(player, format('d Beware, placing features is very experimental and doesn\'t have support for the undo function')))
    )
);

_brush_action(pos, brush) -> (
    [action, args, flags] = global_brushes:brush;
    call(action, pos, args, flags)
);

cube(pos, args, flags) -> (
    _cuboid(pos, args, flags);
    add_to_history('cube',player())
);

cuboid(pos, args, flags) -> (
    _cuboid(pos, args, flags);
    add_to_history('cuboid',player())
);

_cuboid(pos, args, flags) -> (
    [block, size, replacement] = args;

    half_size = (size-1)/2;
    min_corner = pos-half_size;
    max_corner = pos+half_size;

    if(flags~'h', 
        _walls_generic(min_corner, max_corner, 'xyz', block, replacement, flags),
        volume(min_corner, max_corner, set_block(_, block, replacement, flags, {}))
    );
);

_sq_distance(p1, p2) -> reduce(p1-p2, _a + _*_, 0);

_fill_shape(from, to, block, replacement, flags) -> (
    if(flags~'h',
        // hollow
        to_set = {};
        volume(from, to,
            if( _is_inside_shape(_), to_set += pos(_))
        );
        for(keys(to_set),
            if(!all(neighbours(_), has(to_set, pos(_))),
                set_block(_, block, replacement, flags, {})
            )
        ),

        // not hollow
        volume(from, to,
            if( _is_inside_shape(_),
                set_block(_, block, replacement, flags, {})
            )
        )
    )

);

ellipsoid(pos, args, flags) -> (
    [block, radii, replacement] = args;

    _is_inside_shape(block, outer(pos), outer(radii)) -> _sq_distance((pos(block)-pos) / radii, 0) <= 1;
    _fill_shape(pos-radii, pos+radii, block, replacement, flags);

    add_to_history('action_ellipsoid',player())
);

sphere(pos, args, flags) -> (
    [block, radius, replacement] = args;

    if(radius == 1,
        set_block(pos, block, replacement, flags, {}),

        _is_inside_shape(block, outer(pos), outer(radius)) -> _sq_distance(pos, pos(block)) <= radius*radius;
        _fill_shape(pos-radius, pos+radius, block, replacement, flags);
    );

    add_to_history('action_sphere',player())
);

cylinder(pos, args, flags) -> (
    [block, radius, height, axis, replacement] = args;

    offset = _define_flat_distance_squared(axis, radius, height);

    if(radius == 1,
        set_block(pos, block, replacement, flags, {}),

         _is_inside_shape(block, outer(pos), outer(radius)) -> _flat_sq_distance(pos, pos(block)) <= radius*radius;
        _fill_shape(pos-offset, pos+offset, block, replacement, flags);
    );

    add_to_history('action_cylinder',player())
);


cone(pos, args, flags) -> (
    [block, radius, height, signed_axis, replacement] = args;

    axis = slice(signed_axis, 1);
    offset = _define_flat_distance_squared(axis, radius, height);
    axis_index = ['x', 'y', 'z']~axis;

    // define direction
    if( slice(signed_axis, 0, 1) == '-',
        _inside_cone_fun(y, r, outer(height), outer(radius)) -> y <= height/2 - height/radius * r,
        _inside_cone_fun(y, r, outer(height), outer(radius)) -> y >= -height/2 + height/radius * r,
    );


    if(radius == 1,
        set_block(pos, block, replacement, flags, {}),

        _is_inside_shape(block, outer(pos), outer(radius), outer(axis_index)) -> (
            r = sqrt( _flat_sq_distance(pos, pos(block)) );
            r <= radius && _inside_cone_fun((pos-pos(block)):axis_index, r)
        );
        _fill_shape(pos-offset, pos+offset, block, replacement, flags);
    );

    add_to_history('action_cone',player())
);

_define_flat_distance_squared(axis, radius, size) -> (
    if(
        axis=='x',
            _flat_sq_distance(p1, p2) -> (p = p1-p2; p:1*p:1 + p:2*p:2);
            offset = [radius, (size-1)/2, radius],
        axis=='y',
            _flat_sq_distance(p1, p2) -> (p = p1-p2; p:0*p:0 + p:2*p:2);
            offset = [radius, (size-1)/2, radius],
        axis=='z',
            _flat_sq_distance(p1, p2) -> (p = p1-p2; p:0*p:0 + p:1*p:1);
            offset = [radius, radius, (size-1)/2]
    );
);

flood(pos, args, flags) -> (

    start = pos;
    [block, radius, axis] = args;
    if(block(start)==block, return());

    // test if inside sphere
    _flood_tester(pos, outer(start), outer(radius)) -> (
        _sq_distance(pos, start) <= radius*radius
    );

    _flood_generic(block, axis, start, flags);
);

line(pos, args, flags) -> (
    [block, length, replacement] = args;

    player = player();
    if(length,
        final_pos = map(pos(player)+player~'look'*length+[0, player~'eye_height', 0], floor(_)),
        final_pos = pos
    );

    m = player ~ 'pos' + [0, player~'eye_height', 0] - final_pos; // line slopes

    max_size = max(map(m, abs(_)));
    t = l(range(max_size))/max_size;
    for(t,
        b = m * _ + final_pos;
        set_block(b, block, replacement, flags, {})
    );

    add_to_history('action_line',player)
);

paste_brush(pos, args, flags) -> (
    paste(pos, flags);
);


prism_polygon(pos, args, flags) -> (
    [block, radius, height, n_points, axis, rotation, replacement] = args;

    center = map(pos, floor(_));
    flags = _parse_flags(flags);

    // get points in inner and pouter radius + interlace them
    points = _get_circle_points(radius, n_points, rotation);
    points:length(points) = points:0; // add first point at the end to close curve

    // get points and draw the connecting lines
    perimeter = _connect_with_lines(points, center, axis);
    interior = _flood_fill_shape(perimeter, center, axis);

    offset = _get_prism_offset(height, axis);
    if(flags~'h',
        for(perimeter, volume(_+offset, _-offset, set_block(_, block, replacement, flags, {}) ));
        for(interior,
            set_block(_+offset, block, replacement, flags, {});
            set_block(_-offset, block, replacement, flags, {});
        ),
        for(interior, volume(_+offset, _-offset, set_block(_, block, replacement, flags, {})))
    );

    add_to_history('action_prism_polygon',player())
);

prism_star(pos, args, flags) -> (
    [block, outer_radius, inner_radius, height, n_points, axis, rotation, replacement] = args;

    center = map(pos, floor(_));
    flags = _parse_flags(flags);

    // get points in inner and pouter radius + interlace them
    inner_points = _get_circle_points(inner_radius, n_points, phase);
    outer_points = _get_circle_points(outer_radius, n_points, phase + 360/n_points/2);
    interlaced_list = _interlace_lists(inner_points, outer_points);
    interlaced_list += inner_points:0; // add first point at the end to close curve

    // get points and draw the connecting lines
    perimeter = _connect_with_lines(interlaced_list, center, axis);
    interior = _flood_fill_shape(perimeter, center, axis);

    offset = _get_prism_offset(height, axis);
    if(flags~'h',
        for(perimeter, volume(_+offset, _-offset, set_block(_, block, replacement, flags, {}) ));
        for(interior,
            set_block(_+offset, block, replacement, flags, {});
            set_block(_-offset, block, replacement, flags, {});
        ),
        for(interior, volume(_+offset, _-offset, set_block(_, block, replacement, flags, {})))
    );

    add_to_history('action_prism_star',player())
);

_get_circle_points(R, n, phase) -> (
    // retunrs <n> equidistant points on a circle
    angle_step = 360/n;
    get_step(i, outer(R), outer(angle_step), outer(phase)) -> R * [ cos(i*angle_step + phase), sin(i*angle_step + phase)];
    map(range(n), get_step(_) );
);

_connect_with_lines(points, center, axis) -> (
    // takes in a list of points in 2D and an axis, projects them to 3D and connects them with lines
    // returns the lines as a continous list of pints, the perimeter
    if(
        axis=='x', _2D_to_3D(u, v, w) -> [w, u, v],
        axis=='y', _2D_to_3D(u, v, w) -> [u, w, v],
        axis=='z', _2D_to_3D(u, v, w) -> [u, v, w],
    );

    perimeter = [];
    loop(length(points)-1,
        p1 = center + _2D_to_3D(points:_:0, points:_:1, 0) ;
        p2 = center + _2D_to_3D(points:(_+1):0, points:(_+1):1, 0) ;
        _draw_line(p1, p2, perimeter);
    );
    perimeter
);

_draw_line(p1, p2, perimeter) -> (
    m = p2-p1;
    max_size = max(map(m, abs(_)));
    t = l(range(max_size))/max_size;
    for(t,
        b = m * _ + p1;
        perimeter += map(b, floor(_));
    );
);

_interlace_lists(l1, l2) -> (
    // asumes l1 and l2 of same length
    out = [];
    for(l1,
        out:length(out) = _;
        out:length(out) =  l2:_i;
    );
    return(out);
);

_get_prism_offset(height, axis) -> (
    // returns offset half column given axis
    halfheight = (height-1)/2;
    if(
        axis=='x', [halfheight, 0, 0],
        axis=='y', [0, halfheight, 0],
        axis=='z', [0, 0, halfheight],
    )
);

_flood_fill_shape(perimeter, center, axis) ->(
    // returns blocks corresponding to the interior of the shape defined by <perimeter>
    // should work in 3D too giving a close surphase and passing null as <axis>
    if(
        axis==null, flood_neighbours(block) -> map(neighbours(block), pos(_)),
        axis=='x', flood_neighbours(block) -> [pos_offset(block, 'north'), pos_offset(block, 'south'), pos_offset(block, 'up'), pos_offset(block, 'down')],
        axis=='y', flood_neighbours(block) -> [pos_offset(block, 'north'), pos_offset(block, 'south'), pos_offset(block, 'east'), pos_offset(block, 'west')],
        axis=='z', flood_neighbours(block) -> [pos_offset(block, 'east'), pos_offset(block, 'west'), pos_offset(block, 'up'), pos_offset(block, 'down')]
    );


    interior = {center->null};
    map(perimeter,interior:_ = null);
    queue = [center];

    while(length(queue)>0, 10000,

        current_pos = queue:0;
        delete(queue, 0);

        for(flood_neighbours(current_pos),
            current_neighbour = _;
            // check neighbours, add the non visited ones to the visited set
            if(!has(interior, current_neighbour),
                interior:current_neighbour = null;
                queue:length(queue) = current_neighbour;
            );
        );
    );
    interior;
);

feature(pos, args, flags) -> (
    [what] = args;
    plop(pos, what)
);


// Some algebra stuff needed for the spray paint
_direction(yaw, pitch) -> [-sin(yaw)*cos(pitch), -sin(pitch), cos(pitch)*cos(yaw)];
_normalize(vec) -> vec / sqrt(reduce(vec, _a + _*_, 0));
_dot_prod(v, w) -> reduce(v*w, _a + _, 0);
_cross_prod(v, w) -> [v:1*w:2 - v:2*w:1, v:2*w:0 - v:0*w:2, v:0*w:1 - v:1*w:0];
_outer_prod(v, w) -> map(w, v*_);
_corss_matrix(vec) -> (
	[x, y, z] = vec;
	[[0, -z, y], [z, 0, -x], [-y, x, 0]]
);


spray(pos, args, flags)->(
	
	// because for some reason, the event is called twice
	if(global_sprayed_tick == tick_time(), return());
	global_sprayed_tick = tick_time();


	[block, size, count, replacement] = args;

	player = player();
	if(block==null, block=query(player, 'holds', 'offhand'):0);

	// get player looking direction
	yaw = player~'yaw';
	pitch = player~'pitch';
	dir = _direction(yaw, pitch);

	// set up stuff to rotat the random points
	id3 = [[1, 0, 0], [0, 1, 0], [0, 0, 1]];
	u = _normalize(_cross_prod([0, 0, 1], dir));
	angle = acos(_dot_prod(dir, [0, 0, 1]));
	rot_matrix = cos(angle) * id3 + sin(angle) * _corss_matrix(u) + (1-cos(angle)) * _outer_prod(u, u);

	loop(count,
		
		// get a random point in the spherical cap around [0, 0, 1]
		z = rand(1-cos(size)) + cos(size);
		phi = rand(360);
		x = sqrt(1-z*z) * cos(phi);
		y = sqrt(1-z*z) * sin(phi);
		random_dir = [x, y, z];

		// rotate the point to be around the looking direction
		rotated_dir_matrix = rot_matrix*[random_dir,random_dir,random_dir];//cos matrix multiplication dont work in scarpet, yet...
        rotated_dir = map(rotated_dir_matrix, reduce(_, _a+_, 0));

        // set start and end points and slope of the ray trace
		start = player~'pos' + [0, player~'eye_height', 0];
		end = rotated_dir*global_brush_reach + start;

		slope = end-start;
	    max_size = max(map(slope, abs(_)));
	    slope = slope / max_size;

	    // ray trice until it finds a non air block
		scanned_block = start;
		while(air(scanned_block),global_brush_reach,
			scanned_block = slope * _ + start;
		);
		set_block(scanned_block, block, replacement, flags, {})

	);
	add_to_history('action_spray', player);
);


//Command processing functions

global_water_greenery = {'seagrass', 'tall_seagrass', 'kelp_plant', 'kelp'};
global_air_greenery = {'grass', 'tall_grass', 'fern', 'large_fern'};

set_block(pos, block, replacement, flags, extra)->(//use this function to set blocks

        if( !( 
            (flags~'a' && block=='air' ) || 
            ((flags~'a' &&flags~'g') &&has(global_air_greenery, str(block))) 
        ),
        success=null;
        existing = block(pos);

        // undo expects positions, not blocks
        if(type(pos)!='list', pos=pos(pos));

        state = if(flags~'s',
            bs_e=block_state(existing);
            bs_b=block_state(block);
            if(all(keys(bs_e), has(bs_b, _)),
                bs_e, {}
            );
        , {});
        if(flags~'d',
            if(
                block=='water' || (flags~'g' &&has(global_water_greenery, str(block))) , block='air',
                block_state(block, 'waterlogged')!=null, put(state, 'waterlogged','false')
            );
        );
        if(flags~'w' && (
            (existing == 'water' && block_state(existing, 'level')=='0') ||
            block_state(existing, 'waterlogged')=='true'
            ),
            if(
                block=='air', block='water', // "waterlog" air blocks
                block_state(block, 'waterlogged')!=null, put(state, 'waterlogged','true')
            );
        );
        if(flags~'g',
            if(replacement:0=='water' && has(global_water_greenery,s=str(existing)), replacement=[s, null, [], false]);
            if(replacement:0=='air' && has(global_air_greenery,s=str(existing)), replacement=[s, null, [], false]);
        );

        if(block != existing && (!replacement || _block_matches(existing, replacement)) && (!flags~'p' || air(pos)),
            postblock=if(flags~'u',without_updates(set(existing,block,state)),set(existing,block,state));
            prev_biome=biome(pos);
            if(flag~'b'&&extra:'biome',set_biome(pos,extra:'biome'));
            success=existing;
            global_affected_blocks+=[pos,existing,{'biome'->prev_biome}];
        );
        bool(success), //cos undo uses this
        false
    )

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
        _print(player, 'entry_undo_1', (history~command)+1, _translate(command:'type'));//printing twice so it goes on 2 separate lines
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

structure(name, action, args)->(
    p=player();
    if(action=='save',
        if(read_file('structures/'+name,'nbt'),
            if(args:'force',
                _print(p,'structure_overwrite',name);
                delete_file('structures/'+name,'nbt'),
                _error(p,'existing_structure',name)
            )
        );

        [pos1,pos2]=_get_current_selection(player);
        data={};
        blocks=volume_blocks(pos1,pos2);

        pos_diff=map(pos1-pos2,abs(_)+1);

        //mc nbt data structure (cos schematic have weird format which is unreplicable in scarpet)
        min_pos=map(pos1,min(_,pos2:_i));
        avg_pos=(pos1+pos2)/2;
        entities=if(args:'include_entities',entity_area('*',avg_pos,map(avg_pos-min_pos,abs(_))),[]);

        palette_map={};

        states=[];
        key_map(block)->(
            map={'Name'->if((str(block)-'minecraft:')~':',str(block),str('minecraft:%s',block))};
            if(block_state(block),put(map,'Properties',block_state(block)));
            map
        );

        for(blocks,
            palette_map:key_map(_)+=1;

            state_map={
                'pos'->(pos(_)-min_pos),
                'state'->(keys(palette_map)~key_map(_))
            };
            if((nbt=parse_nbt(block_data(_)))!='null',
                delete(nbt,'x');
                delete(nbt,'y');
                delete(nbt,'z');
                put(state_map,'nbt',nbt)
            );
            states+=state_map
        );

        data={
            'blocks'->states,
            'entities'->entities,
            'palette'->keys(palette_map),
            'size'->{
                'x'->pos_diff:0,
                'y'->pos_diff:1,
                'z'->pos_diff:2
            },
            'DataVersion'->system_info('game_data_version')
        };

        write_file('structures/'+name,'nbt',encode_nbt(data));
        _print(p,'saved_structure',name),

        action=='save_clipboard',

        if(read_file('structures/'+name,'nbt'),
            if(args:'force',
                _print(p,'structure_overwrite',name);
                delete_file('structures/'+name,'nbt'),
                _error(p,'existing_structure',name)
            )
        );

        if(!global_clipboard,_error(player, 'paste_no_clipboard', player));

        clipboard=global_clipboard;
        delete(clipboard,0);//cos first arg is entities

        entities=global_clipboard:0;

        pos1=clipboard:0:0;
        pos2=get(clipboard,-1):0;


        pos_diff=map(pos1-pos2,abs(_)+1);

        min_pos=map(pos1,min(_,pos2:_i));
        avg_pos=(pos1+pos2)/2;

        entities=entity_area('*',avg_pos,map(avg_pos-min_pos,abs(_)));
        entities_list = [];

        entities_list = map(entities, {
            'nbt'->_~'nbt',
            'pos'->pos(_)-min_pos,
            'blockPos'->map(pos(_)-min_pos, round(_))
        });

        palette_map={};

        states=[];
        key_map(block)->(
            map={'Name'->if((str(block:0)-'minecraft:')~':',str(block:0),str('minecraft:%s',block:0))};
            if(block:2,put(map,'Properties',block:2));
            map
        );

        for(clipboard,
            palette_map:key_map(_)+=1;

            state_map={
                'pos'->(_:0-min_pos),
                'state'->(keys(palette_map)~key_map(_))
            };
            if((nbt=parse_nbt(_:3))!='null',
                delete(nbt,'x');
                delete(nbt,'y');
                delete(nbt,'z');
                put(state_map,'nbt',nbt)
            );
            states+=state_map
        );

        data={
            'blocks'->states,
            'entities'->entities_list,
            'palette'->keys(palette_map),
            'size'->{
                'x'->pos_diff:0,
                'y'->pos_diff:1,
                'z'->pos_diff:2
            },
            'DataVersion'->system_info('game_data_version')
        };

        write_file('structures/'+name,'nbt',encode_nbt(data));
        _print(p,'saved_structure',name),

        action=='copy',
        if(!(file=read_file('structures/'+name,'nbt')),
            _error(p,'structure_load_fail',name)
        );

        if(global_clipboard,
            if(args:'force',
                _print(player,'copy_force');
                global_clipboard=[],
                _error(player,'copy_clipboard_not_empty')
            )
        );

        file=parse_nbt(file);
        palette=file:'palette';
        blocks=file:'blocks';
        entities=file:'entities';

        global_clipboard+=entities;//todo in code cleanup for copy, not rly functional rn

        for(blocks,
            state=palette:(_:'state');
            global_clipboard+=[_:'pos',state:'Name',state,null]
        );

        _print(player,'copy_success',length(global_clipboard)-1,length(global_clipboard:0)),

        action=='load',
        if(!(file=read_file('structures/'+name,'nbt')),
           _error(p,'structure_load_fail',name)
        );

        flags=_parse_flags(args:'flags');

        pos=if(args:'pos',args:'pos',p~'pos');
        file=parse_nbt(file);
        palette=file:'palette';
        blocks=file:'blocks';
        entities=file:'entities';

        if(flags~'e',
            for(entities,
                nbt = parse_nbt(_:'nbt');
                spawn(nbt:'id', _:'pos' + pos, nbt)
            )
        );

        for(blocks,
            state=palette:(_:'state');
            set_block(_:'pos'+pos,state:'Name',null,flags,{'state'->state:'Properties','nbt'->_:'nbt'});
        );

        add_to_history('action_structure_paste',p),

        action=='delete',
        if(delete_file('structures/'+name,'nbt'),
            _print(p,'structure_delete_success',name),
            _error(p,'structure_delete_fail',name)
        ),

        action=='list',
        _print(p,'structure_list');
        strucs=list_files('structures','nbt');
        print(str(strucs)-'['-']')
    )
);

set_in_selection(block,replacement,flags)->
(
    player=player();
    [pos1,pos2]=_get_current_selection(player);
    volume(pos1,pos2,set_block(pos(_),block,replacement,flags,{}));
    add_to_history('action_set', player)
);

walls(block, sides, replacement, flags) -> (
    player = player();
    [pos1,pos2]=_get_current_selection(player);
    flags=_parse_flags(flags);

    _walls_generic(pos1, pos2, sides, block, replacement, flags);

    add_to_history('walls', player)
);

_walls_generic(min_corner, max_corner, sides, block, replacement, flags) -> (

    [ox, oy, oz] = max_corner-min_corner;

    if(sides~'z',
        volume(min_corner, min_corner+[ox, oy, 0], set_block(_, block, replacement, flags, {}));
        volume(max_corner, max_corner-[ox, oy, 0], set_block(_, block, replacement, flags, {}))
    );
    if(sides~'x',
        volume(min_corner, min_corner+[0, oy, oz], set_block(_, block, replacement, flags, {}));
        volume(max_corner, max_corner-[0, oy, oz], set_block(_, block, replacement, flags, {}))
    );
    if(sides~'y',
        volume(min_corner, min_corner+[ox, 0, oz], set_block(_, block, replacement, flags, {}));
        volume(max_corner, max_corner-[ox, 0, oz], set_block(_, block, replacement, flags, {}))
    )
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

    add_to_history('action_flood', player())
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

    add_to_history('action_rotate', player)
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
        modify(_,'remove')
    );

    add_to_history('action_move', player)
);

stack(count,direction,flags) -> (
    print([count, type(count)]);
    player=player();
    translation_vector = pos_offset([0,0,0],if(direction,direction,player~'facing'));
    [pos1,pos2]=_get_current_selection(player);
    flags = _parse_flags(flags);
    print(flags);

    translation_vector = translation_vector*map(pos1-pos2,abs(_)+1);

    loop(count,
        c = _;
        offset = translation_vector*(c+1);
        volume(pos1,pos2,
            pos = pos(_)+offset;
            set_block(pos,_,null,flags,{});
        );
    );

    add_to_history('action_stack', player);
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
        set_block(_,expand_map:_,null,null,{})
    );
    add_to_history('action_expand',player)
);

_copy(origin, force)->(
    player = player();
    if(!origin,origin=pos(player));
    [pos1,pos2]=_get_current_selection(player);
    if(global_clipboard,
        if(force,
            _print(player,'copy_force');
            global_clipboard=[],
            _error(player,'copy_clipboard_not_empty', player)
        )
    );

    min_pos=map(pos1,min(_,pos2:_i));
    avg_pos=(pos1+pos2)/2;

    entities = entity_area('*',avg_pos,map(avg_pos-min_pos,abs(_)));

    for(entities,//if its empty, this just wont run, no errors
        nbt=parse_nbt(_~'nbt');
        old_pos=pos(_);
        pos=old_pos-min_pos;
        delete(nbt,'Pos');//so that when creating new entity, it doesnt think it is in old location
        {'type'->_~'type','pos'->pos,'nbt'->nbt}
    );

    global_clipboard+entities;//always gonna have entities, incase u wanna paste with them

    volume(pos1,pos2,
        global_clipboard+=[centre-pos(_),block(_),block_state(_),block_data(_),biome(_)]//all the important stuff, can add more if the flags require it
    );

    _print(player,'copy_success',length(global_clipboard)-1,length(global_clipboard:0));
);

paste(pos, flags)->(
    player=player();
    if(!pos,pos=pos(player));
    if(!global_clipboard,_error(player, 'paste_no_clipboard', player));
    flags=_parse_flags(flags);

    entities=global_clipboard:0;

    if(flags~'e',
        for(entities,
            spawn(_:'type',_:'pos',_:'nbt')
        )
    );

    for(range(1,length(global_clipboard)-1),//cos gotta skip the entity one
        [pos_vector, old_block, old_states, old_nbt, old_biome]=global_clipboard:_;
        new_pos=pos+pos_vector;
        set_block(new_pos, old_block, null, flags, {'state'->old_states,'biome'->old_biome,'nbt'->old_nbt})
    );
    add_to_history('action_paste',player)
);
