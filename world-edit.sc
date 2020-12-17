//World edit

__config()->{
    'stay_loaded'->true,
    'scope'->'global'
};
//Global Variables

global_player_data={};

//Events

__on_player_breaks_block(player,block)->(
    if(player~'holds'==global_player_data:player:'wand',
        global_player_data:player:'positions':0=pos(block);
        without_updates(set(pos(block),block));
        print('Set first position to '+pos(block))
    )
);

__on_player_right_clicks_block(player, item_tuple, hand, block, face, hitvec)->(
    if(item_tuple:0==global_player_data:player:'wand',
        global_player_data:player:'positions':1=pos(block);
        print('Set second position to '+pos(block))
    )
);

//Command functions

create_player_data(player)->(
    global_player_data:player={
        'wand'->'wooden_sword',
        'history'->[],
        'positions'->[]
    }
);
