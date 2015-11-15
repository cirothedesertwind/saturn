/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codingcrucible.saturn;


public final class OperationalTransform {

    private OperationalTransform() {}
    
    public static final Node createClient(int guid, OperationConsumer oc, 
            MessageConsumer target, Transform t){
        return new NodeImpl(guid, oc, 
                new MessagePasserImpl(target, false), t);
    }
    
    public static final Node createServer(int guid, OperationConsumer oc, 
            MessagePasser p, Transform t){
        return new NodeImpl(guid, oc, p, t);
    }
    
    public static final ServerMessagePasser createServerMessagePasser(){
        return new MessagePasserImpl(true);
    }
}
