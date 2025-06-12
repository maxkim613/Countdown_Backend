package back.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import back.model.user.UserImg;
import back.service.user.UserImgService;

@RestController
@RequestMapping("/api/userImg")
public class UserImgController {

    @Autowired
    private UserImgService userImgService;

    @GetMapping("/{userId}")
    public UserImg getUserImg(@PathVariable String userId) {
        return userImgService.getUserImgByUserId(userId);
    }

    @PostMapping
    public void uploadUserImg(@RequestBody UserImg userImg) {
        userImgService.uploadUserImg(userImg);
    }

    @PutMapping
    public void updateUserImg(@RequestBody UserImg userImg) {
        userImgService.updateUserImg(userImg);
    }

    @DeleteMapping("/{userImgId}")
    public void deleteUserImg(@PathVariable Long userImgId) {
        userImgService.deleteUserImg(userImgId);
    }
}
