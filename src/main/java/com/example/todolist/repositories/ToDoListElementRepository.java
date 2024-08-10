package com.example.todolist.repositories;

import com.example.todolist.entities.ToDoListElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToDoListElementRepository extends JpaRepository<ToDoListElement, Long> {
}
