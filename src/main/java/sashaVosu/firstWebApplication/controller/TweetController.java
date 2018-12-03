package sashaVosu.firstWebApplication.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sashaVosu.firstWebApplication.domain.Error;
import sashaVosu.firstWebApplication.domain.dto.CreateTweetModel;
import sashaVosu.firstWebApplication.domain.dto.TweetModel;
import sashaVosu.firstWebApplication.domain.dto.UserModel;
import sashaVosu.firstWebApplication.fasades.TweetFacades;
import sashaVosu.firstWebApplication.utils.Utils;
import sashaVosu.firstWebApplication.service.ReTweetService;
import sashaVosu.firstWebApplication.service.TweetService;
import sashaVosu.firstWebApplication.service.UserTweetLikesService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("tweet")
public class TweetController {

    private final TweetService tweetService;

    private final UserTweetLikesService userTweetLikesService;

    private final TweetFacades tweetFacades;

    public TweetController(TweetService tweetService,
                           UserTweetLikesService userTweetLikesService,
                           TweetFacades tweetFacades
    ) {
        this.tweetService = tweetService;
        this.userTweetLikesService = userTweetLikesService;
        this.tweetFacades = tweetFacades;
    }

    //return list of all tweets
    @GetMapping
    private List<TweetModel> listOfTweets() {

        String nickName = Utils.getNickName();

        List<TweetModel> modelList = tweetService.getTweetsList();

        return tweetFacades.getTweetsList(nickName, modelList);
    }

    @GetMapping("{id}")
    public TweetModel getOneTweet(@PathVariable("id") Long id) {

        String nickName = Utils.getNickName();

        TweetModel model = tweetService.getOne(id);

        return userTweetLikesService.likeStatistic(model, nickName);
    }

    //return TweetModel to the user after creating new tweet
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TweetModel createTweet(@RequestBody CreateTweetModel model
    ) {
        String nickName = Utils.getNickName();

        return tweetService.tweetCreate(model, nickName);
    }

    //update one tweet by tweet id
    @PutMapping("{id}")
    public TweetModel updateTweet(@PathVariable("id") Long id,
                                  @RequestBody CreateTweetModel model) {
        String nickName = Utils.getNickName();

        return tweetService.update(model, nickName, id);
    }

    //delete one tweet by tweet id
    @DeleteMapping("{id}")
    public void delete(@PathVariable("id") Long id) {
        String nickName = Utils.getNickName();

        tweetService.del(id, nickName);
    }

    //Get the name of the picture added by the user to the tweet
    @PostMapping("pic")
    public String addTweetPic(@RequestParam("file") MultipartFile file) throws IOException {

        return tweetService.addTweetPic(file);
    }


    @ExceptionHandler
    public Error handleException(Exception e) {
        e.printStackTrace();
        return new Error(e.getMessage());
    }
}
