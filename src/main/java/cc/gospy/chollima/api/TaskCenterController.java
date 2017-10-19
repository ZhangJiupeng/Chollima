package cc.gospy.chollima.api;

import cc.gospy.chollima.entity.Message;
import cc.gospy.chollima.entity.bean.Spider;
import cc.gospy.chollima.entity.bean.TaskGroup;
import cc.gospy.chollima.service.ComponentAssemblyService;
import cc.gospy.chollima.service.SpiderService;
import cc.gospy.chollima.service.TaskService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping("/task-center/**")
public class TaskCenterController {
    private static JsonParser jsonParser = new JsonParser();

    @Autowired
    private TaskService taskService;

    @Autowired
    private ComponentAssemblyService assemblyService;

    @Autowired
    private SpiderService spiderService;

    @RequestMapping(value = "/task-group/{name}", method = POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String addTaskGroup(@PathVariable("name") String name, @RequestBody String json) {
        try {
            String urlPattern = jsonParser.parse(json).getAsJsonObject().get("urlPattern").getAsString();
            TaskGroup taskGroup = taskService.addTaskGroup(name, urlPattern);
            int count = taskService.getTaskCountByGroupId(taskGroup.getId());
            JsonObject jsonObject = jsonParser.parse(new Message(taskGroup).toJson()).getAsJsonObject();
            jsonObject.get("data").getAsJsonObject().addProperty("taskCount", count);
            return jsonObject.toString();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

    @RequestMapping(value = "/task-group/merge", method = POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String mergeTaskGroups(@RequestBody String json) {
        try {
            JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
            String name = jsonObject.get("name").getAsString();
            JsonArray array = jsonObject.get("mergeIds").getAsJsonArray();
            int[] mergeIds = new int[array.size()];
            for (int i = 0; i < mergeIds.length; i++) {
                mergeIds[i] = array.get(i).getAsInt();
            }
            TaskGroup taskGroup = taskService.mergeTaskGroups(name, mergeIds);
            int count = taskService.getTaskCountByGroupId(taskGroup.getId());
            jsonObject = jsonParser.parse(new Message(taskGroup).toJson()).getAsJsonObject();
            jsonObject.get("data").getAsJsonObject().addProperty("taskCount", count);
            return jsonObject.toString();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

    @RequestMapping(value = "/task-group/{id}", method = DELETE, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String deleteTaskGroup(@PathVariable("id") int id) {
        try {
            taskService.deleteTaskGroupByGroupId(id);
            return new Message(true, null).toJson();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

    @RequestMapping(value = "/task-group/{id}", method = GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getTasks(@PathVariable("id") int id) {
        try {
            return new Message(taskService.getTasksByGroupId(id)).toJson();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

    @RequestMapping(value = "/task-group", method = GET, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getTaskGroups() {
        try {
            List<TaskGroup> taskGroupList = taskService.getTaskGroups();
            JsonObject message = jsonParser.parse(new Message(taskGroupList).toJson()).getAsJsonObject();
            JsonArray jsonArray = message.getAsJsonArray("data");
            jsonArray.forEach(jsonElement -> {
                JsonObject data = jsonElement.getAsJsonObject();
                int groupId = data.get("id").getAsInt();
                data.addProperty("taskCount", taskService.getTaskCountByGroupId(groupId));
            });
            return message.toString();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

    @RequestMapping(value = "/task-group/{id}", method = PUT, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String updateTaskGroupName(@PathVariable("id") int id, @RequestParam("name") String name) {
        try {
            taskService.updateTaskGroupName(name, id);
            return new Message(true, null).toJson();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

    @RequestMapping(value = "/task-group/launch/{groupId}/{spiderId}", method = POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String launchTask(@PathVariable("groupId") int groupId, @PathVariable("spiderId") int spiderId) {
        try {
            Spider spider = new Spider();
            spider.setId(spiderId);
            spider.setGroupId(groupId);
            assemblyService.initSpider(spider, groupId);
            return new Message(true, null).toJson();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

    @RequestMapping(value = "/task-group/unbind/{groupId}", method = DELETE, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String unbindTask(@PathVariable("groupId") int groupId) {
        try {
            assemblyService.unbindSpider(groupId);
            return new Message(true, null).toJson();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

    @RequestMapping(value = "/task-group/start/{groupId}", method = PUT, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String startSpider(@PathVariable("groupId") int groupId) {
        try {
            spiderService.startSpider(groupId);
            return new Message(true, null).toJson();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

    @RequestMapping(value = "/task-group/stop/{groupId}", method = PUT, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String stopSpider(@PathVariable("groupId") int groupId) {
        try {
            spiderService.stopSpider(groupId);
            return new Message(true, null).toJson();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }
}
