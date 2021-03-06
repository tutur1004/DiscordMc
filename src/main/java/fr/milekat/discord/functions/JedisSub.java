package fr.milekat.discord.functions;

import fr.milekat.discord.Main;
import fr.milekat.discord.event.BotChat;
import redis.clients.jedis.JedisPubSub;

import java.sql.SQLException;

public class JedisSub extends JedisPubSub {

    @Override
    public void onMessage(String channel, String message) {
        if (!channel.equalsIgnoreCase("discord")) {
            String[] msg = message.split("#:#");
            BotChat bot_chat = new BotChat();
            if (Main.jedisDebug) Main.log("SUB:{"+channel+"},MSG:{"+message+"}");
            switch (msg[0].toLowerCase()) {
                case "new_msg":
                {
                    try {
                        bot_chat.newChat(Integer.parseInt(msg[1]));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    break;
                }
                case "new_mp":
                {
                    try {
                        bot_chat.sendDiscordPrivate(Integer.parseInt(msg[1]));
                    } catch (SQLException e) {
                        Main.log("Erreur dans l'envoi du message: " + e);
                        e.printStackTrace();
                    }
                    break;
                }
                case "join_notif":
                {
                    bot_chat.sendChatDiscord(":point_right: " + msg[1] + " a rejoint la cité :point_right:");
                    break;
                }
                case "quit_notif":
                {
                    bot_chat.sendChatDiscord(":wave: " + msg[1] + " a quitté la cité :wave:");
                    break;
                }
                case "log_sanction":
                {
                    if (msg.length==8) new Moderation().newSanction(msg[1],msg[2],msg[3],msg[4],msg[5],msg[6], msg[7]);
                    break;
                }
                case "sqlbackup_done":
                {
                    if (msg.length==2) SendFileToDiscord.sendNewSQLBackup(msg[1]);
                }
            }
        } else {
            if (Main.jedisDebug) Main.log("PUB:{"+message+"}");
        }
    }

    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        Main.log("Redis connecté à " + channel);
    }

    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        Main.log("Redis déconnecté de " + channel);
    }
}