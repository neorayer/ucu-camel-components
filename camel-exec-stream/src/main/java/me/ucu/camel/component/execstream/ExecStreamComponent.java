package me.ucu.camel.component.execstream;

import java.net.URLDecoder;
import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;

public class ExecStreamComponent extends DefaultComponent {

    public ExecStreamComponent() {
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        ExecStreamEndpoint endpoint = new ExecStreamEndpoint(uri, this);
        setProperties(endpoint, parameters);
        endpoint.setDirection(URLDecoder.decode(remaining, "UTF-8"));

        return endpoint;
    }
}