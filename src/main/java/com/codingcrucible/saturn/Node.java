/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codingcrucible.saturn;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public final class Node<E> implements MessageConsumer<E> {

    /* A positive integer */
    private final int guid;
    private final OperationConsumer<E> oc;
    private final MessagePasser mc;
    private final Transform<E> t;
    private final AtomicInteger msgGenerated;
    private final AtomicInteger msgRecieved;
    private final Queue<Message<E>> outgoingQueue;

    public Node(int guid, OperationConsumer<E> oc,
            MessagePasser mc, Transform<E> t) {
        this.guid = guid;
        this.oc = oc;
        this.mc = mc;
        this.t = t;
        msgGenerated = new AtomicInteger(0);
        msgRecieved = new AtomicInteger(0);
        outgoingQueue = new ConcurrentLinkedQueue();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Node) {
            Node n = (Node) o;
            return guid - n.guid == 0;
        } else
            return false;
    }

    @Override
    public int hashCode() {
        return guid;
    }

    public void generate(E op) {
        oc.consume(op);
        mc.sendToAll(createMessage(op));

        /*add to outgoing messages */
        outgoingQueue.add(createMessage(op));
        msgGenerated.incrementAndGet();
    }

    @Override
    public void consume(Message<E> m) {
        Message<E> v;
        /* discard acknowledged messages */
        Iterator<Message<E>> i = outgoingQueue.iterator();

        while (i.hasNext()) {
            v = i.next();
            if (v.msgGenerated < m.msgReceived)
                i.remove();
        }

        /* Assert that msg.msgGenerated == msgReceived */
        if (m.msgGenerated != msgRecieved.get()) {
            System.err.println("Assert fails");
            return;
        }

        Iterator<Message<E>> i2 = outgoingQueue.iterator();
        while (i2.hasNext()) {
            v = i2.next();
            E[] cst = t.xform(v.op, m.op);
            v.op = cst[0];
            m.op = cst[1];
        }

        oc.consume(m.op);
        mc.echoToAll(m.node, m);
        msgRecieved.incrementAndGet();

    }

    private Message createMessage(E o) {
        return new Message(this, o, msgGenerated.get(), msgRecieved.get());
    }

    @Override
    public int getGuid() {
        return guid;
    }

}
