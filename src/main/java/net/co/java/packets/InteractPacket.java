package net.co.java.packets;

import net.co.java.server.GameServerClient;
import net.co.java.skill.PhysicalAttack;
import net.co.java.skill.Skill;
import net.co.java.skill.Skill.MagicSkill;

/**
 * The Interact packet is most commonly used for direct melee/archer attacks,
 * but also used for certain player to player actions, such as marriage.
 * 
 * @author Jan-Willem Gmelig Meyling
 * @author Thomas Gmelig Meyling
 */
public class InteractPacket implements PacketHandler {
	private IncomingPacket ip; 
	
	private final long timestamp;
	private final long identity;
	private final long target;
	private final int x;
	private final int y;
	private final Mode mode;
	private Skill skill;
	
	/**
	 * Construct a new {@code InteractPacket} based on a {@code IncomingPacket}
	 * @param ip
	 */
	public InteractPacket(IncomingPacket ip) {
		this.ip = ip ;
		
		this.timestamp = ip.readUnsignedInt(4);
		this.identity = ip.readUnsignedInt(8);
		this.mode = Mode.valueOf(ip.readUnsignedByte(20));
		
		int skillid = ip.readUnsignedShort(24);
		skillid ^= 0x915d;
		skillid ^= identity & 0xFFFF;
		skillid = (skillid << 0x3 | skillid >> 0xd ) & 0xFFFF;
		skillid -= 0xeb42;
		
		this.skill = Skill.valueOf(skillid);
		
		// something seems to go wrong with high skill ids e.g. bless = 9876, that makes me have to do this weird conversion.
		if(this.skill == null)
		{
			this.skill = Skill.valueOf(skillid + (1 << 16));
		}
		
		long x = ip.readUnsignedShort(16);
		x = x ^ ( identity & 0xFFFF ) ^ 0x2ed6;
		x = ((x << 1) | ((x & 0x8000) >> 15)) & 0xffff;
        x |= 0xffff0000;
        x -= 0xffff22ee;
        this.x = (int) x;
        
        long y = ip.readUnsignedShort(18);;
        y = y ^ (identity & 0xffff) ^ 0xb99b;
        y = ((y << 5) | ((y & 0xF800) >> 11)) & 0xffff;
        y |= 0xffff0000;
        y -= 0xffff8922;
        this.y = (int) y;

		this.target = ip.readUnsignedInt(12);
        /* 
         * TODO Some additional encoding seems to be required based on the C# sources.
         * However, in the packets we receive from the client (5013), the target is
         * sent normally. Something strange happens with the X and Y coordinates though.
         * 
         * target = (((target & 0xffffe000) >> 13) | ((target & 0x1fff) << 19));
         * target ^= 0x5F2D2463 ^ identity;
         * target =- 0x746F4AE6;
         * target =- 0x746F4AE6;
         */
        
        System.out.println(this.toString());
	}

	/**
	 * @return the timer value
	 */
	public long getTimer() {
		return timestamp;
	}

	/**
	 * @return the identity value
	 */
	public long getIdentity() {
		return identity;
	}

	/**
	 * @return the target value
	 */
	public long getTarget() {
		return target;
	}

	/**
	 * @return the X value
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return the Y value
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return the mode
	 */
	public Mode getMode() {
		return mode;
	}

	/**
	 * @return the damage
	 */
	public Skill getSkill() {
		return skill;
	}

	@Override
	public void handle(GameServerClient player) {
		switch(mode){
		case AcceptMarriage:
			break;
		case ArcherAttack:
		{
			PacketWriter outgoingPacket = new PhysicalAttack.ArcherAttack(player, this).build();
			if(outgoingPacket != null)
				outgoingPacket.sendTo(player.getPlayer().view.getPlayers());
			break;
		}
		case DashEffect:
			break;
		case Death:
			break;
		case MagicAttack:
			if(skill instanceof MagicSkill)
				((MagicSkill) skill).handle(player, this);
			break;
		case MagicReflect:
			break;
		case None:
			break;
		case PhysicalAttack:
		{
			PacketWriter outgoingPacket = new PhysicalAttack(player, this).build();
			if(outgoingPacket != null)
				outgoingPacket.sendTo(player.getPlayer().view.getPlayers());
			break;
		}
		case RequestMarriage:
			break;
		case RushAttack:
			break;
		case SendFlowers:
			break;
		case WeaponReflect:
			break;
		default:
			break;
		
		}
	}

	@Override
	public String toString() {
		return "InteractPacket [timestamp=" + timestamp + ", identity=" + identity
				+ ", target=" + target + ", x=" + x + ", y=" + y + ", mode="
				+ mode + ", skill=" + skill + "]";
	}
	
	/**
	 * An enumeration of Interaction modes
	 * @author Jan-Willem Gmelig Meyling
	 */
	public static enum Mode {
		None(0),
		PhysicalAttack(2),
		RequestMarriage(8),
		AcceptMarriage(9),
		SendFlowers(13),
		Death(14),
		RushAttack(20),
		MagicAttack(21),
		WeaponReflect(23),
		DashEffect(24),
		ArcherAttack(25),
		MagicReflect(26);
		
		public final int mode;
		
		private Mode(int mode) {
			this.mode = mode;
		}
		
		/**
		 * @param mode
		 * @return the Mode for a given value
		 */
		public static Mode valueOf(int mode) {
			for(Mode m : Mode.values())
				if(m.mode == mode)
					return m;
			return null;
		}
	}

	@Override
	public PacketWriter build() {
		// TODO Auto-generated method stub
		return null;
	}

}
