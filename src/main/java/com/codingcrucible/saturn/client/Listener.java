/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codingcrucible.saturn.client;

import com.codingcrucible.saturn.Message;

public interface Listener {
    void recieve(Message m);
}
