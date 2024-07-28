package vox.com.br.model;

import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AppAberto {
    private final String nome;
    private String data;
    private Calendar calendar;
    private List<String> digitado;
    private int quantidadeDeVezesAberto = 1;
    private Duration usage_status;

    public AppAberto(String nome, String data, Calendar calendar, List<String> digitado) {
        this.nome = nome;
        this.data = data;
        this.calendar = calendar;
        this.digitado = digitado;
    }

    // Segundo construtor que atualiza quantidade de vezes usado
    public AppAberto(AppAberto appAberto, List<String> digitado , String data, Calendar calendar, int quantidadeDeVezesAberto) {
        this.nome = appAberto.nome;
        this.data = data;
        this.calendar = calendar;
        this.digitado = digitado;
        this.usage_status = appAberto.usage_status;
        this.quantidadeDeVezesAberto = quantidadeDeVezesAberto;
    }

    // Terceiro construtor que atualiza a tempo usado
    public AppAberto(AppAberto appAberto, Duration usage_status) {
        this.nome = appAberto.nome;
        this.data = appAberto.data;
        this.calendar = appAberto.calendar;
        this.digitado = appAberto.digitado;
        this.quantidadeDeVezesAberto = appAberto.quantidadeDeVezesAberto;
        this.usage_status = usage_status;
    }

    public AppAberto(AppAberto appAberto, List<String> digitado) {
        this.nome = appAberto.nome;
        this.data = appAberto.data;
        this.calendar = appAberto.calendar;
        this.digitado = digitado;
        this.quantidadeDeVezesAberto = appAberto.quantidadeDeVezesAberto;
        this.usage_status = appAberto.usage_status;
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

    public List<String> getDigitado() {
        return digitado;
    }

    public int getQuantidadeDeVezesAberto() {
        return quantidadeDeVezesAberto;
    }

    public Duration getUsage_status() {
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
