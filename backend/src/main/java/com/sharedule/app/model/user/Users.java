package com.sharedule.app.model.user;


import lombok.Getter;
import lombok.Setter;


public interface Users {
    String getId();
    String getUsername();
    String getEmail();
    String getRole();
    String getDisplayPicture();
    String getPassword();
    void setId(Long id);
    boolean equals(Object obj);
}
