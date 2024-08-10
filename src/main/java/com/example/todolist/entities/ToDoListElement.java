package com.example.todolist.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "todo_lists")
public class ToDoListElement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "element")
    private String element;

    @Column(name = "completed")
    private boolean isCompleted;

    @ManyToOne
    @JoinColumn(name = "todo_list_id", referencedColumnName = "id")
    private ToDoList toDoList;
}
