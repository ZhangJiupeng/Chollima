package cc.gospy.ext;

import cc.gospy.core.entity.Page;
import cc.gospy.core.entity.Result;
import cc.gospy.core.pipeline.PipeException;
import cc.gospy.core.pipeline.Pipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleFilePipeline implements Pipeline {
    private static final Logger logger = LoggerFactory.getLogger(SimpleFilePipeline.class);

    private cc.gospy.core.pipeline.impl.SimpleFilePipeline simpleFilePipeline;

    private SimpleFilePipeline(String basePath) {
        this.simpleFilePipeline = cc.gospy.core.pipeline.impl.SimpleFilePipeline.custom().setBasePath(basePath).build();
    }

    public static SimpleFilePipeline getDefault() {
        return new Builder().build();
    }

    public static Builder custom() {
        return new cc.gospy.ext.SimpleFilePipeline.Builder();
    }

    @Override
    public void pipe(Result<?> result) throws PipeException {
        if (result.getData() != null && result.getData().getClass() == byte[].class) {
            simpleFilePipeline.pipe(result);
        } else {
            Result<byte[]> r = new Result<>();
            Page page = result.getPage();
            if (page != null) {
                r.setPage(page);
                r.setData(page.getContent());
                simpleFilePipeline.pipe(r);
            } else {
                logger.warn("result.page not found. result.data: {}", r.getData());
            }
        }
    }

    @Override
    public Class getAcceptedDataType() {
        return Object.class;
    }

    public static class Builder {
        private String basePath;

        public Builder setBasePath(String basePath) {
            this.basePath = basePath;
            return this;
        }

        public SimpleFilePipeline build() {
            return new SimpleFilePipeline(basePath);
        }
    }
}
