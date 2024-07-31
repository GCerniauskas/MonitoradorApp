package vox.com.br.dao;

import java.util.ArrayList;
import java.util.List;

import vox.com.br.model.Message;

public class MessageDao {

    private final static List<Message> messages = new ArrayList<>();

    public void salvar(Message message) {
        messages.add(message);
    }

    public List<Message> getTodos() {
        return new ArrayList<>(messages);
    }

}
