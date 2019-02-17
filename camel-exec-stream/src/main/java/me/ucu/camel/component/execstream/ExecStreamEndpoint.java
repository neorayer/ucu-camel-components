package me.ucu.camel.component.execstream;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;

import java.net.URISyntaxException;

/**
 * The execStream component can be used to execute OS system commands and
 * consume output as stream.
 * <p>
 * Created by Rui Zhou on 2019/2/17.
 */
@UriEndpoint(firstVersion = "2.3.0",
        scheme = "META-INF/services/org/apache/camel/component/exec-stream",
        title = "Exec Stream",
        syntax = "exec-stream:direction",
        producerOnly = true,
        label = "system")
public class ExecStreamEndpoint extends DefaultEndpoint {

    @UriPath(enums = "in,out")
    @Metadata(required = "true")
    private String direction;

    @UriParam
    private String workingDir;

    @UriParam
    private String command;

    @UriParam
    private boolean repeat = false;

    @UriParam
    private int delayMs = 1000;

    public ExecStreamEndpoint(String uri, ExecStreamComponent component) {
        super(uri, component);
    }

    public ExecStreamEndpoint() {
    }

    @Override
    public Producer createProducer() throws Exception {
        throw new UnsupportedOperationException("Producer not supported for ExecEndpoint!");
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new ExecStreamConsumer(this, processor);
    }

    @Override
    protected String createEndpointUri() {
        return String.format("cli-stream:%s?command=%s", direction, command);
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    ////////// Getter and Setter /////////

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public int getDelayMs() {
        return delayMs;
    }

    public void setDelayMs(int delayMs) {
        this.delayMs = delayMs;
    }
}


