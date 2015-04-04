package net.co.java.entity.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.co.java.entity.Entity;
import net.co.java.entity.Location;
import net.co.java.entity.Player;
import net.co.java.packets.GeneralData;
import net.co.java.packets.SpawnPacket;
import net.co.java.packets.GeneralData.SubType;

/**
 * A basic implementation for {@code View}
 * @author Jan-Willem Gmelig Meyling
 */
public class ViewImpl implements View {
	
	private final Entity me;
	private final Set<Entity> entities;
	
	public ViewImpl(Entity entity) {
		this.me = entity;
		this.entities = new HashSet<>();
	}

	@Override
	public void add(Entity entity) {
		boolean added;
		synchronized(entities) {
			added = entities.add(entity);
		}
		if(added) {
			me.notify(SpawnPacket.create(entity));
			entity.view.add(me);
		}
	}
	
	@Override
	public boolean contains(Entity entity) {
		synchronized(entities) {
			return entities.contains(entity);
		}
	}

	@Override
	public void remove(Entity entity) {
		boolean removed;
		synchronized(entities) {
			removed = entities.remove(entity);
		}
		if(removed) {
			me.notify(new GeneralData(SubType.ENTITY_REMOVE, entity).build());
			entity.view.remove(me);
		}
	}

	@Override
	public int size() {
		synchronized(entities) {
			return entities.size();
		}
	}

	@Override
	public List<Entity> getEntities() {
		synchronized(entities) {
			return new ArrayList<>(entities);
		}
	}

	@Override
	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<>();
		for(Entity e : getEntities()) {
			if(e instanceof Player) {
				players.add((Player) e);
			}
		}
		return players;
	}

	@Override
	public void update(Location location) {
		List<Entity> allEntities = location.getMap().getEntities();
		for(Entity e : allEntities) {
			if(e.inView(me)) {
				add(e);
			} else {
				remove(e);
			}
		}
	}

}
