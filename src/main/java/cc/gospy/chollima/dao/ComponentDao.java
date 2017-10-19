package cc.gospy.chollima.dao;

import cc.gospy.chollima.entity.bean.Component;
import cc.gospy.chollima.entity.component.Component.Type;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComponentDao {
    Component addComponent(Component component);

    boolean deleteComponent(Component component);

    List<Component> listComponents(Type type);

    List<Component> listComponents();

    boolean increaseReference(Component component);

    boolean decreaseReference(Component component);

    Component getComponentById(int id);
}
