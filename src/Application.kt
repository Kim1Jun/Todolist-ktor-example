package kim.wonjun

import com.fasterxml.jackson.databind.*
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import kim.wonjun.exception.TodoException
import kim.wonjun.reqeust.CreateTodoRequest
import kim.wonjun.reqeust.ModifyTodoRequest
import kim.wonjun.response.BaseResponse
import kim.wonjun.response.TodoResponse
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.event.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Get)
        method(HttpMethod.Post)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        allowCredentials = true
        anyHost()
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, "HELLO WORLD!")
        }

        get("/todos") {
            val list = transaction {
                Todo.find { TodoTable.ownerId eq 1 }.toList()
            }

            call.respond(HttpStatusCode.OK, BaseResponse.ok(list.map(TodoResponse::from)))
        }

        post("/todo") {
            val req = call.receive<CreateTodoRequest>()

            val newTodo = transaction {
                Todo.new {
                    description = req.description
                    weight = req.weight
                    ownerId = 1
                }
            }

            call.respond(HttpStatusCode.Created, BaseResponse.ok(TodoResponse.from(newTodo)))
        }

        patch("/todo/{id}") {
            val id = (call.parameters["id"]?.toInt())
            val req = call.receive<ModifyTodoRequest>()

            if (id != null) {
                val todo = transaction {
                    val todo = Todo.findById(id)

                    req.description?.let { todo?.description = req.description }
                    req.weight?.let { todo?.weight = req.weight }
                    req.completed?.let { todo?.completed = req.completed }

                    return@transaction todo
                }

                call.respond(HttpStatusCode.Created, BaseResponse.ok(TodoResponse.from(todo)))
            } else {
                call.respond(HttpStatusCode.NotFound, BaseResponse.error(TodoException))
            }
        }

        delete("/todo/{id}") {
            val id = (call.parameters["id"]?.toInt())

            if (id != null) {
                transaction {
                    val todo = Todo.findById(id)
                    todo?.delete()
                }
                call.respond(HttpStatusCode.OK, BaseResponse.ok(null))
            } else {
                call.respond(HttpStatusCode.NotFound, BaseResponse.error(TodoException))
            }
        }
    }

    val db =
        Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver", user = "sa", password = "sa")

    transaction(db) {
        SchemaUtils.create(TodoTable)
    }
}
