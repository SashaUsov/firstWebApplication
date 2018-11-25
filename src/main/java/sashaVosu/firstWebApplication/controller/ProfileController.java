package sashaVosu.firstWebApplication.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sashaVosu.firstWebApplication.domain.dto.TweetModel;
import sashaVosu.firstWebApplication.domain.dto.UserModel;
import sashaVosu.firstWebApplication.fasades.TweetFacades;
import sashaVosu.firstWebApplication.service.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("profile")
public class ProfileController {

    private final UserService userService;

    private final TweetService tweetService;

    private final UserTweetLikesService userTweetLikesService;

    private final SubscriberService subscriberService;

    private final TweetFacades tweetFacades;

    public ProfileController(UserService userService,
                             TweetService tweetService,
                             UserTweetLikesService userTweetLikesService,
                             SubscriberService subscriberService,
                             TweetFacades tweetFacades)
    {
        this.userService = userService;
        this.tweetService = tweetService;
        this.userTweetLikesService = userTweetLikesService;
        this.subscriberService = subscriberService;
        this.tweetFacades = tweetFacades;
    }

//return list of tweet what create specific user
    @GetMapping("listOfMyTweet")
    public List<TweetModel> getMyTweetList(){
        String nickName = Utils.getNickName();

        List<TweetModel> myTweetList =  tweetService.getListOfMyTweet(nickName);

        return tweetFacades.getTweetsList(nickName, myTweetList);
    }

//return list of tweet what likes specific user
    @GetMapping("likesTweet")
    public List<TweetModel> likesTweetList () {

        String nickName = Utils.getNickName();

        return userTweetLikesService.tweetWhatLike(nickName);

    }

    @PostMapping("img")
    @ResponseStatus(HttpStatus.CREATED)
    public void addProfilePic(@RequestParam("file") MultipartFile file) throws IOException {

        String nickName = Utils.getNickName();

        userService.addProfilePic(nickName, file);

    }
//delete user account and all user and like from user-tweet-like table
//Deleted account cannot be recovered.
    @DeleteMapping("delete")
    public void deleteAccount() {

        String nickName = Utils.getNickName();

        userService.deleteProfile(nickName);
    }

//Subscribe to user
    @PostMapping("subscribe/{id}")
    public void subscribe(@PathVariable("id") Long channelId) {

        String nickName = Utils.getNickName();

        subscriberService.subscribe(nickName, channelId);
    }

//Unsubscribe from user
    @DeleteMapping("unsubscribe/{id}")
    public void unSubscribe(@PathVariable("id") Long channelId) {

        String nickName = Utils.getNickName();

        subscriberService.unsubscribe(nickName, channelId);
    }

//Show list of followers
    @GetMapping("subscribers/{id}")
    public List<UserModel> showSubscribers(@PathVariable("id") Long userId) {

        return subscriberService.subscribersList(userId);
    }

//Show list of subscriptions
    @GetMapping("subscriptions/{id}")
    public List<UserModel> showSubscriptions(@PathVariable("id") Long userId) {

        return subscriberService.subscriptionsList(userId);
    }
}
