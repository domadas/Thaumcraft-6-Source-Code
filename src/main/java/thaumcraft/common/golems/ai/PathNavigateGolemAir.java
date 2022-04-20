// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.ai;

import net.minecraft.util.math.RayTraceResult;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.world.World;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigate;

public class PathNavigateGolemAir extends PathNavigate
{
    public PathNavigateGolemAir(final EntityLiving p_i45873_1_, final World worldIn) {
        super(p_i45873_1_, worldIn);
    }
    
    protected PathFinder getPathFinder() {
        return new PathFinder(new FlightNodeProcessor());
    }
    
    protected boolean canNavigate() {
        return true;
    }
    
    protected Vec3d getEntityPosition() {
        return new Vec3d(this.entity.posX, this.entity.posY + this.entity.height * 0.5, this.entity.posZ);
    }
    
    protected void pathFollow() {
        final Vec3d vec3 = this.getEntityPosition();
        final float f = this.entity.width * this.entity.width;
        final byte b0 = 6;
        if (vec3.squareDistanceTo(this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex())) < f) {
            this.currentPath.incrementPathIndex();
        }
        for (int i = Math.min(this.currentPath.getCurrentPathIndex() + b0, this.currentPath.getCurrentPathLength() - 1); i > this.currentPath.getCurrentPathIndex(); --i) {
            final Vec3d vec4 = this.currentPath.getVectorFromIndex(this.entity, i);
            if (vec4.squareDistanceTo(vec3) <= 36.0 && this.isDirectPathBetweenPoints(vec3, vec4, 0, 0, 0)) {
                this.currentPath.setCurrentPathIndex(i);
                break;
            }
        }
        this.checkForStuck(vec3);
    }
    
    protected void removeSunnyPath() {
        super.removeSunnyPath();
    }
    
    protected boolean isDirectPathBetweenPoints(final Vec3d p_75493_1_, final Vec3d p_75493_2_, final int p_75493_3_, final int p_75493_4_, final int p_75493_5_) {
        final RayTraceResult RayTraceResult = this.world.rayTraceBlocks(p_75493_1_, new Vec3d(p_75493_2_.x, p_75493_2_.y + this.entity.height * 0.5, p_75493_2_.z), false, true, false);
        return RayTraceResult == null || RayTraceResult.typeOfHit == net.minecraft.util.math.RayTraceResult.Type.MISS;
    }
}
