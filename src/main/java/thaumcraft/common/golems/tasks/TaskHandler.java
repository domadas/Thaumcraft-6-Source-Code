// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.golems.tasks;

import net.minecraft.world.World;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import java.util.ArrayList;
import net.minecraft.entity.Entity;
import java.util.UUID;
import java.util.Iterator;
import thaumcraft.api.golems.tasks.Task;
import java.util.concurrent.ConcurrentHashMap;

public class TaskHandler
{
    static final int TASK_LIMIT = 10000;
    public static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Task>> tasks;
    
    public static void addTask(final int dim, final Task ticket) {
        if (!TaskHandler.tasks.containsKey(dim)) {
            TaskHandler.tasks.put(dim, new ConcurrentHashMap<Integer, Task>());
        }
        final ConcurrentHashMap<Integer, Task> dc = TaskHandler.tasks.get(dim);
        if (dc.size() > 10000) {
            try {
                final Iterator<Task> i = dc.values().iterator();
                if (i.hasNext()) {
                    i.next();
                    i.remove();
                }
            }
            catch (final Exception ex) {}
        }
        dc.put(ticket.getId(), ticket);
    }
    
    public static Task getTask(final int dim, final int id) {
        return getTasks(dim).get(id);
    }
    
    public static ConcurrentHashMap<Integer, Task> getTasks(final int dim) {
        if (!TaskHandler.tasks.containsKey(dim)) {
            TaskHandler.tasks.put(dim, new ConcurrentHashMap<Integer, Task>());
        }
        return TaskHandler.tasks.get(dim);
    }
    
    public static ArrayList<Task> getBlockTasksSorted(final int dim, final UUID uuid, final Entity golem) {
        final ConcurrentHashMap<Integer, Task> tickets = getTasks(dim);
        final ArrayList<Task> out = new ArrayList<Task>();
    Label_0025:
        for (final Task ticket : tickets.values()) {
            if (!ticket.isReserved()) {
                if (ticket.getType() != 0) {
                    continue;
                }
                if (uuid != null && ticket.getGolemUUID() != null && !uuid.equals(ticket.getGolemUUID())) {
                    continue;
                }
                if (out.size() == 0) {
                    out.add(ticket);
                }
                else {
                    double d = ticket.getPos().distanceSqToCenter(golem.posX, golem.posY, golem.posZ);
                    d -= ticket.getPriority() * 256;
                    for (int a = 0; a < out.size(); ++a) {
                        double d2 = out.get(a).getPos().distanceSqToCenter(golem.posX, golem.posY, golem.posZ);
                        d2 -= out.get(a).getPriority() * 256;
                        if (d < d2) {
                            out.add(a, ticket);
                            continue Label_0025;
                        }
                    }
                    out.add(ticket);
                }
            }
        }
        return out;
    }
    
    public static ArrayList<Task> getEntityTasksSorted(final int dim, final UUID uuid, final Entity golem) {
        final ConcurrentHashMap<Integer, Task> tickets = getTasks(dim);
        final ArrayList<Task> out = new ArrayList<Task>();
    Label_0025:
        for (final Task ticket : tickets.values()) {
            if (!ticket.isReserved()) {
                if (ticket.getType() != 1) {
                    continue;
                }
                if (uuid != null && ticket.getGolemUUID() != null && !uuid.equals(ticket.getGolemUUID())) {
                    continue;
                }
                if (ticket.getEntity() == null || ticket.getEntity().isDead) {
                    ticket.setSuspended(true);
                }
                else if (out.size() == 0) {
                    out.add(ticket);
                }
                else {
                    double d = ticket.getPos().distanceSqToCenter(golem.posX, golem.posY, golem.posZ);
                    d -= ticket.getPriority() * 256;
                    for (int a = 0; a < out.size(); ++a) {
                        double d2 = out.get(a).getPos().distanceSqToCenter(golem.posX, golem.posY, golem.posZ);
                        d2 -= out.get(a).getPriority() * 256;
                        if (d < d2) {
                            out.add(a, ticket);
                            continue Label_0025;
                        }
                    }
                    out.add(ticket);
                }
            }
        }
        return out;
    }
    
    public static void completeTask(final Task task, final EntityThaumcraftGolem golem) {
        if (task.isCompleted() || task.isSuspended()) {
            return;
        }
        final ISealEntity se = SealHandler.getSealEntity(golem.world.provider.getDimension(), task.getSealPos());
        if (se != null) {
            task.setCompletion(se.getSeal().onTaskCompletion(golem.world, golem, task));
        }
        else {
            task.setCompletion(true);
        }
    }
    
    public static void clearSuspendedOrExpiredTasks(final World world) {
        final ConcurrentHashMap<Integer, Task> tickets = getTasks(world.provider.getDimension());
        final ConcurrentHashMap<Integer, Task> temp = new ConcurrentHashMap<Integer, Task>();
        for (final Task ticket : tickets.values()) {
            if (!ticket.isSuspended() && ticket.getLifespan() > 0L) {
                ticket.setLifespan((short)(ticket.getLifespan() - 1L));
                temp.put(ticket.getId(), ticket);
            }
            else {
                final ISealEntity sEnt = SealHandler.getSealEntity(world.provider.getDimension(), ticket.getSealPos());
                if (sEnt == null) {
                    continue;
                }
                sEnt.getSeal().onTaskSuspension(world, ticket);
            }
        }
        TaskHandler.tasks.put(world.provider.getDimension(), temp);
    }
    
    static {
        TaskHandler.tasks = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Task>>();
    }
}
