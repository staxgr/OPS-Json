/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
public class MulticastSender implements Sender
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
