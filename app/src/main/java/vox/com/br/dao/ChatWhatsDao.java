package vox.com.br.dao;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

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

        for (int i = 0; i < messages.size(); i++ ) {
            Message msgIndexAtual = messages.get(i);
            boolean b = msgIndexAtual.tudo.toString().equals(message.tudo.toString());
//            Log.e(TAG, "atualizarChatComMensagem: " + b );
        }

        if (!messages.contains(message.tudo)) {
            _chat.mensagens.add(message);
//            chats.set(indexDoApp, chatWhats);
        }



    }

    public List<ChatWhats> getTodos() {
        return new ArrayList<>(chats);
    }

    public ChatWhats getChatById(long id) {
        return chats.stream()
                .filter(chat -> chat.getId() == id)
                .findFirst()
                .orElse(null);
    }

}
