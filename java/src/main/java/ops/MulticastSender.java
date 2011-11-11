/**
*
* Copyright (C) 2011 Anton Gravestam.
*
* This file is part of OPS (Open Publish Subscribe).
*
* OPS (Open Publish Subscribe) is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.

* OPS (Open Publish Subscribe) is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with OPS (Open Publish Subscribe).  If not, see <http://www.gnu.org/licenses/>.
*/
package ops;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;

/**
 *
 * @author Anton Gravestam
 */
class MulticastSender implements Sender
{

    private MulticastSocket multicastSocket;
	private final String ip;
	private final int basePort;

    public MulticastSender(int basePort, String ip, String localInterface, int ttl, int outSocketBufferSize) throws IOException
    {
        this.basePort = basePort;
		this.ip = ip;
		//multicastSocket = new MulticastSocket(port);
        multicastSocket = MulticastSocketCreator.getMulticastSocket(basePort);


        if (!localInterface.equals("0.0.0.0"))
        {//For some reason this method throws an error if we try to set outgoing interface to ANY.
            multicastSocket.setNetworkInterface(NetworkInterface.getByInetAddress(Inet4Address.getByName(localInterface)));
        }

        if (outSocketBufferSize > 0)
        {
            multicastSocket.setSendBufferSize(outSocketBufferSize);
        }
        multicastSocket.setTimeToLive(ttl);




    }
    public boolean sendTo(byte[] bytes, int offset, int size, String ip, int port)
    {
        try
        {
            return this.sendTo(bytes, offset, size, InetAddress.getByName(ip), port);
        } catch (UnknownHostException ex)
        {
            return false;
        }

    }
    public boolean sendTo(byte[] bytes, int offset, int size, InetAddress ipAddress, int port)
    {
        try
        {

            DatagramPacket datagramPacket = new DatagramPacket(bytes, offset, size, ipAddress, port);

            multicastSocket.send(datagramPacket);

            return true;
        } catch (IOException ex)
        {
            return false;
        }

    }

    public boolean sendTo(byte[] bytes, String ip, int port)
    {
        return sendTo(bytes, 0, bytes.length, ip, port);

    }
	@Override
	public boolean sendTo(byte[] bytes, Topic<?> topic) {
		return sendTo(bytes, ip, basePort + topic.getPort());
		
	}
}
