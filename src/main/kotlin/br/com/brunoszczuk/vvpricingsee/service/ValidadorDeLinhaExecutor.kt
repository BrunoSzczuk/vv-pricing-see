package br.com.brunoszczuk.vvpricingsee.service

import br.com.brunoszczuk.vvpricingsee.dto.LinhaPlanilhaPreco
import org.springframework.stereotype.Service

@Service
class ValidadorDeLinhaExecutor(private val validadores: List<ValidacaoDeLinha>) {
    fun validar(linha: LinhaPlanilhaPreco): List<String> {
        return validadores.flatMap { it.validar(linha) }
    }

}