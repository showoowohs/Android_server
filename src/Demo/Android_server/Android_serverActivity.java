package Demo.Android_server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author XP
 *
 */
public class Android_serverActivity extends Activity {
	public static String SERVERIP ;
	public static final int SERVERPORT = 1234;
	public static String hostip;//IP
	public TextView ShowIP, ShowMsg;
	public Thread desktopServerThread;
	private Handler handler = new Handler();
	public ServerSocket serverSocket;
	private String Target_IP;
	private String line;
	private Button openCamera, openGPS;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findID();
//        System.out.println("findID OK");
        
        hostip = getLocalIpAddress();//Get IP
        if (hostip != null){
        	Log.d("GetIPMAC", hostip);
        	SERVERIP = hostip;
        	Log.d("SERVERIP", SERVERIP);
        	ShowIP.setText("IP is " + SERVERIP);
        }else{
        	Toast.makeText(getBaseContext(), "hostip is null", Toast.LENGTH_LONG).show();
        	Log.d("GetIPMAC", "null");   
        }
        desktopServerThread = new Thread(socket_server);
        desktopServerThread.start();
        
    }
    private void findID(){
    	ShowIP = (TextView) this.findViewById(R.id.TV);
    	ShowMsg = (TextView) this.findViewById(R.id.TV2);
    	openGPS = (Button) this.findViewById(R.id.openGPS);
    	openCamera = (Button) this.findViewById(R.id.openCamera);
    	ClickListener();
    }
    private void ClickListener(){
    	openGPS.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Send_Message(String.valueOf("opengps"));
			}
		});
    	openCamera.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Send_Message(String.valueOf("openCamera"));
			}
		});
    }
    public String getLocalIpAddress() {   
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface   
                    .getNetworkInterfaces(); en.hasMoreElements();) {   
                NetworkInterface intf = en.nextElement();   
                for (Enumeration<InetAddress> enumIpAddr = intf   
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {   
                    InetAddress inetAddress = enumIpAddr.nextElement();   
                    if (!inetAddress.isLoopbackAddress()) {   
                        return inetAddress.getHostAddress().toString();   
                    }   
                }   
            }   
        } catch (SocketException ex) {   
            Log.e("WifiPreference IpAddress", ex.toString());   
        }   
        return null;   
    }

	private Runnable socket_server = new Runnable() {

		public void run() {


			try {

				// �إ�serverSocket
				System.out.println("�إ�serverSocket");
				serverSocket = new ServerSocket(SERVERPORT);

				// ���ݳs�u

				while (true) {

					// �����s�u

					Socket client = serverSocket.accept();

					handler.post(new Runnable() {

						public void run() {
							System.out.println("Connected.");
						}

					});

					try {

						// �������

						DataInputStream in = new DataInputStream(
								client.getInputStream());

						line = in.readUTF();

						if (line != null || line != "") {

							handler.post(new Runnable() {

								public void run() {
									System.out.println("line. "+line);
									
									Target_IP = line.substring(
											0,
											line.lastIndexOf(": ") )
                                            .toLowerCase();
									System.out.println("Target_IP "+Target_IP);
									ShowMsg.setText(line);
									

								}

							});

						}

//						break;

					} catch (Exception e) {

						handler.post(new Runnable() {

							public void run() {
								System.out.println("�ǰe����");
								

							}

						});

					}

				}

			} catch (IOException e) {

				handler.post(new Runnable() {

					public void run() {
						System.out.println("�إ�socket����");
						

					}

				});

			}

		}

	};

	        

//    class desktopServerThread implements Runnable{
//    	public void run() {
//    		System.out.println("Thread OK");
//    		// TODO Auto-generated method stub
//    		try {
//    			System.out.println("S: Connecting...");
//    			serverSocket = new ServerSocket(SERVERPORT);
//    			System.out.println("serverSocket OK");
//                while (true) {
//                	Socket client = serverSocket.accept();
//    				System.out.println("S: Receiving...");
//                    try {
//                    	BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
//                    	String str = in.readLine();
//                    	System.out.println("S: Received: '" + str + "'");
//                    } catch(Exception e) {
//    			    System.out.println("S: Error");
//    			    e.printStackTrace();
//                    } finally {
//                    	client.close();
//                    	System.out.println("S: Done.");
//                    }
//                }
//    		} catch (Exception e) {
//    			System.out.println("Exception S: Error");
//    			e.printStackTrace();
//    		}
//    	}
//    }
	public void Send_Message(String str){
//		System.out.println("try..  " );
		InetAddress serverAddr = null;

		SocketAddress sc_add = null;

		Socket socket = null;

		// �n�ǰe���r��
		
//		String message = "Hello Socket";
		
		try {

			// �]�wServer IP��m

			serverAddr = InetAddress.getByName(Target_IP.toString());

			// �]�wport:1234

			sc_add = new InetSocketAddress(serverAddr, 1234);

			socket = new Socket();

			// �PServer�s�u�Atimeout�ɶ�2��

			socket.connect(sc_add, 2000);

			// �ǰe���

			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());

			out.writeUTF(str);
			System.out.println("str  " +str);
//			Show.setText(Send);
			// ����socket

			socket.close();

		} catch (UnknownHostException e) {
			System.out.println("InetAddress����إߥ���");
//			Show.setText("InetAddress����إߥ���");

		} catch (SocketException e) {
			System.out.println("socket�إߥ���");
//			Show.setText("socket�إߥ���");

		} catch (IOException e) {
			System.out.println("�ǰe����");
//			Show.setText("�ǰe����");

		}
	}
}