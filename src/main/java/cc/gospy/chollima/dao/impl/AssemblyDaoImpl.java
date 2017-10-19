package cc.gospy.chollima.dao.impl;

import cc.gospy.chollima.dao.AssemblyDao;
import cc.gospy.chollima.entity.bean.Assembly;
import cc.gospy.chollima.entity.bean.mapper.AssemblyMapper;
import cc.gospy.chollima.util.MyBatisUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AssemblyDaoImpl implements AssemblyDao {
    @Override
    public void addAssemblyRelationship(Assembly assembly) {
        final boolean[] finished = {false};
        MyBatisUtil.execute(AssemblyMapper.class, mapper -> {
            try {
                mapper.insert(assembly);
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
    }

    @Override
    public void deleteAssemblyRelationship(Assembly assembly) {
        final boolean[] finished = {false};
        MyBatisUtil.execute(AssemblyMapper.class, mapper -> {
            try {
                mapper.delete(assembly);
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
    }

    @Override
    public List<Assembly> getAssemblyRelationshipsBySpiderId(int spiderId) {
        final boolean[] finished = {false};
        final List<Assembly> assemblies = new ArrayList<>();
        MyBatisUtil.execute(AssemblyMapper.class, mapper -> {
            try {
                assemblies.addAll(mapper.selectAllBySpiderId(spiderId));
            } finally {
                finished[0] = true;
            }
        });
        while (!finished[0]) ;
        return assemblies;
    }
}
