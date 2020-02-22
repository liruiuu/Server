import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintStream;

public class Server {
private static final int SERVER_PORT=8025;
public static CrazyitMap<String,PrintStream> clients=new CrazyitMap<>();
public void init(){
	try(
		ServerSocket ss=new ServerSocket(SERVER_PORT))
	{
		while(true){
			System.out.println("服务器启动");
			Socket socket=ss.accept();
			
			new ServerThread(socket).start();
		}
	}
	catch (IOException ex){
		System.out.println("服务器启动失败，是否端口"+SERVER_PORT+"已被占用？");
	}
}
public static void main(String[] args){
	Server server=new Server();
	server.init();
}
}
