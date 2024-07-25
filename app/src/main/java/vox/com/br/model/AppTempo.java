package vox.com.br.model;

public class AppTempo {
    public final String nome;
    public int tempoInMilliseconds;

    public AppTempo(String nome, int tempoInMilliseconds) {
        this.nome = nome;
        this.tempoInMilliseconds = tempoInMilliseconds;
    }

    public String getNome() {
        return nome;
    }

    public int getTempoInMilliseconds() {
        return tempoInMilliseconds;
    }

    @Override
    public String toString() {
        return "AppTempo{" +
                "nome='" + nome + '\'' +
                ", tempoInMilliseconds=" + tempoInMilliseconds +
                '}';
    }
}
