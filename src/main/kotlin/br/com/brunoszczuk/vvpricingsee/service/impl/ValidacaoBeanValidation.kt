package br.com.brunoszczuk.vvpricingsee.service.impl

import br.com.brunoszczuk.vvpricingsee.dto.LinhaPlanilhaPreco
import br.com.brunoszczuk.vvpricingsee.service.ValidacaoDeLinha
import jakarta.validation.Validator
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service

@Service
@Order(1)
class ValidacaoBeanValidation(private val validator: Validator) : ValidacaoDeLinha {

    override fun validar(linha: LinhaPlanilhaPreco): List<String> {
        val violations = validator.validate(linha)
        return violations.map { it.message }
    }
}