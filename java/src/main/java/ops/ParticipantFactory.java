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
