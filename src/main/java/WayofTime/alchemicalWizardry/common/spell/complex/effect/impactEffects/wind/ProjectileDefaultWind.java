package WayofTime.alchemicalWizardry.common.spell.complex.effect.impactEffects.wind;

import WayofTime.alchemicalWizardry.api.spell.ProjectileImpactEffect;
import net.minecraft.entity.Entity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class ProjectileDefaultWind extends ProjectileImpactEffect
{
    public ProjectileDefaultWind(int power, int potency, int cost)
    {
        super(power, potency, cost);
    }

    @Override
    public void onEntityImpact(Entity mop, Entity proj)
    {
        float wantedYVel = (float) ((0.5) * (0.5 * this.potencyUpgrades + 1));

        mop.motionX = proj.motionX;
        mop.motionY = mop.motionY += wantedYVel;
        mop.motionZ = proj.motionZ;
    }

    @Override
    public void onTileImpact(World world, MovingObjectPosition mop)
    {
        return;
    }
}
