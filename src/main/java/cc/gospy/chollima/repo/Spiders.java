package cc.gospy.chollima.repo;

import cc.gospy.chollima.util.jdbc.util.JDBCUtil;
import cc.gospy.core.Gospy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Spiders {
    private static final Logger logger = LoggerFactory.getLogger(Spiders.class);

    static {
        logger.info("Start spider status initialization");
        JDBCUtil.executeUpdate("update task_group set status = 0");
        JDBCUtil.executeUpdate("update spiders set gid = 0");
        logger.info("Spiders status initialized");
    }

    public static final Map<Integer, Gospy> gospyMap = new HashMap<>();
}
