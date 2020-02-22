import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ServerThread extends Thread {
	private Socket socket;
	BufferedReader br = null;
	PrintStream ps = null;

	public ServerThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			br = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			ps = new PrintStream(socket.getOutputStream());
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				if (line.startsWith(CrazyitProtocol.USER_ROUND)
						&& line.endsWith(CrazyitProtocol.USER_ROUND)) {
					String temp = getRealMsg(line);
					System.out.println("temp=" + temp);
					String outs = getAllUser();
					// /////////////////////登录
					try {
						String ipc = socket.getInetAddress().toString(); // 获得客户端�IP
						String ipclient = ipc.substring(1);

						// 从socketclient获得一个输入对象，以便接收从客户端送来的数据
						// BufferedReader socketinput = new BufferedReader(
						// new
						// InputStreamReader(socketclient.getInputStream()));
						// 获取一个输出对象，以便把服务器的信息返回给客户端
						// PrintWriter socketoutput = new PrintWriter(
						// socketclient.getOutputStream(), true);
						// String temp = socketinput.readLine();//
						//// 读取输入对象的信息
						String s[];
						s = temp.split("/");
						System.out.println(s[1] + "客户端IP=" + ipclient);
						System.out.print(s[1] + "客户端：");
						System.out.println(temp);
						switch (s[0]) {
						case "d": {
							Class.forName("org.sqlite.JDBC");
							Connection dConn = DriverManager
									.getConnection("jdbc:sqlite:serverdata.db");
							Statement dStat = dConn.createStatement();
							ResultSet dRs = dStat
									.executeQuery("select * from membertb where id='"
											+ s[1] + "';");
							String dclientpassword = dRs.getString("password");
							if (dRs.next()) {
								if (s[3].equals(dclientpassword)) {
									// int
									// dclientid=Integer.valueOf(s[1]).intValue();
									dStat.executeUpdate("delete from membertb where id='"
											+ s[1] + "';");// 删除membertb中的记录
									ps.println("id_delete");//  把信息返回给客户端
									System.out.println(s[1] + ": ID成功删除");
								} else {
									System.out.println(s[1]
											+ ": 密码不正确，删除失败");
									ps.println("password_error" + "/" + outs);
								}// 把信息返回给客户端 //-----------------------
							} else {
								System.out.println(s[1] + ": 账号不存在，删除失败");
								ps.println("id_not_exist" + "/" + outs);
							}// 把信息返回给客户端 //-----------------------
							dRs.close();
							dConn.close(); // 结束数据库的连接
							break;
						}

						case "s": {
							Class.forName("org.sqlite.JDBC");
							Connection sConn = DriverManager
									.getConnection("jdbc:sqlite:serverdata.db");
							Statement sStat = sConn.createStatement();
							ResultSet sRs = sStat
									.executeQuery("select * from membertb where id='"
											+ s[1] + "';"); // 查询数据
							boolean f0 = false;
							boolean f1 = false;

							if (!sRs.next()) {
								f0 = true;
							}
							;
							ResultSet sRs1 = sStat
									.executeQuery("select * from membertb where name='"
											+ s[2] + "';");  // 查询数据
							if (!sRs1.next()) {
								f1 = true;
							}
							;
							if (f0 && f1) {

								// int
								// clientid=Integer.valueOf(s[1]).intValue();
								sStat.executeUpdate("insert into membertb values('"
										+ s[1]
										+ "','"
										+ s[2]
										+ "','"
										+ s[3]
										+ "','" + ipclient + "');");// 插入会员账号IP表，两列
								System.out.println(s[1] + ": ID创建成功");
								ps.println("id_create" + "/" + outs); // -----------------------
							} else {
								if (f1) {

									ps.println("id_exist" + "/" + outs); // -----------------------
									System.out.println(s[1]
											+ ": ID存在，创建失败");
								} else {
									ps.println("name_exist" + "/" + outs); // -----------------------
									System.out.println(s[2]
											+ ": name存在，创建失败");
								}
							}
							sRs.close();
							sConn.close(); // 结束数据库的连接
							break;
						}

						case "l": {

							Class.forName("org.sqlite.JDBC");
							Connection conn1 = DriverManager
									.getConnection("jdbc:sqlite:serverdata.db");
							Statement stat1 = conn1.createStatement();
							ResultSet rs2 = stat1
									.executeQuery("select * from membertb where id='"
											+ s[1] + "';");// 查询数据
							String clientpassword = rs2.getString("password");
							/*
							 * int j=0; while(rs2.next()){ j++;}
							 * System.out.println("账号数量="+j);
							 */
							if (!rs2.next()) {
								ps.println("id_not_exist" + "/" + outs);// 判断账号是否存在
																		// //-----------------------
								System.out.println(s[1] + "：账号不存在，登陆失败");
							} else if (s[3].equals(clientpassword)) {

								if (Server.clients.containsKey(s[1])) {
									ps.println("repeat_login" + "/" + outs);// 把信息返回给客户端
									// System.out.println("repeat_login" + "/" +
									// outs); // //-----------------------
									System.out.println(s[1] + ": 重复登录");
									// ps.println(CrazyitProtocol.NAME_REP);
									// Server.clients.removeByValue(Server.clients.get(s[1]));
									// Server.clients.put(s[1],ps);
								} else {
									ps.println("success" + "/" + outs);// 把信息返回给客户端
									// System.out.println("success" + "/" +
									// outs); // //-----------------------
									Server.clients.put(s[1], ps);
									System.out.println(s[1] + ": 登陆成功");
								}

							} else {
								ps.println("password_wrong" + "/" + outs);// // 把信息返回给客户端
																			// //-----------------------
								System.out.println(s[1] + "：密码错误，登陆失败");
							}

							if (s[2].equals(clientpassword))
								stat1.executeUpdate("update membertb set ip='"
										+ ipclient + "' where id='" + s[1]
										+ "';");// 更新IP
							rs2.close();
							conn1.close();  // 结束数据库的连接
							break;
						}
						}

					} catch (Exception e) {
						// TODO: handle exception
					}

					// /////////////////////转发

				} else if (line.startsWith(CrazyitProtocol.PRIVATE_ROUND)
						&& line.endsWith(CrazyitProtocol.PRIVATE_ROUND)) {
					String userAndMsg = getRealMsg(line);
					System.out.println(userAndMsg);
					String user = userAndMsg.split(CrazyitProtocol.SPLIT_SIGN)[0];
					System.out.println(user);
					String msg = userAndMsg.split(CrazyitProtocol.SPLIT_SIGN)[1];
					System.out.println(msg);
					System.out.println(Server.clients.getKeyByValue(ps) + ":"
							+ msg);
					if (Server.clients.get(user) != null) {
						 if(msg.equals("kill_while")){
						 System.out.println("客户端333=="+Server.clients.getKeyByValue(ps)+":"+msg);
						 Server.clients.get(user).println(Server.clients.getKeyByValue(ps)+":"+msg);
						 System.out.println("333333333333333333333333");
						 Server.clients.removeByValue(ps);
						 /**************************************************************/
						 try {
								if (br != null) {
									br.close();
									System.out.println("br.close()");
								}
								if (ps != null) {
									ps.close();
									System.out.println("ps.close()");
								}
								if (socket != null) {
									socket.close();
									System.out.println("socket.close()");
								}
							} catch (IOException ex) {
								ex.printStackTrace();
							}
						 System.out.println("break;");
					    	break;
                     /**************************************************************/
						 }else{
						Server.clients.get(user).println(
								Server.clients.getKeyByValue(ps) + ":" + msg);
						System.out.println("客户端=="
								+ Server.clients.getKeyByValue(ps) + ":" + msg);
					}
						 }

				} else {
					String msg = getRealMsg(line);
					for (PrintStream clientPs : Server.clients.valueSet()) {
						clientPs.println(Server.clients.getKeyByValue(ps) + ":"
								+ msg);
						System.out.println(Server.clients.getKeyByValue(ps)
								+ ":" + msg);
					}
				}
			}

		} catch (IOException e) {
			Server.clients.removeByValue(ps);
			System.out.println(Server.clients.size());
			try {
				if (br != null) {
					br.close();
				}
				if (ps != null) {
					ps.close();
				}
				if (socket != null) {
					socket.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private String getRealMsg(String line) {
		return line.substring(CrazyitProtocol.PROTOCOL_LEN, line.length()
				- CrazyitProtocol.PROTOCOL_LEN);
	}

	private String getAllUser() {
		String temps;
		String outs = null;
		try {
			// 连接SQLite的JDBC

			Class.forName("org.sqlite.JDBC");

			//  建立一个数据库名serverdata.db的连接，如果不存在就在当前目录下创建之֮
			// "jdbc:odbc:Driver={MicroSoft Access Driver (*.mdb)};DBQ=D:\\aa.mdb";
			Connection conn = DriverManager
					.getConnection("jdbc:sqlite:serverdata.db");

			Statement stat = conn.createStatement();
			/*
			 * stat.executeUpdate(
			 * "create table membertb(id  varchar(20), password  varchar(20));"
			 * );//创建会员账号密码表，两列  stat.executeUpdate(
			 * "create table iptb(id  varchar(20), ip varchar(20));"
			 * );//创建会员账号IP表，两列
			 * "create table nametb(id  varchar(20), name varchar(20));"
			 * );//创建会员账号name表，两列
			 * 
			 * stat.executeUpdate(
			 * "insert into membertb values('1000','123456');" );//插入数据
			 * stat.executeUpdate(
			 * "insert into membertb values('1001','123456');" );
			 * 
			 * stat.executeUpdate(
			 * "insert into iptb values('1000','192.168.1.101');" ); //插入数据
			 * stat.executeUpdate(
			 * "insert into iptb values('1001','192.168.1.104');" );
			 * stat.executeUpdate( "insert into nametb values('1000','ϰ��ƽ');"
			 * ); //������� stat.executeUpdate(
			 * "insert into nametb values('1001','���ǿ');" );
			 */
			ResultSet rs = stat.executeQuery("select * from membertb;"); // 查询数据
			temps = "";
			while (rs.next()) { // 将查询到的数据打印出来

				System.out.print("id = " + rs.getString("id") + "     ");  // 列属性一
				System.out.print("name = " + rs.getString("name") + "     "); // 列属性二
				System.out.print("password = " + rs.getString("password")
						+ "     "); // 列属性三
				System.out.println("ip = " + rs.getString("ip")); // 列属性四
				outs = temps + rs.getString("id") + "#" + rs.getString("name")
						+ "#" + rs.getString("ip");
				temps = outs + "/";
			}
			rs.close();
			conn.close(); // 结束数据库的连接
			// System.out.println("outs---->" + outs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outs;
	}
}
