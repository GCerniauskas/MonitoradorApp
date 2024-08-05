package vox.com.br.dao;

import java.util.ArrayList;
import java.util.List;

import vox.com.br.model.ChatWhats;

public class ChatWhatsDao {
    private final static List<ChatWhats> chats = new ArrayList<>();

    public void salvar(ChatWhats chatWhats) {
        // Se tiver a lista estiver vazia ou o Chat novo não existe, então salva
        if (chats.isEmpty() || !chats.contains(chatWhats)) {
            chats.add(chatWhats);
        }
    }

    public void atualizarChat(ChatWhats chatWhats) {

    }

    public List<ChatWhats> getTodos() {
        return new ArrayList<>(chats);
    }

}
