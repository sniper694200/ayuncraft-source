package net.minecraft.src;

import net.minecraft.client.Minecraft;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet250CustomPayload extends Packet {
	/** Name of the 'channel' used to send data */
	public String channel;

	/** Length of the data to be read */
	public int length;

	/** Any data */
	public byte[] data;

	public Packet250CustomPayload() {
	}

	public Packet250CustomPayload(String par1Str, byte[] par2ArrayOfByte) {
		this.channel = par1Str;
		this.data = par2ArrayOfByte;

		if (par2ArrayOfByte != null) {
			this.length = par2ArrayOfByte.length;

			if (this.length > 32767) {
				System.err.println("Tried to send '" + this.channel + "' with size " + this.length + " which is larger than the 32k so it was truncated down to 1 byte");
				this.length = 1;
				this.data = new byte[] { (byte) 0xFF };
			}
		}
	}

	/**
	 * Abstract. Reads the raw packet data from the data stream.
	 */
	public void readPacketData(DataInputStream par1DataInputStream) throws IOException {
		this.channel = readString(par1DataInputStream, 20);
		this.length = par1DataInputStream.readShort();

		if (this.length > 0 && this.length < 32767) {
			if(Minecraft.getMinecraft().gameSettings.useDefaultProtocol){
				par1DataInputStream.skipBytes(this.length);
			}else {
				this.data = new byte[this.length];
				par1DataInputStream.readFully(this.data);
			}
		}
	}

	/**
	 * Abstract. Writes the raw packet data to the data stream.
	 */
	public void writePacketData(DataOutputStream par1DataOutputStream) throws IOException {
		writeString(this.channel, par1DataOutputStream);
		par1DataOutputStream.writeShort((short) this.length);

		if (this.data != null) {
			par1DataOutputStream.write(this.data);
		}
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(NetHandler par1NetHandler) {
		par1NetHandler.handleCustomPayload(this);
	}

	/**
	 * Abstract. Return the size of the packet (not counting the header).
	 */
	public int getPacketSize() {
		return 2 + this.channel.length() * 2 + 2 + this.length;
	}
}
