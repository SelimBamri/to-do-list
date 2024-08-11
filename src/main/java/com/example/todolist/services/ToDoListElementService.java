package com.example.todolist.services;

import com.example.todolist.entities.ToDoListElement;
import com.example.todolist.repositories.ToDoListElementRepository;
import lombok.*;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ToDoListElementService {
    private final ToDoListElementRepository toDoListElementRepository;

    public ToDoListElement saveToDoListElement(ToDoListElement toDoListElement) {
        return toDoListElementRepository.save(toDoListElement);
    }

    public ToDoListElement findById(Long id) {
        return toDoListElementRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        toDoListElementRepository.deleteById(id);
    }
}
