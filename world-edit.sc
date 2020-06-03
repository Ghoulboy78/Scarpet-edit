//World edit

__command()->help();
__config()->m(l('stay_loaded','true'),l('scope','global'));
//Global Variables

global_player_data=m();

global_wand='wooden_axe';

//Events

__on_player_breaks_block(player,block)->(
    if(player~'holds'==global_wand,
        global_player_data:player:'positions':0=pos(block);
        without_updates(set(pos(block),block));
        print('Set first position to '+pos(block))
    )
);

__on_player_right_clicks_block(player, item_tuple, hand, block, face, hitvec)->(
    if(item_tuple:0==global_wand,
        global_player_data:player:'positions':1=pos(block);
        print('Set second position to '+pos(block))
    )
);

//Command functions

help()->print(
        'Welcome to World Edit by Ghoulboy and Firigion \'n others!\n'+
        'Commands:\n'+
        '/world-edit help: Displays this help page\n'+
        '/world-edit set_block block replace/keep: Will set the first position to \'block\'.\n'+
        'TEMP: As of now, you can\'t enter block properties.\n'+//Todo add block properties
        '/world-edit fill_blocks block replace/keep: Will fill in the blocks with \'block\'.\n'+
        '/world-edit pos1: Sets first position to your current coordinates\n'+
        '/world-edit pos2: Sets second position to your current coordinates\n'+
        'TEMP: As of now, you can\'t enter block properties.\n'+
        '/world-edit replace_keep \'replace\'/\'keep\': If it\'s \'replace\', the replace/keep option in the commands will replace all blocks of that type\n'+
        'If it\'s \'keep\', it will replace all blocks other than that one.\n'+
        '/world-edit undo num: Undoes \'num\' number of moves.\n'+//Todo add ability to undo other player moves for operators
        '/world-edit clear_history: Clears all your undoable history'
    );

set_block(block,replace)->(
    if(global_positions:player():'positions':0,pos=global_player_data:player():'positions':0,return(print('No position defined')))
    global_player_data:player():'history':length(global_player_data:player():'history')=l(l(pos(block),block));//For the Undo function
    success=_set_block(pos,block,replace);
    if(success,print('Successfully set 1 block'),print('Unable to set block, replacement block found'))
);

fill_blocks(block,replace)->(
    if(global_player_data:player():'positions':0,l(x1,y1,z1)=global_player_data:player():'positions':0,return(print('No position 1 selected')));
    if(global_player_data:player():'positions':1,l(x2,y2,z2)=global_player_data:player():'positions':1,return(print('No position 2 selected')));
    success=0;
    history=l();
    volume(x1,y1,z1,x2,y2,z2,
        success+=_set_block(pos(_),block,replace)
    );
    global_player_data:player():'history':length(global_player_data:player():'history')=history;//For the Undo function
    print(str('Successfully filled %d out of %d blocks',success,_vol(global_player_data:player():'positions':0,global_player_data:player():'positions':1)))
);

pos1()->(
    player=player();
    pos=pos(block(pos(player)));
    global_player_data:player:'positions':0=pos;
    print('Set first position to '+pos)
);

pos2()->(
    player=player();
    pos(block(pos(player)));
    global_player_data:player:'positions':1=pos;
    print('Set second position to '+pos)
);

replace_keep(type)->global_player_data:player():'replace_keep'=type;

undo(num)->(
    if(num<1,return(print('You cannot undo less than one move!')));
    if(num>length(global_player_data:player():'history')-1,//Makes sure num is not 0
        print('You are trying to undo too many moves, there are only '+(length(global_player_data:player():'history')-1)+' moves available to undo!');
        return()
    );
    for(range(1,num+1),//So I don't have to add one at each step
        for(global_player_data:player():'history':length(global_player_data:player():'history')-num,
            l(pos,block)=_;
            set(pos,block)
        )
        delete(global_player_data:player():'history':length(global_player_data:player():'history')-num)
    );
    print(str('Successfully undid %d operations',num))
)

//Other functions
_vol(pos1,pos2)->return(abs(pos1:0-pos2:0)*abs(pos1:1-pos2:1)*abs(pos1:2-pos2:2))


_set_block(pos,block,replace)->( //So I can have a separate command called set_block
    if((block(pos)==replace&&global_player_data:player():'replace_keep'=='replace')||(block(pos)!=replace&&global_player_data:player():'replace_keep'=='keep'),
        set(pos,block);
        return(1);
    );
    return(0)
);
