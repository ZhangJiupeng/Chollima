package cc.gospy.chollima.dao;

import cc.gospy.chollima.entity.bean.Spider;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpiderDao {
    Spider addSpider(Spider spider);

    boolean deleteSpider(Spider spider);

    List<Spider> listSpiders();

    Spider getSpiderById(int id);

    void updateSpiderById(Spider spider);

}
