package cc.gospy.chollima.api;

import cc.gospy.chollima.entity.Message;
import cc.gospy.chollima.service.SpiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping("/dashboard/**")
public class DashboardController {

    @Autowired
    private SpiderService spiderService;

    @RequestMapping(value = "/spider/{groupId}", method = GET, produces = "application/json;charset=UTF-8")
    public String getSpiderStatus(@PathVariable("groupId") int groupId) {
        try {
            return new Message(spiderService.getSpiderStatus(groupId)).toJson();
        } catch (Exception e) {
            return new Message(false, e.getMessage()).toJson();
        }
    }

}
