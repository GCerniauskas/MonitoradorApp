package vox.com.br.model;

public class Message {
    public String texto;
    public String horario;
    public String data;
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
                "texto='" + texto + '\'' +
                ", horario='" + horario + '\'' +
                ", data='" + data + '\'' +
                ", estado=" + estado +
                '}';
    }

}

