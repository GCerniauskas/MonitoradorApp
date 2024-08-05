package vox.com.br.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatWhats {
    public long id;
    public String nome;
    public List<Message> mensagens = new ArrayList<>();

    public ChatWhats(long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public ChatWhats(ChatWhats chatWhats) {
        this.id = chatWhats.getId();
        this.nome = chatWhats.getNome();
        this.mensagens = chatWhats.getMensagens();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatWhats chatWhats = (ChatWhats) o;
        return Objects.equals(nome, chatWhats.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nome);
    }
}
