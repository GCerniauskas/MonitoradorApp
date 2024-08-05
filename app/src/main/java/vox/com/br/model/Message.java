package vox.com.br.model;

import java.util.ArrayList;
import java.util.List;

public class Message {
    public long id ;
    public List<String> tudo = new ArrayList<>();
    public String texto ;
    public String horario;
    public String data ;
    public static Estado estado;

    public enum Estado {
        ENVIADO,
        RECEBIDO
    }

    public String getTexto() {
        return texto;
    }

    public String getHorario() {

        return horario;
    }

    public String getData() {
        return data;
    }

    public Message.Estado getEstado() {
        return estado;
    }

    public long getId() {
        return id;
    }

    public void addMessage(String texto) {
        tudo.add(texto);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setEstado(Message.Estado estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", tudo=" + tudo +
                ", texto='" + texto + '\'' +
                ", horario='" + horario + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}

