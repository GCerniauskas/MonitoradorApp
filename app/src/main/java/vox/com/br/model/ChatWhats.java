package vox.com.br.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatWhats {
    public String nome;
    public List<Message> mensagens = new ArrayList<>();

    public ChatWhats(String nome) {
        this.nome = nome;
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

    @NonNull
    @Override
    public String toString() {
        return "ChatWhats{" +
                "nome='" + nome + '\'' +
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
