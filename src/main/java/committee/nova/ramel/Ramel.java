package committee.nova.ramel;

import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Camel;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.List;

public final class Ramel extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getScheduler().scheduleSyncRepeatingTask(this, tickCamel(getServer()), 1L, 1L);
        getLogger().info("Ramel initialized!");
    }

    public static Runnable tickCamel(Server server) {
        return () -> {
            final List<World> worlds = server.getWorlds();
            for (final World world : worlds) {
                final Collection<Camel> entities = world.getEntitiesByClass(Camel.class);
                for (final Camel camel : entities) {
                    if (!world.isChunkLoaded(camel.getLocation().getChunk())) continue;
                    if (!camel.isDashing()) continue;
                    world.getNearbyEntities(camel.getBoundingBox().expand(.5)).stream()
                            .filter(e -> e instanceof LivingEntity && e.isValid() && !camel.equals(e) && !camel.getPassengers().contains(e))
                            .forEach(e -> {
                                final LivingEntity l = (LivingEntity) e;
                                world.playSound(l, Sound.ENTITY_PLAYER_ATTACK_KNOCKBACK, 1.0F, 1.0F);
                                l.damage(5.0, camel);
                                l.setVelocity(l.getVelocity().add(camel.getVelocity().multiply(3.0)));
                            });
                }
            }
        };
    }
}
