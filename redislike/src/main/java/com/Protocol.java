package com;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


/**
 * package:com
 * Description:TODO
 *
 * @date:2019/8/2
 * @Author:weiwei
 **/
public class Protocol {
    public static Object read(InputStream is) throws IOException {
        return process(is);
    }

    public static Command readCommand(InputStream is) throws Exception {
        Object o = read(is);
        if (!(o instanceof List)) {
            throw new Exception("命令必须是Array类型");
        }

        List<Object> list = (List<Object>) o;
        if (list.size() <= 1) {
            throw new Exception("命令个数必须大于1");
        }

        Object o2 = list.remove(0);
        if (!(o2 instanceof byte[])) {
            throw new Exception("错误的命令类型");
        }

        byte[] array = (byte[]) o2;
        String commandName = new String(array);
        String className = String.format("com.Commands.%sCommand", commandName.toUpperCase());
        Class<?> cls = Class.forName(className);
        if (!Command.class.isAssignableFrom(cls)) {
            throw new Exception("错误的命令");
        }
        Command command = (Command) cls.newInstance();
        command.setArgs(list);

        return command;

    }

    private static String processSimpleString(InputStream is) throws IOException {
        return readLine(is);
    }

    private static String processError(InputStream is) throws IOException {
        return readLine(is);
    }

    private static long processInteger(InputStream is) throws IOException {
        return readInteger(is);
    }


    private static byte[] processBulkString(InputStream is) throws IOException {
        int len = (int) readInteger(is);
        if (len == -1) {
            //"$-1\r\n-->null
            return null;
        }
        byte[] r = new byte[len];
        is.read(r, 0, len);

//        for (int i = 0; i < len; i++) {
//            int b = is.read();
//            r[i] = byte(b);
//        }
        //"$5\r\nhello\r\n";

        is.read();
        is.read();

        return r;
    }

    private static List<Object> processArray(InputStream is) throws IOException {
        int len = (int) readInteger(is);

        if (len == -1) {
            //"$-1\r\n-->null
            return null;
        }

        List<Object> list = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            try {
                list.add(process(is));
            } catch (RemoteException e) {
                list.add(e);
            }
        }
        return list;
    }

    private static Object process(InputStream is) throws IOException {
        int b = is.read();
        if (b == -1) {
            throw new RuntimeException("不应该读到结尾的");
        }
        switch (b) {
            case '+':
                return processSimpleString(is);
            case '-':
                throw new RemoteException(processError(is));
            case ':':
                return processInteger(is);
            case '$':
                return processBulkString(is);
            case '*':
                return processArray(is);
            default:
                throw new RuntimeException("不识别的类型");
        }
    }

    private static String readLine(InputStream is) throws IOException {
        boolean needRead = true;
        StringBuilder sb = new StringBuilder();
        int b = -1;
        while (true) {
            if (needRead == true) {
                b = is.read();
                if (b == -1) {
                    throw new RemoteException("不应该读到结尾的");
                }
            } else {
                needRead = true;
            }
            if (b == '\r') {
                int c = is.read();
                if (c == -1) {
                    throw new RuntimeException("不应该读到结尾的");
                }
                if (c == '\n') {
                    break;
                }
                if (c == '\r') {
                    sb.append((char) b);
                    b = c;
                    needRead = false;
                }else{
                    sb.append((char)b);
                    sb.append((char)c);
                }
            } else {
                sb.append((char) b);
            }
        }
        return sb.toString();
    }

    public static long readInteger(InputStream is) throws IOException {
        boolean isNegative = false;
        StringBuilder sb = new StringBuilder();
        int b = is.read();
        if (b == -1) {
            throw new RuntimeException("不应该读到结尾的");
        }
        if (b == '-') {
            isNegative = true;
        } else {
            sb.append((char) b);
        }
        while (true) {
            b = is.read();
            if (b == -1) {
                throw new RuntimeException("不应该读到结尾的");
            }
            if (b == '\r') {
                int c = is.read();
                if (c == -1) {
                    throw new RuntimeException("不应该读到结尾的");
                }
                if (c == '\n') {
                    break;
                }
                throw new RuntimeException("没有读到\\r\\n");
            } else {
                sb.append((char) b);
            }
        }

        long v = Long.parseLong(sb.toString());
        if (isNegative) {
            v = -v;
        }
        return v;
    }

    public static void writeError(OutputStream os, String message) throws IOException {
        os.write('-');
        os.write(message.getBytes());//默认字符集是utf8，redis是GBK
        os.write("\r\n".getBytes());
    }

    public static void writeInteger(OutputStream os, long v) throws IOException {
        //v = 10
        //:10\r\n

        os.write(':');
        os.write(String.valueOf(v).getBytes());
        os.write("\r\n".getBytes());

    }

    public static void writeArray(OutputStream os, List<?> list) throws Exception {
        os.write('*');
        os.write(String.valueOf(list.size()).getBytes());
        os.write("\r\n".getBytes());
        for (Object o : list) {
            if (o instanceof String) {
                writeBulkString(os, (String) o);
            } else if (o instanceof Integer) {
                writeInteger(os, (Integer) o);
            } else if (o instanceof Long) {
                writeInteger(os, (Long) o);
            } else {
                throw new Exception("错误的类型");
            }
        }
    }

    public static void writeBulkString(OutputStream os, String s) throws IOException {
        byte[] buf = s.getBytes();
        os.write('$');
        os.write(String.valueOf(buf.length).getBytes());
        os.write("\r\n".getBytes());
        os.write(buf);
        os.write("\r\n".getBytes());
    }

    public static void writenull(OutputStream os) throws IOException {
        os.write('$');
        os.write('-');
        os.write('1');
        os.write('\r');
        os.write('\n');
    }
}
