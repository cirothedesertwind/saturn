/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codingcrucible.saturn;

public final class CSTuple {

    private Runnable clientOp;
    private Runnable serverOp;

    public CSTuple(Runnable clientOp, Runnable serverOp) {
        this.clientOp = clientOp;
        this.serverOp = serverOp;
    }

    public Runnable getClientOp() {
        return clientOp;
    }

    public Runnable getServerOp() {
        return serverOp;
    }

}
