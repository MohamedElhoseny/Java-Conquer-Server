package net.co.java.skill;

import net.co.java.entity.Entity;
import net.co.java.packets.InteractPacket;
import net.co.java.server.GameServerClient;

public abstract class Skill {
	
	/*
	 * TODO
	 * - Check if the correct weapon is equipped
	 * - Implement in attack
	 */
	
	public static final MagicSkill FAST_BLADE = new FastBlade();
	public static final MagicSkill SCENT_SWORD = new FastBlade.ScentSword();
	public static final MagicSkill HERCULES = new Hercules();
	public static final MagicSkill BLESS = new Bless(); 
	public static final MagicSkill CYCLONE = new Cyclone();
	public static final PassiveSkill PHOENIX = new Phoenix();
	public static final PassiveSkill WIDE_STRIKE = new WideStrike();
	public static final PassiveSkill BOREAS = new WideStrike.Boreas();
	public static final PassiveSkill SNOW = new Snow();
	public static final PassiveSkill STRANDED_MONSTER = new StandedMonster();
	public static final PassiveSkill SPEED_GUN = new SpeedGun();
	public static final PassiveSkill PENETRATION = new Penetration();
	public static final PassiveSkill BOOM = new Boom();
	public static final PassiveSkill SEIZER = new Seizer();
	public static final PassiveSkill EARTHQUAKE = new Earthquake();
	public static final PassiveSkill RAGE = new Rage();
	public static final PassiveSkill CELESTIAL = new Celestial();
	public static final PassiveSkill ROAMER = new Roamer();
	public static final PassiveSkill HALT = new Halt();
	
	
	/**
	 * Get a {@code Skill} by SkillID
	 * @param skillID
	 * @return the Skill
	 */
	public static Skill valueOf(int skillID) {
		switch(skillID) {
		case 1045: return FAST_BLADE;
		case 1046: return SCENT_SWORD;
		case 1115: return HERCULES;
		case 1250: return WIDE_STRIKE;
		case 1260: return SPEED_GUN;
		case 1290: return PENETRATION;
		case 5010: return SNOW;
		case 5020: return STRANDED_MONSTER;
		case 5030: return PHOENIX;
		case 5040: return BOOM;
		case 5050: return BOREAS;
		case 7000: return SEIZER;
		case 7010: return EARTHQUAKE;
		case 7020: return RAGE;
		case 7030: return CELESTIAL;
		case 7040: return ROAMER;
		case 1110: return CYCLONE;
		case 9876: return BLESS;
		}
		return null;
	}
	
	public static PassiveSkill passiveSkillOf(WeaponType weaponType){
		if(weaponType == null) return null;
		switch(weaponType)
		{
			case SWORD:	return PHOENIX; 
			case BACKSWORD: return PHOENIX;
			case GLAIVE: return WIDE_STRIKE;
			case POLEAXE: return BOREAS;
			case WAND: return SNOW; 
			case HALBERT: return STRANDED_MONSTER;
			case SPEAR: return SPEED_GUN;
			case DAGGER: return PENETRATION;
			case HAMMER: return BOOM;
			case LONGHAMMER: return HALT;
			case HOOK: return SEIZER;
			case AXE: return EARTHQUAKE;
			case CLUB: return RAGE;
			case SCEPTER: return CELESTIAL;
			case WHIP: return ROAMER;
			default: return null;
		}
	}
	
	@Override
	public String toString() {
		return getSkillID() + "_" + this.getClass().getSimpleName();
	}
	
	/**
	 * @return the SkillID for this Skill
	 */
	public abstract int getSkillID();
	
	/**
	 * @param level
	 * @return the target exp for a given level
	 */
	public abstract int getTargetExp(int level);
	
	/**
	 * @param level
	 * @return at which level the skill can be improved
	 */
	public abstract int levelUpLevel(int level);
	
	/**
	 * @return max level for this skill
	 */
	public abstract int maxLevel();
	
	/**
	 * These skills below are active: You have to select them and use them manually.
	 * @author Jan-Willem Gmelig Meyling
	 *
	 */
	public static abstract class MagicSkill extends Skill {
		
		/**
		 * @param level
		 * @return get the damage multiplier for this Skill
		 */
		public abstract double damageMutiplier(int level);
		
		/**
		 * @param level
		 * @return the range for this skill
		 */
		public abstract int range(int level);
		
		/**
		 * @param level
		 * @return the distance for this skill
		 */
		public abstract int distance(int level);
		
		public abstract void handle(GameServerClient player, InteractPacket ip);
		
	}
	
	/**
	 * The skills below are passive: They have a chance to work automatically when you attack.
	 * @author Jan-Willem Gmelig Meyling
	 *
	 */
	public static abstract class PassiveSkill extends Skill {
		
		protected Entity target; 
		
		public void setTarget(Entity target)
		{
			this.target = target;
		}
		
		/**
		 * @param level
		 * @return the chance for this skill to occur
		 */
		public abstract double chance(int level);
		
		/**
		 * @param level
		 * @return get the damage multiplier for this Skill
		 */
		public abstract double damageMutiplier(int level);
		
		/**
		 * @param level
		 * @return the range for this skill
		 */
		public abstract int range(int level);
		
		/**
		 * @param level
		 * @return the distance for this skill
		 */
		public abstract int distance(int level);
		
		/**
		 * @return the WeaponType required for this Skill
		 */
		public abstract WeaponType getWeaponType();
		
		/**
		 * @return the entities taking damage from the hit. 
		 */
		public abstract TargetBuilder getHittedEntities(GameServerClient client, int level);
	}
	
	/**
	 * The AbstractPassiveSkill class provides some default implementations for Passive Skill
	 * @author Jan-Willem Gmelig Meyling
	 *
	 */
	static abstract class AbstractPassiveSkill extends PassiveSkill {
		
		private final static int[] exps = { 20243, 37056, 66011, 116140, 192800, 418030, 454350, 491200, 520030 };
		
		@Override
		public int getTargetExp(int level) {
			if(level<exps.length){
				return exps[level];
			}
			return 0;
		}
		
		@Override
		public int maxLevel() {
			return exps.length;
		}
		
		@Override
		public int levelUpLevel(int level) {
			return 10 * level + 30;
		}
		
		@Override
		public int distance(int level) {
			return 9;
		}				
	}
}
