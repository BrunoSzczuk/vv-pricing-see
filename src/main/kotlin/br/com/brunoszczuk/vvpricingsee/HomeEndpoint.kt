package br.com.brunoszczuk.vvpricingsee

import br.com.brunoszczuk.vvpricingsee.dto.LinhaPlanilhaPreco
import br.com.brunoszczuk.vvpricingsee.dto.PlanilhaPreco
import br.com.brunoszczuk.vvpricingsee.service.ValidadorDeLinhaExecutor
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.util.*

@Controller
@RequestMapping("/")
class HomeEndpoint {

    @GetMapping
    fun home(): String {
        return "index"
    }


}

@RestController
@RequestMapping("/api")
class ResourceEndpoint(
    private val fileRepository: FileRepository,
    private val validadorDeLinhaExecutor: ValidadorDeLinhaExecutor
) {

    @PostMapping("/upload")
    fun handleFileUpload(@RequestPart("file") file: MultipartFile): ResponseEntity<Resource> {
        val workbook = XSSFWorkbook(file.inputStream.buffered())
        val sheet = workbook.first()
        val planilhaPreco = carregaDadosDaPlanilha(sheet)


        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)
        workbook.close()

        val modifiedFile = outputStream.toByteArray()
        val resource: Resource = ByteArrayResource(modifiedFile)
        fileRepository.save(FileEntity(name = file.originalFilename!!, base64Content = modifiedFile))

        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=planilha_modificada.xlsx")
            .body(resource)
    }

    private fun carregaDadosDaPlanilha(sheet: Sheet): PlanilhaPreco {
        val linhas = mutableListOf<LinhaPlanilhaPreco>()
        for (i in PlanilhaPreco.INDICE_INICIAL_DADOS..sheet.lastRowNum) {
            val row = sheet.getRow(i)
            val ocorrenciasCell = row.createCell(LinhaPlanilhaPreco.COLUNA_OCORRENCIAS)
            ocorrenciasCell.cellStyle.wrapText = true
            try {
                val linha = LinhaPlanilhaPreco(
                    empresa = getValueAsString(row.getCell(LinhaPlanilhaPreco.COLUNA_EMPRESA)),
                    bandeira = getValueAsString(row.getCell(LinhaPlanilhaPreco.COLUNA_BANDEIRA)),
                    canal = getValueAsString(row.getCell(LinhaPlanilhaPreco.COLUNA_CANAL)),
                    prestadora = getValueAsString(row.getCell(LinhaPlanilhaPreco.COLUNA_PRESTADORA)),
                    numCategoria = getValueAsString(row.getCell(LinhaPlanilhaPreco.COLUNA_NUM_CATEGORIA)),
                    produtos = getValueAsString(row.getCell(LinhaPlanilhaPreco.COLUNA_PRODUTOS)),
                    faixaInicial = getValueAsString(row.getCell(LinhaPlanilhaPreco.COLUNA_FAIXA_INICIAL)).toBigDecimal(),
                    faixaFinal = getValueAsString(row.getCell(LinhaPlanilhaPreco.COLUNA_FAIXA_FINAL)).toBigDecimal(),
                    codigoFaixa = getValueAsString(row.getCell(LinhaPlanilhaPreco.COLUNA_CODIGO_FAIXA)),
                    cobertura = getValueAsString(row.getCell(LinhaPlanilhaPreco.COLUNA_COBERTURA)).toBigDecimal().toInt(),
                    deTaxaCliente = getValueAsString(row.getCell(LinhaPlanilhaPreco.COLUNA_DE_TAXA_CLIENTE)).toBigDecimal(),
                    deTaxaPrestadora = getValueAsString(row.getCell(LinhaPlanilhaPreco.COLUNA_DE_TAXA_PRESTADORA)).toBigDecimal(),
                    deValorPrestadora = getValueAsString(row.getCell(LinhaPlanilhaPreco.COLUNA_DE_VALOR_PRESTADORA)).toBigDecimal(),
                    paraTaxaCliente = getValueAsString(row.getCell(LinhaPlanilhaPreco.COLUNA_PARA_TAXA_CLIENTE)).toBigDecimal(),
                    paraTaxaPrestadora = getValueAsString(row.getCell(LinhaPlanilhaPreco.COLUNA_PARA_TAXA_PRESTADORA)).toBigDecimal(),
                    paraValorPrestadora = getValueAsString(row.getCell(LinhaPlanilhaPreco.COLUNA_PARA_VALOR_PRESTADORA)).toBigDecimal()
                )
                linha.ocorrencias = validadorDeLinhaExecutor.validar(linha).joinToString(separator = "\n")
                linhas.add(linha)
                ocorrenciasCell.setCellValue(linha.ocorrencias)
            } catch (e: NumberFormatException) {
                e.printStackTrace()
                ocorrenciasCell.setCellValue("Erro de em algum campo numérico. Verifique os valores informados.")
            }
            ocorrenciasCell.row.height = -1
        }
        sheet.setColumnWidth(LinhaPlanilhaPreco.COLUNA_OCORRENCIAS, 100 * 256)
        return PlanilhaPreco(linhas = linhas, sheet = sheet)
    }

    @GetMapping("/download/{id}")
    fun download(@PathVariable id: UUID): ResponseEntity<Resource> {
        val file = fileRepository.findById(id)
        return if (file.isPresent) {
            val resource: Resource = ByteArrayResource(file.get().base64Content)
            ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=${file.get().name}")
                .body(resource)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    private fun getValueAsString(cell: Cell?): String {
        if (cell == null) return ""
        return when (cell.cellType) {
            CellType.STRING -> cell.stringCellValue
            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            CellType.NUMERIC -> cell.numericCellValue.toString().onlyNumbers()
            else -> ""
        }
    }
}

private fun String.onlyNumbers(): String {
    return this.replace("[^-\\d.]".toRegex(), "")
}





