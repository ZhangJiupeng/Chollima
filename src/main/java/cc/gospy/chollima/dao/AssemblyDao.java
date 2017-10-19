package cc.gospy.chollima.dao;

import cc.gospy.chollima.entity.bean.Assembly;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssemblyDao {
    void addAssemblyRelationship(Assembly assembly);

    void deleteAssemblyRelationship(Assembly assembly);

    List<Assembly> getAssemblyRelationshipsBySpiderId(int spiderId);
}
