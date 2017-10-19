package cc.gospy.chollima.entity.component.impl;

import cc.gospy.chollima.entity.component.Component;
import cc.gospy.core.entity.Result;
import cc.gospy.core.entity.Task;
import cc.gospy.core.processor.Processor;
import cc.gospy.core.processor.impl.JSoupProcessor;
import cc.gospy.core.util.StringHelper;
import org.jsoup.nodes.Element;

import java.util.Collection;
import java.util.LinkedHashSet;

public class FullLinkProcessComponent extends Component<Processor> {

    public FullLinkProcessComponent(String name) {
        super(name);
    }

    @Override
    public void loadCore(String configJson) {
        this.core = JSoupProcessor.custom()
                .setDocumentExtractor((page, document) -> {
                    if (page.getStatusCode() != 200) {
                        return null;
                    }
                    Task task = page.getTask();
                    Collection<Task> tasks = new LinkedHashSet<>();
                    for (Element element : document.select("[href]")) {
                        String link = element.attr("href");
                        if (link != null && !link.equals("")) {
                            tasks.add(new Task(StringHelper.toAbsoluteUrl(task.getProtocol(), task.getHost(), task.getUrl(), link)));
                        }
                    }
                    for (Element element : document.select("[src]")) {
                        String link = element.attr("src");
                        if (link != null && !link.equals("")) {
                            tasks.add(new Task(StringHelper.toAbsoluteUrl(task.getProtocol(), task.getHost(), task.getUrl(), link)));
                        }
                    }
                    return new Result<>(tasks, null);
                })
                .build();
    }

    public static String getDefaultConfigJson() {
        return "{}";
    }

    public static String getTemplateName() {
        return "Full Link Processor";
    }
}
