package com.axperty.farmingexperiencecore.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

// Makes zombies be scared of any light source and run away from them.
// This also prevents them from attacking villagers easily.

public class ZombieAvoidLightGoal extends Goal {
    private final Zombie zombie;
    private final double speedModifier;
    private double targetX;
    private double targetY;
    private double targetZ;

    public ZombieAvoidLightGoal(Zombie zombie, double speedModifier) {
        this.zombie = zombie;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        Level level = this.zombie.level();
        BlockPos pos = this.zombie.blockPosition();

        if (level.getBrightness(LightLayer.BLOCK, pos) > 0) {
            Vec3 fleePos = getDarkPos(level, pos);
            if (fleePos != null) {
                this.targetX = fleePos.x;
                this.targetY = fleePos.y;
                this.targetZ = fleePos.z;

                this.zombie.setTarget(null);
                return true;
            }
        }

        var target = this.zombie.getTarget();
        if (target != null && level.getBrightness(LightLayer.BLOCK, target.blockPosition()) > 0) {
            this.zombie.setTarget(null);
        }
        
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        return !this.zombie.getNavigation().isDone() && this.zombie.level().getBrightness(LightLayer.BLOCK, this.zombie.blockPosition()) > 0;
    }

    @Override
    public void start() {
        this.zombie.getNavigation().moveTo(this.targetX, this.targetY, this.targetZ, this.speedModifier);
    }
    
    private Vec3 getDarkPos(Level level, BlockPos startPos) {
        for (int i = 0; i < 10; i++) {
            Vec3 pos = DefaultRandomPos.getPos(this.zombie, 10, 7);
            if (pos != null) {
                BlockPos bPos = BlockPos.containing(pos);
                if (level.getBrightness(LightLayer.BLOCK, bPos) == 0) {
                    return pos;
                }
            }
        }
        return null;
    }
}
