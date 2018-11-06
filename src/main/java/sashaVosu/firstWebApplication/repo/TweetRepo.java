package sashaVosu.firstWebApplication.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sashaVosu.firstWebApplication.domain.Tweet;

public interface TweetRepo extends JpaRepository<Tweet, Long> {

    Boolean existsByCreatorAndId(String nickName, Long id);
}