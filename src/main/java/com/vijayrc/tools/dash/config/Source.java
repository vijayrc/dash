package com.vijayrc.tools.dash.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vijay Chakravarthy
 * @version %I% %G%
 * @since 1.0
 */
public class Source {
    private String name;
    private String link;
    private String host;
    private String port;
    private String username;
    private String password;
    private int poll;
    private int duration;
    private boolean flatten;

    public Source(String name, String link, String poll, String duration, String username, String password, boolean flatten) {
        this.name = name;
        this.link = link;
        this.username = username;
        this.password = password;
        this.flatten = flatten;
        this.poll = Integer.parseInt(poll);
        this.duration = Integer.parseInt(duration);
        setHostAndPort();
    }

    private void setHostAndPort() {
        String ipPattern = "(http[s]?://)(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}):(\\d+)/(.*)";      
        String hostPattern = "(http[s]?://)([\\w\\.\\-]+):(\\d+)/(.*)";
        Pattern pattern = Pattern.compile(hostPattern + "|" + ipPattern);
        Matcher matcher = pattern.matcher(link);
        if (matcher.matches()) {
            this.host = matcher.group(2);
            this.port = matcher.group(3);
        }
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    public int getPoll() {
        return poll;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getDuration() {
        return duration;
    }

    public boolean isFlatten() {
        return flatten;
    }

    @Override
    public String toString() {
        return "Source{" +
                "name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", poll=" + poll +
                ", duration=" + duration +
                ", flatten=" + flatten +
                '}';
    }

}
