package vox.com.br.model;

import java.util.ArrayList;
import java.util.List;

public class Message {
    public List<String> tudo = new ArrayList<>();
    public List<String> mensagem = new ArrayList<>();
    public String horario;
    public String data;
    public MessageStatus status = MessageStatus.RECEBIDO;

    public void addTudo(String texto) {
        tudo.add(texto);
    }

    public void addMessage(String texto) {
        mensagem.add(texto);
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Message{" +
                "tudo=" + tudo +
                ", mensagem=" + mensagem +
                ", horario='" + horario + '\'' +
                ", data='" + data + '\'' +
                ", status=" + status +
                '}';
    }

    public enum MessageStatus {
        ENVIADO,
        RECEBIDO
    }

}

