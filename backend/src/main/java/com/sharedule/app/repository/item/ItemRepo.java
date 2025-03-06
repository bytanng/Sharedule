package com.sharedule.app.repository.item;

import org.springframework.stereotype.Repository;
import com.sharedule.app.model.item.Item;
import com.sharedule.app.model.user.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepo extends MongoRepository<Item, String> {
    List<Item> findByUser(Users user);
    List<Item> findByUser_Id(String userId);
    Optional<Item> findById(String itemId);
}