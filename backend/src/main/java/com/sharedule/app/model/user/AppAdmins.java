package com.sharedule.app.model.user;

import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
public class AppAdmins implements Users {
    @Id
    @Field("_id")
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    private String password;
    private String displayPicture;
    private  String role; // Fixed role for admins

    public void setId(String id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDisplayPicture(String displayPicture) {
        this.displayPicture = displayPicture;
    }


    public AppAdmins(String username, String email, String password, String role, String displayPicture) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.displayPicture = displayPicture;
    }
    @Override
    public void setId(Long id){
        this.id = id != null ? String.valueOf(id) : null;
    }
}
