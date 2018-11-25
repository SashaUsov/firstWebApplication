package sashaVosu.firstWebApplication.controller;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sashaVosu.firstWebApplication.domain.Error;
import sashaVosu.firstWebApplication.domain.dto.CreateUserModel;
import sashaVosu.firstWebApplication.domain.dto.UserModel;
import sashaVosu.firstWebApplication.service.UserService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("account")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

//return list of all users
    @GetMapping("list")
    private List<UserModel> listOfUser() {

        return userService.getUserList();
    }

//create new user
    @PostMapping("create")
    @ResponseStatus(HttpStatus.CREATED)
    public UserModel createUser(@RequestBody CreateUserModel model) {

        return userService.userCreate(model);
    }

    @GetMapping("{id}")
    public UserModel getOneUser(@PathVariable("id") Long id) {

        return userService.getOneUser(id);
    }

    @ExceptionHandler
    public Error handleException(Exception e) {
        e.printStackTrace();
        return new Error(e.getMessage());
    }
}
