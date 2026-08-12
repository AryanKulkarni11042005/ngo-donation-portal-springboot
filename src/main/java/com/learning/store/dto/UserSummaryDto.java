package com.learning.store.dto;

public class UserSummaryDto {
    private Integer id;
    private String name;
    private String email;

    public UserSummaryDto(Integer id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    public Integer getId() {return id;}
    public String getName() {return name;}
    public String getEmail() {return email;}
}
