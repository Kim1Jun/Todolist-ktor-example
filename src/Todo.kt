package kim.wonjun

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object TodoTable: IntIdTable() {
    val description = varchar("description", 50)
    val ownerId = integer("owner_id")
    val completed = bool("completed").default(false)
}

class Todo(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Todo>(TodoTable)

    var description by TodoTable.description
    var ownerId by TodoTable.ownerId
    var completed by TodoTable.completed
}
