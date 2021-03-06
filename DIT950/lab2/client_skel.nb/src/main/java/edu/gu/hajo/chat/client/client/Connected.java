/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.gu.hajo.chat.client.client;

import edu.gu.hajo.chat.client.exception.ChatClientException;
import edu.gu.hajo.chat.client.io.FileHandler;
import edu.gu.hajo.chat.client.util.ChatClientOptions;
import edu.gu.hajo.chat.server.core.User;
import edu.gu.hajo.chat.server.io.ChatFile;
import edu.gu.hajo.chat.server.spec.IChatServer;
import edu.gu.hajo.chat.server.spec.IPeer;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;


/**
 *
 * @author Mikael
 */
class Connected implements IState {
    private final StateContext context;
    private final IChatServer server;
    private final Client client;
    
    public Connected(StateContext context, Client client, IChatServer server){
        this.context = context;
        this.server = server;
        this.client = client;
    }

    @Override
    public User connect(Client client, String login, String password) 
            throws ChatClientException
    {
        throw new ChatClientException("Already connected");
    }

    @Override
    public void disconnect(User user) {
        try {
            server.disconnect(user);
            UnicastRemoteObject.unexportObject(client, true);
        } catch (RemoteException ex) {
            // Wat? I can't has disconnect?
        }
        
        context.set(new Disconnected(context));
    }
    
    @Override
    public void send(User sender, String message){
        try {
            server.message(sender, message);
        } catch (RemoteException ex) {
            forceDisconnect();
        }
    }

    @Override
    public List<String> getFileListFromPeer(String peer) 
    {
        try {
            return server.getFilelistFromUser(peer);
        } catch (RemoteException ex) {
            forceDisconnect();
        }
        return null;
    }

    @Override
    public void download(final String filename,final  String username) 
            throws ChatClientException
    {
        final IPeer peer;
        try {
            peer = server.getUserForFile(username);
            class Downloader extends SwingWorker<Void, Object> {
                @Override
                public Void doInBackground() {
                    try {
                        ChatFile file = peer.getFile(filename);
                        FileHandler.saveFile(
                                ChatClientOptions.getDownloadPath(), 
                                filename, 
                                file.getBytes()

                        );
                    } catch (RemoteException ex) {
                        Logger.getLogger(Connected.class.getName()).log(Level.SEVERE, "Unable to download file.", ex);
                        
                        client.informUser("Unable to download file.");
                    } catch (IOException ex) {
                        Logger.getLogger(Connected.class.getName()).log(Level.SEVERE, "Unable to save file to disk.", ex);
                        
                        client.informUser("Unable to save file to disk.");
                    }
                    return null;
                }

                @Override
                protected void done() {
                    client.informUser("Download complete: "+filename);
                }
            }

            (new Downloader()).execute();
            
        } catch (RemoteException ex) {
            forceDisconnect();
        }
            
    }
    
    private void forceDisconnect() 
        throws ChatClientException
    {
        try {
            UnicastRemoteObject.unexportObject(client, true);
        } catch (Exception e) {
            // All is well.
        }
        context.set(new Disconnected(context));
        throw new ChatClientException("Unable to connect to client.");
    }
    
    
}
