package la.renzhen.basis.spring.boot;

import la.renzhen.kotlin.utils.Nets;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 让程序可以使用${port.random} ${port.random.[ID]}, ${port.random(1000)} ${port.random.[ID](1000)}<p>
 *
 * @author <a href="mailto:wo@renzhen.la">haiker</a>
 * @version 2018\4\1 0001 14:41
 */
public class PortPropertySource extends PropertySource<Integer> {

    static final String RANDOM_PROPERTY_SOURCE_NAME = "port";
    static final Pattern REGULAR_EXPRESSION = Pattern.compile("port\\.random(\\.([a-zA-Z0-9\\-\\_]+))?(\\((\\d+)\\))?");

    public PortPropertySource(String name, Integer source) {
        super(name, source);
    }

    public PortPropertySource(String name) {
        super(name, 0);
    }

    public PortPropertySource() {
        this(RANDOM_PROPERTY_SOURCE_NAME);
    }

    Map<String, Integer> ports = new HashMap<>();

    @Override
    public Object getProperty(String name) {
        if (!name.startsWith("port.random")) {
            return null;
        }
        Matcher m = REGULAR_EXPRESSION.matcher(name);
        if (m.find()) {
            String id = m.group(2);
            String startPort = m.group(4);

            if (StringUtils.hasText(id) && ports.containsKey(id)) {
                return ports.get(id);
            }
            int port = 0;
            if (StringUtils.hasText(startPort)) {
                port = Nets.INSTANCE.getAvailablePort(Integer.parseInt(startPort));
            } else {
                port = Nets.INSTANCE.randomAvailablePort();
            }
            if (StringUtils.hasText(id)) {
                ports.put(id, port);
            }
            return port;
        } else {
            return 0;
        }

    }
}
