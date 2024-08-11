package com.example.todolist.controllers;

import com.example.todolist.dtos.requests.ToDoListRequest;
import com.example.todolist.entities.ToDoList;
import com.example.todolist.entities.ToDoListElement;
import com.example.todolist.entities.User;
import com.example.todolist.services.ToDoListElementService;
import com.example.todolist.services.ToDoListService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
@AllArgsConstructor
public class ToDoListController {
    private final ToDoListService toDoListService;
    private final ToDoListElementService toDoListElementService;
    @GetMapping("/")
    public ResponseEntity<List<ToDoList>> getMyToDoLists() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser.getToDoLists());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteToDoList(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        ToDoList toDoListToDelete = toDoListService.getToDoListById(id);
        if(toDoListToDelete == null ||!toDoListToDelete.getUser().equals(currentUser) ){
            return ResponseEntity.badRequest().body("You don't have access to delete this todo list");
        }
        toDoListService.deleteToDoListById(id);
        return ResponseEntity.ok().body("Todo list deleted successfully");
    }

    @DeleteMapping("/element/{id}")
    public ResponseEntity<?> deleteToDoElement(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        ToDoListElement toDoListToDelete = toDoListElementService.findById(id);
        if(toDoListToDelete == null ||!toDoListToDelete.getToDoList().getUser().equals(currentUser) ){
            return ResponseEntity.badRequest().body("You don't have access to delete this todo list");
        }
        toDoListElementService.saveToDoListElement(toDoListToDelete);
        return ResponseEntity.ok().body("Todo list Element deleted successfully");
    }

    @PostMapping("/")
    public ResponseEntity<?> createToDoList(@RequestBody ToDoListRequest toDoListRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        ToDoList newToDoList = new ToDoList();
        newToDoList.setTitle(toDoListRequest.getTitle());
        newToDoList.setUser(currentUser);
        newToDoList = toDoListService.saveToDoList(newToDoList);
        return ResponseEntity.ok(newToDoList);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> addItem(@RequestBody ToDoListRequest toDoListRequest, @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        ToDoList toDoList = toDoListService.getToDoListById(id);
        if(toDoList == null ||!toDoList.getUser().equals(currentUser) ){
            return ResponseEntity.badRequest().body("You don't have access to delete this todo list");
        }
        ToDoListElement newItem = new ToDoListElement();
        newItem.setElement(toDoListRequest.getTitle());
        newItem.setCompleted(false);
        return ResponseEntity.ok(newItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> checkItem(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        ToDoListElement toDoListElement = toDoListElementService.findById(id);
        if(toDoListElement == null ||!toDoListElement.getToDoList().getUser().equals(currentUser) ){
            return ResponseEntity.badRequest().body("You don't have access to modify this item");
        }
        toDoListElement.setCompleted(!toDoListElement.isCompleted());
        return ResponseEntity.ok(toDoListElement);
    }

}
