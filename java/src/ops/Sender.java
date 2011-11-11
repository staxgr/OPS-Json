/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ops;

/**
 *
 * @author staxgr
 */
public interface Sender
{
    boolean sendTo(byte[] bytes, Topic<?> topic);
    
}
