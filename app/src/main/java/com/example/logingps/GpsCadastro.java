package com.example.logingps;

public class GpsCadastro {


    private String id;
    private String cordenadas;
    private String cordendas2;
    private String Datahora;

    public GpsCadastro() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCordenadas() {
        return cordenadas;
    }

    public void setCordenadas(String cordenadas) {
        this.cordenadas = cordenadas;
    }

    public String getCordendas2() {
        return cordendas2;
    }

    public void setCordendas2(String cordendas2) {
        this.cordendas2 = cordendas2;
    }

    public String getDatahora() {
        return Datahora;
    }

    public void setDatahora(String datahora) {
        Datahora = datahora;
    }

    @Override
    public String toString() {
        return "Latitude=" + cordenadas + '\'' +
                "                                 Longitude='" + cordendas2 + '\'' +
                " Datahora='" + Datahora + '\'';
    }
}
