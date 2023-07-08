package br.com.brunoszczuk.vvpricingsee.dto

import jakarta.validation.constraints.*
import java.math.BigDecimal

data class LinhaPlanilhaPreco(
    @field:NotEmpty(message = "Empresa não pode estar em branco.")
    val empresa: String = "",
    @field:NotEmpty(message = "Bandeira não pode estar em branco.")
    val bandeira: String = "",
    @field:NotEmpty(message = "Canal não pode estar em branco.")
    val canal: String = "",
    @field:NotEmpty(message = "Prestadora não pode estar em branco.")
    val prestadora: String = "",
    @field:NotEmpty(message = "Número da categoria não pode estar em branco.")
    val numCategoria: String = "",
    @field:NotEmpty(message = "Produtos não pode estar em branco.")
    val produtos: String = "",
    @field:Positive(message = "Faixa inicial deve ser maior que zero.")
    val faixaInicial: BigDecimal = BigDecimal.valueOf(0.1),
    @field:Positive(message = "Faixa final deve ser maior que zero.")
    val faixaFinal: BigDecimal = BigDecimal.valueOf(0.1),
    @field:NotEmpty(message = "Código da faixa não pode estar em branco.")
    val codigoFaixa: String = "",
    @field:Min(value = 1, message = "Cobertura deve ser maior que zero.")
    val cobertura: Int = 0,
    @field:PositiveOrZero(message = "De taxa cliente deve ser maior ou igual a zero.")
    val deTaxaCliente: BigDecimal = BigDecimal.ZERO,
    @field:PositiveOrZero(message = "De taxa prestadora deve ser maior ou igual a zero.")
    val deTaxaPrestadora: BigDecimal = BigDecimal.ZERO,
    @field:PositiveOrZero(message = "De valor prestadora deve ser maior ou igual a zero.")
    val deValorPrestadora: BigDecimal = BigDecimal.ZERO,
    @field:PositiveOrZero(message = "Para taxa cliente deve ser maior ou igual a zero.")
    val paraTaxaCliente: BigDecimal = BigDecimal.ZERO,
    @field:PositiveOrZero(message = "Para taxa prestadora deve ser maior ou igual a zero.")
    val paraTaxaPrestadora: BigDecimal = BigDecimal.ZERO,
    @field:PositiveOrZero(message = "Para valor prestadora deve ser maior ou igual a zero.")
    val paraValorPrestadora: BigDecimal = BigDecimal.ZERO,
    var ocorrencias: String = ""
) {
    @AssertTrue(message = "Faixa final deve ser maior que faixa inicial.")
    fun isFaixaFinalMaiorQueInicial(): Boolean {
        return faixaFinal > faixaInicial
    }

    @AssertTrue(message = "Para taxa cliente deve ser menor ou igual que De taxa de cliente.")
    fun isParaTaxaClienteDeMaiorOuIgualADeTaxaDeCliente(): Boolean {
        return paraTaxaCliente <= deTaxaCliente
    }

    @AssertTrue(message = "Para taxa prestadora deve ser maior ou igual que De taxa de prestadora.")
    fun isParaTaxaDePrestadoraMaiorOuIgualADeTaxaPrestadora(): Boolean {
        return paraTaxaPrestadora >= deTaxaPrestadora
    }

    @AssertTrue(message = "Para valor prestadora deve ser maior ou igual que De valor de prestadora.")
    fun isParaValorDePrestadoraMaiorOuIgualADeValorPrestadora(): Boolean {
        return paraValorPrestadora >= deValorPrestadora
    }


    companion object {
        const val COLUNA_EMPRESA: Int = 0
        const val COLUNA_BANDEIRA: Int = 1
        const val COLUNA_CANAL: Int = 2
        const val COLUNA_PRESTADORA: Int = 3
        const val COLUNA_NUM_CATEGORIA: Int = 4
        const val COLUNA_PRODUTOS: Int = 5
        const val COLUNA_FAIXA_INICIAL: Int = 6
        const val COLUNA_FAIXA_FINAL: Int = 7
        const val COLUNA_CODIGO_FAIXA: Int = 8
        const val COLUNA_COBERTURA: Int = 9
        const val COLUNA_DE_TAXA_CLIENTE: Int = 10
        const val COLUNA_DE_TAXA_PRESTADORA: Int = 11
        const val COLUNA_DE_VALOR_PRESTADORA: Int = 12
        const val COLUNA_PARA_TAXA_CLIENTE: Int = 13
        const val COLUNA_PARA_TAXA_PRESTADORA: Int = 14
        const val COLUNA_PARA_VALOR_PRESTADORA: Int = 15
        const val COLUNA_OCORRENCIAS: Int = 16

    }
}