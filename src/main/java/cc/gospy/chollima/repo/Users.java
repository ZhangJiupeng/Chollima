package cc.gospy.chollima.repo;

import cc.gospy.chollima.util.Constant;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import static cc.gospy.chollima.util.Constant.USERS_CONFIG_PATH;

public class Users {
    private static final Logger logger = LoggerFactory.getLogger(Users.class);

    public static final Map<String, String> users = new HashMap<>();

    static {
        logger.info("Loading user configuration from {}", Constant.USERS_CONFIG_PATH);
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(USERS_CONFIG_PATH);
        if (inputStream == null) {
            logger.info("configuration file \"" + USERS_CONFIG_PATH + "\" not found, use default: <guest, guest>");
            users.put("guest", "guest");
        }
        try {
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(new InputStreamReader(inputStream));
            JsonObject object = element.getAsJsonObject();
            object.get("users").getAsJsonArray().forEach(jsonElement -> {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                users.put(jsonObject.get("username").getAsString(), jsonObject.get("password").getAsString());
            });
        } catch (Exception e) {
            logger.error("Configuration file [{}] parse failure.", USERS_CONFIG_PATH);
            e.printStackTrace();
            System.exit(-1);
        }
        logger.info("User configuration loaded ({})", users.size() + " users");
    }
}
