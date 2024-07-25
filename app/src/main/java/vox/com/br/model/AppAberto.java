package vox.com.br.model;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AppAberto {
    private final String nome;
    private String data;
    private Calendar calendar;
    private List<CharSequence> digitado;
    private int quantidadeDeVezesAberto = 1;
    private Date usage_status;

    public AppAberto(String nome, String data, Calendar calendar, List<CharSequence> digitado) {
        this.nome = nome;
        this.data = data;
        this.calendar = calendar;
        this.digitado = digitado;
    }

    // Segundo construtor que atualiza quantidade de vezes usado
    public AppAberto(AppAberto appAberto, int quantidadeDeVezesAberto) {
        this.nome = appAberto.nome;
        this.data = appAberto.data;
        this.calendar = appAberto.calendar;
        this.digitado = appAberto.digitado;
        this.quantidadeDeVezesAberto = quantidadeDeVezesAberto;
    }

    // Terceiro construtor que atualiza a tempo usado
    public AppAberto(AppAberto appAberto, Date usage_status) {
        this.nome = appAberto.nome;
        this.data = appAberto.data;
        this.calendar = appAberto.calendar;
        this.digitado = appAberto.digitado;
        this.quantidadeDeVezesAberto = appAberto.quantidadeDeVezesAberto;
        this.usage_status = usage_status;
    }



    // Getters
    public String getNome() {
        return nome;
    }

    public String getData() {
        return data;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public List<CharSequence> getDigitado() {
        return digitado;
    }

    public int getQuantidadeDeVezesAberto() {
        return quantidadeDeVezesAberto;
    }

    public Date getUsage_status() {
        return usage_status;
    }

    @Override
    public String toString() {
        return "Aplicativo: " + nome + ", Horário: " + data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        AppAberto appAberto = (AppAberto) o;
        if (Objects.equals(nome, appAberto.nome)) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return Objects.equals(nome, appAberto.nome) && Objects.equals(data, appAberto.data) && Objects.equals(calendar, appAberto.calendar) && Objects.equals(digitado, appAberto.digitado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, data, calendar, digitado);
    }
}
