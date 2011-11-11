/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ops;

import ops.participant.Message;

/**
 *
 * @author Anton
 */
public class Topic<T> {
    public static final int DEFAULT_TTL = 1;
	private final String name;
	private final int port;

    public Topic(String name, int port)
    {
        this.name = name;
		this.port = port;
    }
    
    public int getPort() {
		return port;
	}
    
    public String getName() {
		return name;
	}


}
