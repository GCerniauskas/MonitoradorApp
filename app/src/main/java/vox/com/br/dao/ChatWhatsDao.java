package vox.com.br.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import vox.com.br.model.ChatWhats;
import vox.com.br.model.Message;

public class ChatWhatsDao {
    private final static List<ChatWhats> chats = new ArrayList<>();

    public void salvar(ChatWhats chatWhats) {
        // Se tiver a lista estiver vazia ou o Chat novo não existe, então salva
        if (chats.isEmpty() || !chats.contains(chatWhats)) {
            chats.add(chatWhats);
        }
    }

    public void atualizarChatComMensagem(ChatWhats chatWhats, Message message) {

        // Algoritmo para saber o index do app da lista apps
        int indexDoApp = IntStream.range(0, chats.size())
                .filter(i -> chats.get(i).getNome().equals(chatWhats.getNome()))
                .findFirst()
                .orElse(-1);

        ChatWhats _chat = chats.get(indexDoApp);
        List<Message> messages = _chat.getMensagens();

        // For para verificar se as mensagens do chat é igual a mensagem atual
        for (int i = 0; i < messages.size(); i++ ) {
            Message msgIndexAtual = messages.get(i);
            boolean b = msgIndexAtual.mensagem.toString().equals(message.mensagem.toString());
            if (!b) {
                // Adiciona msg...
            }
        }

        // Sempre adiciona a mensagem...
        _chat.mensagens.add(message);

    }

    public List<ChatWhats> getTodos() {
        return new ArrayList<>(chats);
    }

}
