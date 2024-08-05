package vox.com.br.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ChatWhats {
    public long id;
    public String nome;
    public List<Message> mensagens = new ArrayList<>();

    public ChatWhats(long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public ChatWhats() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Message> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<Message> mensagens) {
        this.mensagens = mensagens;
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatWhats{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", mensagens=" + mensagens +
                '}';
    }

}
