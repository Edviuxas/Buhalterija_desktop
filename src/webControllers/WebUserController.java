package webControllers;

import GSONSerializable.CategoryGSONSerializer;
import GSONSerializable.UserGSONSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import controller.DatabaseController;
import ds.Category;
import ds.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Controller
public class WebUserController {
    @RequestMapping(value = "users/getAll")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getAllUsers() {
        ArrayList<User> allUsers = DatabaseController.getAllUsers();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(User.class, new UserGSONSerializer());
        Gson parser = gsonBuilder.create();
        parser.toJson(allUsers.get(0));

        Type userList = new TypeToken<List<User>>() {
        }.getType();
        gsonBuilder.registerTypeAdapter(userList, new CategoryGSONSerializer());
        parser = gsonBuilder.create();

        return parser.toJson(allUsers);
    }

    @RequestMapping(value = "users/delete", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String deleteUser(@RequestParam("id") int id) {
        if (id != 2)  { // if not admin user
            try {
                DatabaseController.deleteUser(id);
            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }
        }
        return "User was removed successfully";
    }

    @RequestMapping(value = "users/add", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String addUser(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String username = data.getProperty("username");
        String password = data.getProperty("password");
        boolean isCompanyUser = Boolean.getBoolean(data.getProperty("isCompanyUser"));
        String name = data.getProperty("name");
        String contactPerson = null;
        String surname = null;
        String emailAddress = null;
        if (!data.getProperty("contactPerson").equals("")) {
            contactPerson = data.getProperty("contactPerson");
        }
        if (!data.getProperty("surname").equals("")) {
            surname = data.getProperty("surname");
        }
        if (!data.getProperty("emailAddress").equals("")) {
            emailAddress = data.getProperty("emailAddress");
        }

        try {
            DatabaseController.addUser(username, password, isCompanyUser, name, contactPerson, surname, emailAddress);
        } catch (Exception e) {
            return "Error";
        }
        return "Successfully added user";
    }

    @RequestMapping(value = "users/update", method = RequestMethod.PUT)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String updateUser(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        int id = Integer.parseInt(data.getProperty("userID"));
        String username = data.getProperty("username");
        String password = data.getProperty("password");
//        boolean isCompanyUser = Boolean.getBoolean(data.getProperty("isCompanyUser"));
//        String name = data.getProperty("name");
//        String contactPerson = null;
//        String surname = null;
//        String emailAddress = null;
//        if (!data.getProperty("contactPerson").equals("")) {
//            contactPerson = data.getProperty("contactPerson");
//        }
//        if (!data.getProperty("surname").equals("")) {
//            surname = data.getProperty("surname");
//        }
//        if (!data.getProperty("emailAddress").equals("")) {
//            emailAddress = data.getProperty("emailAddress");
//        }

        try {
//            DatabaseController.updateUser(id, username, password, isCompanyUser, name, contactPerson, surname, emailAddress);
            DatabaseController.updateUser(id, username, password);
        } catch (Exception e) {
            return "Error";
        }
        return "Successfully updated user";
    }

    @RequestMapping(value = "users/login", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String validateUser(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        String username = data.getProperty("username");
        String password = data.getProperty("password");
        System.out.println(username + " " + password);
        User user = DatabaseController.validateUser(username, password);
        if (user != null) {
            return user.getId() + " " + user.getUserName() + " " + user.getPassword();
        } else {
            return "Validation error";
        }
    }

    @RequestMapping(value = "users/getUsernamePassword", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String getUsernamePassword(@RequestBody String request) {
        Gson parser = new Gson();
        Properties data = parser.fromJson(request, Properties.class);
        int userID = Integer.parseInt(data.getProperty("userID"));
        String usernamePassword = DatabaseController.getUsernamePassword(userID);
        if (usernamePassword != null) {
            return usernamePassword;
        } else {
            return "Error";
        }
    }
}
