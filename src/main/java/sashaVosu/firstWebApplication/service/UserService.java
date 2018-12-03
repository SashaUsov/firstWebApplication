package sashaVosu.firstWebApplication.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sashaVosu.firstWebApplication.converters.UserConverters;
import sashaVosu.firstWebApplication.domain.ApplicationUser;
import sashaVosu.firstWebApplication.domain.Tweet;
import sashaVosu.firstWebApplication.domain.dto.CreateUserModel;
import sashaVosu.firstWebApplication.domain.dto.TweetModel;
import sashaVosu.firstWebApplication.domain.dto.UserModel;
import sashaVosu.firstWebApplication.exception.AccessNotAllowedException;
import sashaVosu.firstWebApplication.exception.UserExistsException;
import sashaVosu.firstWebApplication.exception.UserNotFoundException;
import sashaVosu.firstWebApplication.repo.TweetRepo;
import sashaVosu.firstWebApplication.repo.UserRepo;
import sashaVosu.firstWebApplication.repo.UserTweetLikesRepo;
import sashaVosu.firstWebApplication.utils.DeleteTweetUtil;
import sashaVosu.firstWebApplication.utils.TweetReTweetUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepo userRepo;

    private final TweetRepo tweetRepo;

    private final UserTweetLikesRepo userTweetLikesRepo;

    public UserService(UserRepo userRepo,
                       TweetRepo tweetRepo,
                       UserTweetLikesRepo userTweetLikesRepo) {
        this.userRepo = userRepo;
        this.tweetRepo = tweetRepo;
        this.userTweetLikesRepo = userTweetLikesRepo;
    }

    @Value("${upload.path}")
    public String uploadPath;

    //return list of all users
    public List<UserModel> getUserList() {

        return userRepo.findAllByActive(true).stream()
                .map(UserConverters::toModel).collect(Collectors.toList());
    }

    //create new user
    public UserModel userCreate(CreateUserModel model) {

        ApplicationUser userFromDb = userRepo.findOneByNickNameAndEmail(model.getNickName(), model.getEmail());

        if (userFromDb == null) {

            ApplicationUser newUser = UserConverters.toEntity(model);

            return UserConverters.toModel(userRepo.save(newUser));

        } else {

            throw new UserExistsException("ApplicationUser with this nickname or email already exists");
        }
    }


    //delete user account and all user tweet and like from user-tweet-like table
//Deleted account cannot be recovered.
    public void deleteProfile(String nickName) {

        Long userId = userRepo.findOneByNickNameAndActive(nickName, true).getId();

        List<Tweet> tweetList = tweetRepo.findAllByCreatorAndPublished(nickName, true);

        List<Long> tweetIdList = tweetList.stream()
                .map(Tweet::getId)
                .collect(Collectors.toList());

        userTweetLikesRepo.deleteAllByTweetIdIn(tweetIdList);

        userTweetLikesRepo.deleteAllByUserId(userId);

        List<Tweet> allByFirstTweetIn = tweetRepo.findAllByPublishedAndFirstTweetIn(true, tweetList);

        List<Tweet> allReTweet = allByFirstTweetIn.stream()
                .map(DeleteTweetUtil::deactivateTweet)
                .collect(Collectors.toList());

        allReTweet.stream().map(tweetRepo::save);

        List<Tweet> deactTweet = tweetList.stream()
                .map(DeleteTweetUtil::deactivateTweet)
                .collect(Collectors.toList());

        deactTweet.stream().map(tweetRepo::save);

        ApplicationUser user = userRepo.findOneByNickNameAndActive(nickName, true);

        user.setActive(false);

        userRepo.save(user);

    }

    //get one user by user id
    public UserModel getOneUser(Long id) {

        ApplicationUser user = userRepo.findOneByIdAndActive(id, true);

        if (user != null) {

            return UserConverters.toModel(user);

        } else {

            throw new UserNotFoundException("User not found");
        }
    }

    //add avatar image to user profile
    public void addProfilePic(String nickName, MultipartFile file) throws IOException {

        ApplicationUser user = userRepo.findOneByNickNameAndActive(nickName, true);

        if (file != null) {

            File uploadDir = new File(uploadPath);

            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }

            String uuidFile = UUID.randomUUID().toString();
            String resultFileName = uuidFile + "." + file.getOriginalFilename();

            file.transferTo(new File(uploadPath + "/" + resultFileName));

            user.setFileName(resultFileName);

            userRepo.save(user);
        }
    }

    //Returns a list of tweets in which users are tagged
    public List<TweetModel> getTweetListWhereIMark(String nickName) {


        ApplicationUser marksUser = userRepo.findOneByNickNameAndActive(nickName, true);

        return marksUser.getUserMarkedTweetList().stream()
                .map(TweetReTweetUtil::convert)
                .collect(Collectors.toList());
    }
}

