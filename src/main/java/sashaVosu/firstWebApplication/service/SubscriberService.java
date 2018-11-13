package sashaVosu.firstWebApplication.service;

import org.springframework.stereotype.Service;
import sashaVosu.firstWebApplication.converters.UserConverters;
import sashaVosu.firstWebApplication.domain.User;
import sashaVosu.firstWebApplication.domain.dto.UserModel;
import sashaVosu.firstWebApplication.repo.UserRepo;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SubscriberService {

    private final UserRepo userRepo;

    public SubscriberService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

//Subscribe to user
    public void subscribe(String currentUser, Long channelId) {

        User subscriber = userRepo.findOneByNickName(currentUser);

        User userChannel = userRepo.findOneById(channelId);

        userChannel.getSubscribers().add(subscriber);

        userRepo.save(userChannel);
    }

//Unsubscribe from user
    @Transactional
    public void unsubscribe(String currentUser, Long channelId) {

        User subscriber = userRepo.findOneByNickName(currentUser);

        User userChannel = userRepo.findOneById(channelId);

        subscriber.getSubscriptions().remove(userChannel);

        userRepo.save(subscriber);
    }

//Show list of followers
    public List<UserModel> subscribersList(Long userId) {

        User user = userRepo.findOneById(userId);

        Set<User> subscribersList = user.getSubscribers();

        return subscribersList.stream()
                .map(UserConverters::toModel)
                .collect(Collectors.toList());
    }

//Show list of subscriptions
    public List<UserModel> subscriptionsList(Long userId) {

        User user = userRepo.findOneById(userId);

        Set<User> subscriptionsList = user.getSubscriptions();

        return subscriptionsList.stream()
                .map(UserConverters::toModel)
                .collect(Collectors.toList());

    }
}