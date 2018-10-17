package slackClient;

import java.util.List;
import java.util.Observable;

import com.ullink.slack.simpleslackapi.ChannelHistoryModule;
import com.ullink.slack.simpleslackapi.SlackChannel;
import com.ullink.slack.simpleslackapi.SlackSession;
import com.ullink.slack.simpleslackapi.SlackUser;
import com.ullink.slack.simpleslackapi.events.SlackMessagePosted;
import com.ullink.slack.simpleslackapi.impl.ChannelHistoryModuleFactory;
import com.ullink.slack.simpleslackapi.listeners.SlackMessagePostedListener;

public class Connection extends Observable {
	
	public Connection(){};
	
	
	public void slackMessagePostedEventContent(SlackSession session, final String channelName){
		session.addMessagePostedListener(new SlackMessagePostedListener(){
			@Override
			public void onEvent(SlackMessagePosted event, SlackSession session1){
				// if I'm only interested on a certain channel :
                // I can filter out messages coming from other channels
                SlackChannel theChannel = session1.findChannelByName(channelName);
                
                if (!theChannel.getId().equals(event.getChannel().getId())) {
                	System.out.println(event.getMessageContent());
                    return;
                }
                // How to avoid message the bot send (yes it is receiving notification for its own messages)
                // session.sessionPersona() returns the user this session represents
                if (session1.sessionPersona().getId().equals(event.getSender().getId())) {
                    return;
                }

                setChanged();
                notifyObservers(event);
			}
		});
	}
	
	public void sendMessageToAChannel(SlackSession session)
    {
        //get a channel
        SlackChannel channel = session.findChannelByName("general");
        System.out.println("Idem posielat spravu.");
        session.sendMessage(channel, "Hey there");
    }
	
	public List<SlackMessagePosted> fetchMessagesFromChannelHistory(SlackSession session, SlackChannel channel, int numberOfMessages){
		ChannelHistoryModule channelHistoryModule = ChannelHistoryModuleFactory.createChannelHistoryModule(session);
		
		 List<SlackMessagePosted> messages = channelHistoryModule.fetchHistoryOfChannel(channel.getId(), numberOfMessages);
		 System.out.println(messages);
		 return messages;
		
	}
	
}
