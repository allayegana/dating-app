package com.datingapp.racontre.Repository;

import com.datingapp.racontre.Model.Message;
import com.datingapp.racontre.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiverOrReceiverAndSenderOrderByTimestampAsc(User sender1, User receiver1, User sender2, User receiver2);
    List<Message> findBySenderOrReceiver(User sender, User receiver);

    boolean existsBySenderAndReceiverAndIsReadFalse(User sender, User receiver);

    Message findTopBySenderOrReceiverOrderByTimestampDesc(User contact, User contact1);

    Message findFirstBySenderAndReceiverOrReceiverAndSenderOrderByTimestampDesc(User contact, User currentUser, User contact1, User currentUser1);

    Message findFirstBySenderAndReceiverOrderByTimestampDesc(User sender, User receiver);
}
