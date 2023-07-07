package br.com.brunoszczuk.vvpricingsee

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Repository
import java.util.UUID

@Entity
class FileEntity(
    @Id
    val id: UUID = UUID.randomUUID(),
    val name: String,
    @Column(columnDefinition = "bytea")
    val base64Content: ByteArray
)

@Repository
interface FileRepository : JpaRepository<FileEntity, UUID> {
}


