package br.com.brunoszczuk.vvpricingsee.dto

import org.apache.poi.ss.usermodel.Sheet
import java.time.LocalDate

data class PlanilhaPreco(
    val linhas : List<LinhaPlanilhaPreco>,
    val dataVigenciaInicial : LocalDate = LocalDate.now(),
    val dataVigenciaFinal : LocalDate = LocalDate.now().plusDays(1),
    val sheet: Sheet
){
    companion object{
        const val INDICE_INICIAL_DADOS = 3
    }
}