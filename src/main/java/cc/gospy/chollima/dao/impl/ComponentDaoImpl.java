package cc.gospy.chollima.dao.impl;

import cc.gospy.chollima.dao.ComponentDao;
import cc.gospy.chollima.entity.bean.Component;
import cc.gospy.chollima.entity.bean.mapper.ComponentMapper;
import cc.gospy.chollima.entity.component.Component.Type;
import cc.gospy.chollima.util.MyBatisUtil;
import cc.gospy.chollima.util.StringUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ComponentDaoImpl implements ComponentDao {
    @Override
    public Component addComponent(Component component) {
        final boolean[] finished = {false};
        final Component[] ret = new Component[1];
        MyBatisUtil.execute(ComponentMapper.class, mapper -> {
            try {
                while (mapper.nameOccupied(component.getName())) {
                    component.setName(StringUtil.concatAutoIncreasedSuffix(component.getName()));
                }
                mapper.insert(component);
                ret[0] = mapper.selectByValues(component);
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
        return ret[0];
    }

    @Override
    public boolean deleteComponent(Component component) {
        final boolean[] finished = {false};
        final boolean[] ret = {false};
        MyBatisUtil.execute(ComponentMapper.class, mapper -> {
            try {
                Component c = mapper.selectById(component.getId());
                if (c == null) {
                    throw new RuntimeException("component [id=" + component.getId() + "] not exists");
                } else {
                    if (c.getReference() > 0) {
                        throw new RuntimeException("cannot delete components in use");
                    }
                }
                mapper.deleteById(component.getId());
                if (mapper.selectById(component.getId()) == null) {
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
    public List<Component> listComponents(Type type) {
        final boolean[] finished = {false};
        final List<Component> components = new ArrayList<>();
        MyBatisUtil.execute(ComponentMapper.class, mapper -> {
            components.addAll(mapper.selectAllByType(type.getValue()));
            finished[0] = true;
        });
        while (!finished[0]) ;
        return components;
    }

    @Override
    public List<Component> listComponents() {
        final boolean[] finished = {false};
        final List<Component> components = new ArrayList<>();
        MyBatisUtil.execute(ComponentMapper.class, mapper -> {
            components.addAll(mapper.selectAll());
            finished[0] = true;
        });
        while (!finished[0]) ;
        return components;
    }

    @Override
    public boolean increaseReference(Component component) {
        final boolean[] ret = {false};
        final boolean[] finished = {false};
        MyBatisUtil.execute(ComponentMapper.class, mapper -> {
            try {
                Component c = mapper.selectById(component.getId());
                if (c == null) {
                    throw new RuntimeException("component [id=" + component.getId() + "] not exists");
                }
                c.setReference(c.getReference() + 1);
                mapper.updateReference(c);
                ret[0] = true;
            } finally {
                finished[0] = true;
            }
        });
        return ret[0];
    }

    @Override
    public boolean decreaseReference(Component component) {
        final boolean[] ret = {false};
        final boolean[] finished = {false};
        MyBatisUtil.execute(ComponentMapper.class, mapper -> {
            try {
                Component c = mapper.selectById(component.getId());
                if (c == null) {
                    throw new RuntimeException("component [id=" + component.getId() + "] not exists");
                }
                c.setReference(c.getReference() - 1);
                mapper.updateReference(c);
                ret[0] = true;
            } finally {
                finished[0] = true;
            }
        });
        return ret[0];
    }

    @Override
    public Component getComponentById(int id) {
        final boolean[] finished = {false};
        final Component[] ret = new Component[1];
        MyBatisUtil.execute(ComponentMapper.class, mapper -> {
            try {
                ret[0] = mapper.selectById(id);
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
        return ret[0];
    }

}
