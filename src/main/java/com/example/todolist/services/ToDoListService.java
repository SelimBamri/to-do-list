package com.example.todolist.services;

import com.example.todolist.entities.ToDoList;
import com.example.todolist.repositories.ToDoListRepository;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ToDoListService {
    private final ToDoListRepository toDoListRepository;

    public ToDoList saveToDoList(ToDoList toDoList) {
        return toDoListRepository.save(toDoList);
    }

    public ToDoList getToDoListById(Long id) {
        return toDoListRepository.findById(id).orElse(null);
    }

    public void deleteToDoListById(Long id) {
        toDoListRepository.deleteById(id);
    }

    public List<ToDoList> getAllToDoLists() {
        return toDoListRepository.findAll();
    }
}
