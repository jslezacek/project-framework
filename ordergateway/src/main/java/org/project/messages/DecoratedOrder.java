package org.project.messages;

/**
 * Created by jojo on 7/2/17.
 */
public abstract class DecoratedOrder implements Order {
    Order myOrder;
    long receivedTimestamp;

    public DecoratedOrder(Order normalOrder) {
        this.myOrder = normalOrder;
    }

    public void setTimestamp() {
        this.receivedTimestamp = System.nanoTime();
    }
}
