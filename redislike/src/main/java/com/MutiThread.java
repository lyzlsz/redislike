package com;

import com.Command;
import com.Protocol;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * package:com.Commands
 * Description:TODO
 *
 * @date:2019/8/11 0011
 * @Author:weiwei
 **/
public class MutiThread implements Runnable {
    private Socket socket;

    public MutiThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            {
                while (true) {
                    Command command = Protocol.readCommand(is);
                    command.run(os);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
