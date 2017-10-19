package cc.gospy.chollima.dao.impl;

import cc.gospy.chollima.dao.SpiderDao;
import cc.gospy.chollima.entity.bean.Spider;
import cc.gospy.chollima.entity.bean.mapper.SpiderMapper;
import cc.gospy.chollima.util.MyBatisUtil;
import cc.gospy.chollima.util.StringUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class SpiderDaoImpl implements SpiderDao {

    @Override
    public Spider addSpider(Spider spider) {
        final boolean[] finished = {false};
        final Spider[] ret = new Spider[1];
        MyBatisUtil.execute(SpiderMapper.class, mapper -> {
            try {
                while (mapper.nameOccupied(spider.getName())) {
                    spider.setName(StringUtil.concatAutoIncreasedSuffix(spider.getName()));
                }
                mapper.insert(spider);
                ret[0] = mapper.selectByName(spider.getName());
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
        return ret[0];
    }

    @Override
    public boolean deleteSpider(Spider spider) {
        final boolean[] ret = {false};
        final boolean[] finished = new boolean[1];
        MyBatisUtil.execute(SpiderMapper.class, mapper -> {
            try {
                Spider s = mapper.selectById(spider.getId());
                if (s == null) {
                    throw new RuntimeException("spider not exists");
                }
                mapper.deleteById(spider.getId());
                if (mapper.selectById(spider.getId()) != null) {
                    ret[0] = false;
                } else {
                    ret[0] = true;
                }
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
        return ret[0];
    }

    @Override
    public List<Spider> listSpiders() {
        final boolean[] finished = {false};
        List<Spider> spiders = new ArrayList<>();
        MyBatisUtil.execute(SpiderMapper.class, mapper -> {
            try {
                spiders.addAll(mapper.selectAll());
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
        return spiders;
    }

    @Override
    public Spider getSpiderById(int id) {
        final boolean[] finished = {false};
        final Spider[] ret = new Spider[1];
        MyBatisUtil.execute(SpiderMapper.class, mapper -> {
            try {
                ret[0] = mapper.selectById(id);
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
        return ret[0];
    }

    @Override
    public void updateSpiderById(Spider spider) {
        final boolean[] finished = {false};
        MyBatisUtil.execute(SpiderMapper.class, mapper -> {
            try {
                mapper.updateById(spider);
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
    }
}
