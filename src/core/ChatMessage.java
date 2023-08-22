package core;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ChatMessage implements Serializable {
private String name;
private String text;
private Set<String> setOnlines = new HashSet<String>();
private Action action;

public enum Action {
	CONNECT, DISCONNECT, SEND_ALL, USER_ONLINE
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getText() {
	return text;
}

public void setText(String text) {
	this.text = text;
}

public Set<String> getSetOnlines() {
	return setOnlines;
}

public void setSetOnlines(Set<String> setOnlines) {
	this.setOnlines = setOnlines;
}

public Action getAction() {
	return action;
}

public void setAction(Action action) {
	this.action = action;
}
}
