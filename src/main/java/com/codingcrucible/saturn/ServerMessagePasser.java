/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codingcrucible.saturn;

import java.util.List;

public interface ServerMessagePasser<E> extends MessagePasser<E> {
      List<MessageConsumer> getClients();
}
