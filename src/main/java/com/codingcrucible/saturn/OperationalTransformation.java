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

public final class OperationalTransformation {
    
    private final Transform t;
    private final AtomicInteger msgGenerated;
    private final AtomicInteger msgRecieved;
    private final Queue<EnumeratedOpTuple> outgoingQueue;
    private final Applier a;
    private final Sender s;

    public OperationalTransformation(Transform t, Applier a, Sender s) {
        this.t = t;
        this.a = a;
        this.s = s;
        msgGenerated = new AtomicInteger(0);
        msgRecieved = new AtomicInteger(0);
        outgoingQueue = new ConcurrentLinkedQueue();
    }
    
    public void generate(Operation op){
        a.apply(op);
        s.send(new Message(op, msgGenerated.get(), msgRecieved.get()));
        
        /*add to outgoing messages */
        outgoingQueue.add(new EnumeratedOpTuple(op, msgGenerated.get()));
        msgGenerated.incrementAndGet();
    }
    
    public void recieve(Message m){
        /* discard acknowledged messages */
        Iterator<EnumeratedOpTuple> i = outgoingQueue.iterator();
        
        while(i.hasNext()){
            EnumeratedOpTuple eot = i.next();
            if (eot.getValue() < m.msgReceived)
                i.remove();
        }
        
        /* Assert that msg.msgGenerated == msgReceived */
        if (m.msgGenerated != msgRecieved.get()){
            System.err.println("Assert fails");
            return;
        }
        
        Iterator<EnumeratedOpTuple> i2 = outgoingQueue.iterator();
        while(i2.hasNext()){
            EnumeratedOpTuple eot = i2.next();
            CSTuple cst = t.xform(new CSTuple(eot.getOp(), m.op));
            m.op = cst.getServerOp();
            eot.setOp(cst.getClientOp());
        }
        
        a.apply(m.op);
        msgRecieved.incrementAndGet();
            
    }

}
