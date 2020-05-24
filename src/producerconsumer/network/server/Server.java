package producerconsumer.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.BlockingQueue;
import producerconsumer.GUIDesignFrame;


public class Server {
    private ServerSocket serverSocket;
    private ConcurrentHashMap<InetAddress, Socket> socketsMap;
    private ServerBuffer buffer;
    private Semaphore semaphore;
    private boolean running;
    private GUIDesignFrame gui;
    private ArrayList<String> dictionary;
    
    private final BlockingQueue<ServerProducer> idleProducers;
    private final BlockingQueue<ServerConsumer> idleConsumers;

    public Server(GUIDesignFrame gui, ArrayList<String> dictionary) {
        this.gui = gui;
        socketsMap = new ConcurrentHashMap<>();
        idleProducers = new LinkedBlockingQueue<>();
        idleConsumers = new LinkedBlockingQueue<>();
        try
        {
            serverSocket = new ServerSocket(8081);
        }
        catch(IOException e)
        {
            System.out.println(e.getMessage());
        }
        this.dictionary = dictionary;
    }
    
    public void listen(){
        new Thread(){
            @Override
            public void run() {
                try
                {
                    System.out.println("Server Started!");
                    // if the program runs it stops to accept new connections;
                    while(!running)
                    {
                        Socket socket = serverSocket.accept();
                        gui.addClient(socket);
                        socketsMap.put(socket.getInetAddress(), socket);
                        System.out.println("New client: " + socket.getInetAddress().toString());
                    }
                }
                catch(IOException e)
                {
                    System.out.println(e.getMessage());
                }
                catch(NullPointerException e){
                    System.out.println("Server closed");
                }
            }
            
        }.start();
    }

    public void run(int bufferSize, int consumers, int producers, GUIDesignFrame gui, int producerWait, int consumerWait, int min, int max){
        this.buffer = new ServerBuffer(bufferSize, gui);
        semaphore = new Semaphore(bufferSize);
        running = true;
        
        socketsMap.values().forEach((socket) -> {
            new Connection(socket, socketsMap, idleProducers, idleConsumers, consumers, producers, buffer, producerWait, consumerWait, min, max, dictionary, gui).start();
        }); 
        
        //check for socketsMap size. If empty, then start offline mode

        // Producer Manager
        new Thread(){
            @Override
            public void run() {
                System.out.println("++++++Started producer manager.");
                while(running){
                    try{
                        boolean flag = semaphore.tryAcquire();
                        if(flag){
                            System.out.println("acquired");
                            idleProducers.take().produce();
                        }
                        Thread.sleep(100);
                    } catch(InterruptedException ex){
                        System.out.println(ex);
                    }
                    catch(IOException e) {
                        System.out.println(e);
                    }
                }
                System.out.println("++++++Producer manager stopped.");
            }
        }.start();

        // Consumer Manager
        new Thread(){
            @Override
            public void run() {
                System.out.println("++++++Started Consumer manager.");
                while(running){
                    try{
                        idleConsumers.take().consume(buffer.consume());
                        semaphore.release();
                        System.out.println("release");
                        Thread.sleep(100);
                    }
                    catch(InterruptedException e){
                        System.out.println(e);
                    }
                    catch(IOException e) {
                        System.out.println(e);
                    }
                }
                System.out.println("++++++Consumer manager stopped.");
            }
        }.start();
           
    }
    
    public boolean runServer(int bufferSize, int consumers, int producers, GUIDesignFrame gui, int producerWait, int consumerWait, int min, int max) {
        if(!this.socketsMap.isEmpty()){
            running = true;
            run(bufferSize, consumers, producers, gui, producerWait, consumerWait, min, max);
            return true;
        }
        return false;
        
    }
    
    public void stop() {
        try {
            if(serverSocket != null){
                serverSocket.close();
            }
            running = false;
            
            //return true;
        } catch (IOException ex) {
            System.out.println(ex);
        }
        
        //return false;
    }
    
    public Socket[] getSockets(){
        Object[] objects = this.socketsMap.values().toArray();
        Socket[] sockets = new Socket[objects.length];
        for(int object = 0; object < objects.length; object++) {
            sockets[object] = (Socket) objects[object];
        }
        return sockets;
    }

}