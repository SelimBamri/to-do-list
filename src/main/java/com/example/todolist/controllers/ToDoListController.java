package com.example.todolist.controllers;

import com.example.todolist.dtos.requests.ToDoListRequest;
import com.example.todolist.entities.ToDoList;
import com.example.todolist.entities.ToDoListElement;
import com.example.todolist.entities.User;
import com.example.todolist.services.ToDoListElementService;
import com.example.todolist.services.ToDoListService;
import com.example.todolist.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/todos")
@AllArgsConstructor
public class ToDoListController {
    private final ToDoListService toDoListService;
    private final ToDoListElementService toDoListElementService;
    private final UserService userService;
    @GetMapping("/")
    public ResponseEntity<List<ToDoList>> getMyToDoLists() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUserById(((User) authentication.getPrincipal()).getId());
        return ResponseEntity.ok(currentUser.getToDoLists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getToDoListById(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUserById(((User) authentication.getPrincipal()).getId());
        ToDoList toDoList = toDoListService.getToDoListById(id);
        if(toDoList == null ||!toDoList.getUser().equals(currentUser) ){
            return ResponseEntity.badRequest().body("You don't have access to this todo list");
        }
        return ResponseEntity.ok(toDoList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteToDoList(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUserById(((User) authentication.getPrincipal()).getId());
        ToDoList toDoListToDelete = toDoListService.getToDoListById(id);
        if(toDoListToDelete == null ||!toDoListToDelete.getUser().equals(currentUser) ){
            return ResponseEntity.badRequest().body("You don't have access to delete this todo list");
        }
        toDoListService.deleteToDoListById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Todo list deleted successfully");
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/element/{id}")
    public ResponseEntity<?> deleteToDoElement(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUserById(((User) authentication.getPrincipal()).getId());
        ToDoListElement toDoListToDelete = toDoListElementService.findById(id);
        if(toDoListToDelete == null ||!toDoListToDelete.getToDoList().getUser().equals(currentUser) ){
            return ResponseEntity.badRequest().body("You don't have access to delete this todo list");
        }
        toDoListElementService.deleteById(toDoListToDelete.getId());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Todo list element deleted successfully");
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/")
    public ResponseEntity<?> createToDoList(@RequestBody ToDoListRequest toDoListRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = ((User) authentication.getPrincipal());
        ToDoList newToDoList = new ToDoList();
        newToDoList.setTitle(toDoListRequest.getTitle());
        newToDoList.setUser(currentUser);
        newToDoList = toDoListService.saveToDoList(newToDoList);
        return ResponseEntity.ok(newToDoList);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> addItem(@RequestBody ToDoListRequest toDoListRequest, @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUserById(((User) authentication.getPrincipal()).getId());
        ToDoList toDoList = toDoListService.getToDoListById(id);
        if(toDoList == null ||!toDoList.getUser().equals(currentUser) ){
            return ResponseEntity.badRequest().body("You don't have access to add to this todo list");
        }
        ToDoListElement newItem = new ToDoListElement();
        newItem.setElement(toDoListRequest.getTitle());
        newItem.setCompleted(false);
        newItem.setToDoList(toDoList);
        return ResponseEntity.ok(toDoListElementService.saveToDoListElement(newItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> checkItem(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userService.getUserById(((User) authentication.getPrincipal()).getId());
        ToDoListElement toDoListElement = toDoListElementService.findById(id);
        if(toDoListElement == null ||!toDoListElement.getToDoList().getUser().equals(currentUser) ){
            return ResponseEntity.badRequest().body("You don't have access to modify this item");
        }
        toDoListElement.setCompleted(!toDoListElement.isCompleted());
        return ResponseEntity.ok(toDoListElementService.saveToDoListElement(toDoListElement));
    }

}
