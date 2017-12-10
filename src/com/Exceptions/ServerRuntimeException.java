package com.Exceptions;

public class ServerRuntimeException extends Exception {

    /**
     * Declares an exception on the server side
     *
     * @return
     */
    @Override
    public String toString() {
        return "ServerRuntimeException{ The server encountered an error while excecuting a client request. }";
    }
}
