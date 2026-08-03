execute at @s run execute if dimension minecraft:overworld run execute if entity @s[type=#farmingexperiencecore:cave_mobs] if predicate farmingexperiencecore:sky_spawn run tag @s add SpawnForbidden
execute as @s[tag=SpawnForbidden] run tp @s ~ ~-1000 ~
