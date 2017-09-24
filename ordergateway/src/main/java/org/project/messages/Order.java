package org.project.messages;

/**
 * Created by jojo on 7/2/17.
 */
public interface Order {

    Integer getPrice();
    byte[] toBytes();
}
