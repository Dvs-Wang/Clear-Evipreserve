package AuditServer.utils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import sample.Module.Share.Massage.Message;

/**
 * Created by WangMingming on 2017/3/11.
 */
public class ClientHandler extends SimpleChannelInboundHandler<Message> {
    private ObjectProperty<Message> overWriteMsg;
    public ClientHandler(ObjectProperty<Message> overWriteMsg){
        this.overWriteMsg = overWriteMsg;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg){
        Message message = (Message)msg;
        switch (message.getType()){
            default:
                Platform.runLater(()-> overWriteMsg.set(message));
                break;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client active :");
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client close ：");
        super.channelInactive(ctx);
    }
}
