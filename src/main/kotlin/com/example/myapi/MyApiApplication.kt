package com.example.myapi

import org.flywaydb.core.Flyway
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.annotation.Id
import org.springframework.data.repository.CrudRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.persistence.*

@SpringBootApplication
class MyApiApplication

fun main(args: Array<String>) {
    initMigrations()
    runApplication<MyApiApplication>(*args)
}


@Entity
@Table
data class Tasks(
    val status: String,
    val description: String,
    val task_type: String,
    val summary: String,
    @javax.persistence.Id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long = 0,
)

@Repository
interface TaskRepository : CrudRepository<Tasks, Long>

@Service
class TaskService(private val taskRepository: TaskRepository) {
    fun all(): Iterable<Tasks> = taskRepository.findAll()
    fun get(id: Long): Optional<Tasks> = taskRepository.findById(id)
    fun add(tasks: Tasks): Tasks = taskRepository.save(tasks)
    fun edit(id: Long, tasks: Tasks): Tasks = taskRepository.save(tasks.copy(id = id))
    fun remove(id: Long) = taskRepository.deleteById(id)
}

@RestController
@RequestMapping("v1/api/tasks")
class TaskController(private val taskService: TaskService) {
    @GetMapping
    fun index() = taskService.all()

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody tasks: Tasks) = taskService.add(tasks)

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)

    fun read(@PathVariable id: Long): Any {
        val task = taskService.get(id)
        if (task.isEmpty) {
            return "Task not found"
        }
        return task
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody tasks: Tasks) = taskService.edit(id, tasks)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = taskService.remove(id)
}

fun initMigrations() {
    val flyway = Flyway.configure().dataSource("jdbc:postgresql:postgres", "postgres", "postgres").load()
    flyway.migrate()
}