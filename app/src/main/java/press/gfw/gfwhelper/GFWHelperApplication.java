package press.gfw.gfwhelper;

import android.app.Application;
import android.content.Intent;

import java.sql.Timestamp;

import press.gfw.Client;

/**
 * Created by a14248 on 10/24/17.
 */

final public class GFWHelperApplication extends Application {

    private static GFWHelperApplication singleton;
    GFWHelperService service = null;
    Client client = null;
    Intent serviceIntent = null;
    String serverHost = "127.0.0.1";
    String serverPort = "12345";
    String password = "password";
    String proxyPort = "3128";

    /**
     * 打印信息
     *Client client = null;
     * @param o 打印对象
     */
    @SuppressWarnings("unused")
    private static void log(Object o) {

        String time = (new Timestamp(System.currentTimeMillis())).toString().substring(0, 19);
        System.out.println("[" + time + "] " + o.toString());

    }

    public GFWHelperApplication getInstance() {
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

    protected void startService(boolean force) {
        if (service == null && force) {
            serviceIntent = new Intent(this, GFWHelperService.class);
            log("Application serverHost = " + serverHost);
            //Run startService from a Thread
            Thread t = new Thread(){
                public void run(){
                    startService(serviceIntent);
                }
            };
            t.start();
        }
    }

    protected void stopService() {
        if (serviceIntent != null) {
            stopService(serviceIntent);
            service = null;
            serviceIntent = null;
        }
    }

    /**
     * 开始运行
     */
    @SuppressWarnings("deprecation")
    protected void startClient() {
        if (client != null && !client.isKill()) {

            if (serverHost.equals(client.getServerHost()) && serverPort.equals(String.valueOf(client.getServerPort())) && password.equals(client.getPassword()) && proxyPort.equals(String.valueOf(client.getListenPort()))) {
                return;
            } else {
                client.kill();
            }

        }
        client = new Client(serverHost, serverPort, password, proxyPort);
        client.start();
        log("client.started!");

    }

    /**
     * 停止运行
     */
    protected void stopClient() {
        //closeProxy();
        if (client != null && !client.isKill()) {
            client.kill();
            log("client.killed!");
        }
    }

}
