/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ops.participant;

import java.io.IOException;

import ops.participant.multicast.MulticastParticipant;

/**
 *
 * @author Anton
 */
class ParticipantFactory {

    static Participant get(String domainDescriptor)
    {
        int protoIdx = domainDescriptor.indexOf("://");
        int addressIdx = domainDescriptor.indexOf(":", protoIdx + 1);
        String proto = domainDescriptor.substring(0, protoIdx);
        String address = domainDescriptor.substring(protoIdx + "://".length(), addressIdx);
        String port = domainDescriptor.substring(addressIdx + ":".length());


        System.out.println(new StringBuilder().append("proto = ").append(proto).append(" address = ").append(address).append(" port = ").append(port).toString());
        try
        {
            MulticastParticipant mp = new MulticastParticipant(address, Integer.parseInt(port));
            return mp;
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
        
    }

}
