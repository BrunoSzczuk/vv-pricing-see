package br.com.brunoszczuk.vvpricingsee.service

import br.com.brunoszczuk.vvpricingsee.dto.LinhaPlanilhaPreco

fun interface ValidacaoDeLinha {
    fun validar(linha: LinhaPlanilhaPreco): List<String>
}