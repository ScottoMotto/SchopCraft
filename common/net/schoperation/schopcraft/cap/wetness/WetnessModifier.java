package net.schoperation.schopcraft.cap.wetness;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.schoperation.schopcraft.packet.SchopPackets;
import net.schoperation.schopcraft.packet.StatsPacket;
import net.schoperation.schopcraft.util.ProximityDetect;

/*
 * This is where the magic of changing one's wetness occurs. You'll most likely be here.
 * Geez that sounds wrong. I wonder if Klei implemented their wetness mechanic with dumb jokes too.
 */

public class WetnessModifier {
	
	
	public static void getClientChange(String uuid, float value) {
		
		// basic server variables
		MinecraftServer serverworld = FMLCommonHandler.instance().getMinecraftServerInstance();
		int playerCount = serverworld.getCurrentPlayerCount();
		String[] playerlist = serverworld.getOnlinePlayerNames();	
		
		// loop through each player and see if the uuid matches the sent one.
		for (int num = 0; num < playerCount; num++) {
			
			EntityPlayerMP player = serverworld.getPlayerList().getPlayerByUsername(playerlist[num]);
			String playeruuid = player.getCachedUniqueIdString();
			IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
			boolean equalStrings = uuid.equals(playeruuid);
			
			
			if (equalStrings) {

				wetness.increase(value-10);
			}
		}
	}
	
	
	@SubscribeEvent
	public void onPlayerLogsIn(PlayerLoggedInEvent event) {
		
		EntityPlayer player = event.player;
		
		if (player instanceof EntityPlayerMP) {
			
			IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
			IMessage msg = new StatsPacket.StatsMessage(player.getCachedUniqueIdString(), wetness.getWetness());
			SchopPackets.net.sendTo(msg, (EntityPlayerMP)player);
		}
		
	}
	
	@SubscribeEvent
	public void onPlayerUpdate(LivingUpdateEvent event) {
		
		
		// is this a player? kek
		if (event.getEntity() instanceof EntityPlayer) {
			
			Entity player = event.getEntity();
			IWetness wetness = player.getCapability(WetnessProvider.WETNESS_CAP, null);
			
			
			// get coords of player
			int playerPosX = (int) player.posX-1;
			int playerPosY = (int) player.posY;
			int playerPosZ = (int) player.posZ;
			
			// getting blocks is client-side?? should be fine....................totally
			if (player.world.isRemote) {
				
				wetness.set(10f);
				// these if-statement blocks is for stuff that directly doesn't have to do with water bombardment.
				// check if the player is near a fire
				if (ProximityDetect.isBlockNextToPlayer(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:fire"))) {
					
					// are they in the rain? If so, the fire is less effective
					if (player.isWet()) {
						wetness.decrease(0.25f);
						//if (wetness.getWetness() < 20.0f) { wetness.increase(0.25f); }
					}
					else {
						wetness.decrease(0.5f);
					}
				}
				// check if the player is near a fire - two blocks away. if there's a block between the player and the fire, it won't count.
				else if (ProximityDetect.isBlockNearPlayer2(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:fire"), false)) {
					
					// are they in the rain? If so, the fire is less effective
					if (player.isWet()) {
						wetness.decrease(0.15f);
						//if (wetness.getWetness() < 30.0f) { wetness.increase(0.15f); }
					}
					else {
						wetness.decrease(0.25f);
					}
				}
				// check if the fire is below the player somehow... one block
				else if (ProximityDetect.isBlockUnderPlayer(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:fire"))) {
					
					// are they in the rain? If so, the fire is less effective
					if (player.isWet()) {
						wetness.decrease(0.20f);
						//if (wetness.getWetness() < 20.0f) { wetness.increase(0.20f); }
					}
					else {
						wetness.decrease(0.30f);
					}
				}
				// ...and two blocks
				else if (ProximityDetect.isBlockUnderPlayer2(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:fire"), false)) {
					
					// are they in the rain? If so, the fire is less effective
					if (player.isWet()) {
						wetness.decrease(0.10f);
						//if (wetness.getWetness() < 35.0f) { wetness.increase(0.10f); }
					}
					else {
						wetness.decrease(0.20f);
					}
				}
				// ==============================================================================================================================
				// check if the player is near lava
				if (ProximityDetect.isBlockNextToPlayer(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:lava"))) {
					
					// are they in the rain? If so, the lava is less effective
					if (player.isWet()) {
						wetness.decrease(0.5f);
						//if (wetness.getWetness() < 10.0f) { wetness.increase(0.5f); }
					}
					else {
						wetness.decrease(1f);
					}
				}
				// check if the player is near lava - two blocks away.
				else if (ProximityDetect.isBlockNearPlayer2(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:lava"), false)) {
					
					// are they in the rain? If so, the lava is less effective
					if (player.isWet()) {
						wetness.decrease(0.25f);
						//if (wetness.getWetness() < 30.0f) { wetness.increase(0.25f); }
					}
					else {
						wetness.decrease(0.45f);
					}
				}
				// check if the lava is below the player... one block
				else if (ProximityDetect.isBlockUnderPlayer(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:lava"))) {
					
					// are they in the rain? If so, the lava is less effective
					if (player.isWet()) {
						wetness.decrease(0.5f);
						//if (wetness.getWetness() < 10.0f) { wetness.increase(0.5f); }
					}
					else {
						wetness.decrease(1f);
					}
				}
				// ...and two blocks
				else if (ProximityDetect.isBlockUnderPlayer2(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:lava"), false)) {
					
					// are they in the rain? If so, the lava is less effective
					if (player.isWet()) {
						wetness.decrease(0.25f);
						//if (wetness.getWetness() < 30.0f) { wetness.increase(0.25f); }
					}
					else {
						wetness.decrease(0.45f);
					}
				}
				
				// =============================================================================================================================
				// burning furnace proximity... only same y-level
				if (ProximityDetect.isBlockNextToPlayer(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:lit_furnace"))) {
					
					// are they in the rain? If so, the furnace is less effective
					if (player.isWet()) {
						wetness.decrease(0.10f);
						//if (wetness.getWetness() < 50.0f) { wetness.increase(0.10f); }
					}
					else {
						wetness.decrease(0.40f);
					}
				}
				// two blocks
				else if (ProximityDetect.isBlockNearPlayer2(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:lit_furnace"), false)) {
					
					// are they in the rain? If so, the furnace is less effective
					if (player.isWet()) {
						wetness.decrease(0.05f);
						//if (wetness.getWetness() < 60.0f) { wetness.increase(0.05f); }
					}
					else {
						wetness.decrease(0.20f);
					}
				}
				
				// =============================================================================================================================
				// magma block proximity... only one y-level under
				if (ProximityDetect.isBlockUnderPlayer(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:magma"))) {
					
					// are they in the rain? If so, the magma block is less effective
					if (player.isWet()) {
						wetness.decrease(0.10f);
						//if (wetness.getWetness() < 50.0f) { wetness.increase(0.10f); }
					}
					else {
						wetness.decrease(0.40f);
					}
				}
				// two blocks
				else if (ProximityDetect.isBlockUnderPlayer2(playerPosX, playerPosY, playerPosZ, Block.getBlockFromName("minecraft:magma"), false)) {
					
					// are they in the rain? If so, the magma block is less effective
					if (player.isWet()) {
						wetness.decrease(0.05f);
						//if (wetness.getWetness() < 60.0f) { wetness.increase(0.05f); }
					}
					else {
						wetness.decrease(0.20f);
					}
				}
				
				// send wetness data to server
				IMessage msg = new StatsPacket.StatsMessage(player.getCachedUniqueIdString(), wetness.getWetness());
				SchopPackets.net.sendToServer(msg);
				
			}
			
			// only server-side. sends packets to client just to render on the gui bars.
			if (!player.world.isRemote) {
				
				
				// check if the player is in water
				if (player.isInWater()) {
					
					wetness.increase(5f);
				}
				// check if the player is in the rain
				else if (player.isWet()) {
					
					wetness.increase(0.05f);
				}
				// otherwise, allow for natural drying off (very slow)
				else {
					
					// figure out the conditions of the world, then dry off naturally accordingly
					if (player.world.isDaytime() && player.world.canBlockSeeSky(new BlockPos(playerPosX,playerPosY,playerPosZ))) { wetness.decrease(0.02f); }
					else { wetness.decrease(0.01f); }
					
				}
				
				// send new wetness data to client in order to render correctly
				IMessage msg = new StatsPacket.StatsMessage(player.getCachedUniqueIdString(), wetness.getWetness());
				SchopPackets.net.sendTo(msg, (EntityPlayerMP)player);
			}
			// render wetness particles (vanilla water dripping particles)
			//if (wetness.getWetness() > 50.0f) {
				//player.world.spawnParticle(EnumParticleTypes.DRIP_WATER, playerPosX, playerPosY, playerPosZ, 0.0d, 0.0d, 0.0d, new int[0]);
			//}
		}
	}
}
