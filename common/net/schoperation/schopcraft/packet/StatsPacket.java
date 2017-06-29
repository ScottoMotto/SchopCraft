package net.schoperation.schopcraft.packet;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.schoperation.schopcraft.cap.wetness.WetnessModifier;
import net.schoperation.schopcraft.gui.GuiRenderBar;
import net.schoperation.schopcraft.packet.StatsPacket.StatsMessage;

public class StatsPacket implements IMessageHandler<StatsMessage, IMessage> {
	
	@Override
	public IMessage onMessage(StatsMessage message, MessageContext ctx) {
		
		if(ctx.side.isClient()) {
			
			String uuid = message.uuid;
			float wetness = message.wetness;
			GuiRenderBar.getServerWetness(wetness);
			
		}
		else {
			
			String uuid = message.uuid;
			float wetness = message.wetness;
			WetnessModifier.getClientChange(uuid, wetness);
		}
		
		return null;
	}
	
	public static class StatsMessage implements IMessage {
		
		// actual variables to be used and sent and crap
		private String uuid;
		private float wetness;
		
		// dumb constructor WHY FML
		public StatsMessage() {}
		
		public StatsMessage(String uuid, float wetness) {
			
			this.uuid = uuid;
			this.wetness = wetness;
		}
		
		@Override
		public void fromBytes(ByteBuf buf) {
			
			this.uuid = ByteBufUtils.readUTF8String(buf);
			this.wetness = buf.readFloat();
		}
		
		@Override
		public void toBytes(ByteBuf buf) {
			
			ByteBufUtils.writeUTF8String(buf, uuid);
			buf.writeFloat(wetness);
		}
	}
}
