package com.lucky.boot.stopup;



import com.lucky.boot.conf.ServerConfig;
import com.lucky.framework.uitls.file.FileUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public abstract class LuckyShutdown {
	
	private static final ServerConfig serverCfg=ServerConfig.getServerConfig();

	public static void shutdown(int closePort,String command) throws IOException {
        Socket socket = new Socket("localhost", closePort);
        OutputStream stream = socket.getOutputStream();
        for(int i = 0;i < command.length();i++){
            stream.write(command.charAt(i));
            stream.flush();
        }
        stream.close();
        socket.close();
	}

}
