package br.com.taok.bizu.tse.model;

import java.util.ArrayList;
import java.util.List;

public enum Eleicoes {

    ELEICAO_2018(2018, EleicoesURL.ELEICAO_2018, EleicoesCargos.CARGOS_2018),
    ELEICAO_2016(2016,EleicoesURL.ELEICAO_2016, EleicoesCargos.CARGOS_2016),
    ELEICAO_2014(2014,EleicoesURL.ELEICAO_2014, EleicoesCargos.CARGOS_2014);

    private final int anoEleicao;
    private final EleicoesURL eleicaoURL;
    private final EleicoesCargos eleicaoCargos;

    Eleicoes(int anoEleicao, EleicoesURL eleicaoURL, EleicoesCargos eleicaoCargos) {
        this.anoEleicao = anoEleicao;
        this.eleicaoURL = eleicaoURL;
        this.eleicaoCargos = eleicaoCargos;
    }

    public int getAnoEleicao() {
        return anoEleicao;
    }

    public List<String> getURLs(Localidade localidade){

        List<String> urls = new ArrayList<>();
        eleicaoCargos.getCargos().stream()
                .forEach(c -> {
                    urls.add( eleicaoURL.getUrl(localidade, c) );
                });

        return urls;
    }
    public static Eleicoes encontraPorAnoEleitora(int anoEleicao){
        for (Eleicoes eleicoes : Eleicoes.values()) {
            if( eleicoes.anoEleicao == anoEleicao ){
                return eleicoes;
            }
        }
        return null;
    }
}
