/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codingcrucible.saturn;

public final class CSTuple {

    private Operation clientOp;
    private Operation serverOp;

    public CSTuple(Operation clientOp, Operation serverOp) {
        this.clientOp = clientOp;
        this.serverOp = serverOp;
    }

    public Operation getClientOp() {
        return clientOp;
    }

    public Operation getServerOp() {
        return serverOp;
    }

}
