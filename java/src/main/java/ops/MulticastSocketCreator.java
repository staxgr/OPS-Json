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
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Anton
 */
class MulticastSocketCreator
{
    private static Map<Integer, MulticastSocket> sockets = new HashMap<Integer, MulticastSocket>();
    public static synchronized MulticastSocket getMulticastSocket(int port) throws IOException
    {
        if(sockets.containsKey(port))
        {
            return sockets.get(port);
        }
        MulticastSocket newSocket = new MulticastSocket(port);
        //newSocket.setLoopbackMode(true);
        
        sockets.put(port, newSocket);
        return newSocket;
    }
}
