/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.logback.xmpp;

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.sun.org.apache.xpath.internal.operations.Mult;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.RoomInfo;

/**
 * XMPP appender for logback.
 */
public final class XmppAppender extends AppenderBase<LoggingEvent> {

    private MultiUserChat chat;
    private String server;
    private String user;
    private String password;
    private String channel;
    private String nick;
    private String channelPassword;

    @Override
    public void start() {
        if (chat == null) {
            try {
                final XMPPConnection connection = new XMPPConnection(server);
                connection.connect();
                connection.login(user, password);

                chat = new MultiUserChat(connection, channel);
                if (roomExists(connection)) {

                    if (nick == null) {
                        if (channelPassword == null) {
                            chat.join(user);
                        } else {
                            chat.join(user, channelPassword);
                        }
                    } else {
                        if (channelPassword == null) {
                            chat.join(nick);
                        } else {
                            chat.join(nick, channelPassword);
                        }
                    }
                } else {
                    if (nick == null) {
                        chat.create(user);
                    } else {
                        chat.create(nick);
                    }
                    chat.sendConfigurationForm(new Form(Form.TYPE_SUBMIT));
                }

            } catch (XMPPException e) {
                addError(e.getMessage());
                return;
            }
        }
        super.start();
    }

    private boolean roomExists(XMPPConnection connection) {
        try {
            MultiUserChat.getRoomInfo(connection, channel);
        } catch (XMPPException e) {
            return false;
        }
        return true;
    }

    @Override
    protected void append(final LoggingEvent eventObject) {
        try {
            chat.sendMessage(eventObject.getFormattedMessage());
        } catch (XMPPException ignored) {
            chat = null;
            addError(ignored.getMessage());
        }
    }

    public String getServer() {
        return server;
    }

    public void setServer(final String server) {
        this.server = server;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(final String channel) {
        this.channel = channel;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(final String nick) {
        this.nick = nick;
    }

    public String getChannelPassword() {
        return channelPassword;
    }

    public void setChannelPassword(final String channelPassword) {
        this.channelPassword = channelPassword;
    }
}
