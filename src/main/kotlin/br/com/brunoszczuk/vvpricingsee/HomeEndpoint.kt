package br.com.brunoszczuk.vvpricingsee

import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.util.UUID

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
class ResourceEndpoint(private val fileRepository: FileRepository) {

    @PostMapping("/upload")
    fun handleFileUpload(@RequestPart("file") file: MultipartFile): ResponseEntity<Resource> {
        val workbook = XSSFWorkbook(file.inputStream.buffered())
        val sheet = workbook.getSheetAt(0) // Primeira planilha
        val style = workbook.createCellStyle().apply {
            wrapText = true
        }
        for (i in 3..sheet.lastRowNum) {
            val row = sheet.getRow(i)
            val linha = LinhaDaPlanilha(
                empresa = getValueAsString(row.getCell(0)),
                bandeira = getValueAsString(row.getCell(1)),
                canal = getValueAsString(row.getCell(2)),
                prestadora = getValueAsString(row.getCell(3)),
                numCategoria = getValueAsString(row.getCell(4)),
                produtos = getValueAsString(row.getCell(5)),
                faixaInicial = getValueAsString(row.getCell(6)),
                faixaFinal = getValueAsString(row.getCell(7)),
                codigoFaixa = getValueAsString(row.getCell(8)),
                cobertura = getValueAsString(row.getCell(9)),
                deTaxaCliente = getValueAsString(row.getCell(10)),
                deTaxaPrestadora = getValueAsString(row.getCell(11)),
                deValorPrestadora = getValueAsString(row.getCell(12)),
                paraTaxaCliente = getValueAsString(row.getCell(13)),
                paraTaxaPrestadora = getValueAsString(row.getCell(14)),
                paraValorPrestadora = getValueAsString(row.getCell(15)),
                ocorrencias = ""
            )

            if (linha.empresa.isBlank()) {
                linha.ocorrencias += "Empresa não pode estar em branco.\n"
            }
            if (linha.bandeira.isBlank()) {
                linha.ocorrencias += "Bandeira não pode estar em branco.\n"
            }
            if (linha.canal.isBlank()) {
                linha.ocorrencias += "Canal não pode estar em branco.\n"
            }

            val ocorrenciasCell = row.createCell(16)
            ocorrenciasCell.setCellValue(linha.ocorrencias)
            ocorrenciasCell.cellStyle = style
            if (linha.ocorrencias.isNotBlank()) {
                ocorrenciasCell.row.height = -1;
            }

        }
        sheet.setColumnWidth(16, 100 * 256)

        val autoFilter = sheet.ctWorksheet.autoFilter
        autoFilter.ref = CellRangeAddress(2, sheet.lastRowNum, 0, 16).formatAsString()
        //filtrar por ocorrencias não vazias
        //autoFilter.


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
            CellType.NUMERIC -> cell.numericCellValue.toString()
            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            else -> ""
        }
    }
}


data class LinhaDaPlanilha(
    var empresa: String = "",
    var bandeira: String = "",
    var canal: String = "",
    var prestadora: String = "",
    var numCategoria: String = "",
    var produtos: String = "",
    var faixaInicial: String = "",
    var faixaFinal: String = "",
    var codigoFaixa: String = "",
    var cobertura: String = "",
    var deTaxaCliente: String = "",
    var deTaxaPrestadora: String = "",
    var deValorPrestadora: String = "",
    var paraTaxaCliente: String = "",
    var paraTaxaPrestadora: String = "",
    var paraValorPrestadora: String = "",
    var ocorrencias: String = ""
)


