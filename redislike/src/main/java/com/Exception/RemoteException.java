package com.Exception;

/**
 * package:com.Exception
 * Description:TODO
 *
 * @date:2019/8/9 0009
 * @Author:weiwei
 **/
public class RemoteException extends Exception {
    public RemoteException() {

    }

    public RemoteException(String messgae) {
        super(messgae);
    }

    public RemoteException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteException(Throwable cause) {
        super(cause);
    }

    public RemoteException(String message, Throwable cause, boolean enableSupperssion, boolean writableTrace) {
        super(message, cause, enableSupperssion, writableTrace);
    }
}
