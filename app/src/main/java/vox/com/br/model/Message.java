package vox.com.br.model;

import java.util.ArrayList;
import java.util.List;

public class Message {
    public long id;
    public List<String> mensagem = new ArrayList<>();


    public void addMessage(String texto) {
        mensagem.add(texto);
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", GruopView=" + mensagem +
                '}';
    }
}

