gamemode spectator @a
execute as @a run attribute @s air_drag_modifier base set 0.2

tellraw @a {text:"--- Lithostitched Debug ---",color:"#BBFFBB"}
function test:line {name:"add_biome_spawns",desc:"Armadillos in all biomes"}
function test:line {name:"add_features",desc:"Crimson roots in all biomes"}
function test:line {name:"set_biome_attributes",desc:"Pale garden fog"}
function test:line {name:"wrap_noise_router",desc:"Sky islands"}