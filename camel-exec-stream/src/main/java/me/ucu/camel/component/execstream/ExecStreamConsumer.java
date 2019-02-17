package me.ucu.camel.component.execstream;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;

/**
 * Exec producer.
 *
 * @see {link Producer}
 */
public class ExecStreamConsumer extends DefaultConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(ExecStreamConsumer.class);

    private final ExecStreamEndpoint endpoint;

    private ExecutorService executor;

    public ExecStreamConsumer(ExecStreamEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        executor = endpoint.getCamelContext().getExecutorServiceManager()
                .newSingleThreadExecutor(this, endpoint.getEndpointUri());
        executor.execute(this::run);
    }

    private BufferedReader bufferedReader(InputStream is) {
        return new BufferedReader(new InputStreamReader(is, Charset.forName("GBK")));
    }

    @Override
    public void doStop() throws Exception {
        if (executor != null) {
            endpoint.getCamelContext().getExecutorServiceManager().shutdownNow(executor);
            executor = null;
        }
        super.doStop();
    }

    private void run() {
        while (isRunAllowed()) {
            try {
                runOnce();
                if (endpoint.isRepeat()) {
                    Thread.sleep(endpoint.getDelayMs());
                }else {
                    break;
                }
            } catch (Exception e) {
                getExceptionHandler().handleException(e);
                try {
                    Thread.sleep(endpoint.getDelayMs());
                } catch (InterruptedException e1) {
                    getExceptionHandler().handleException(e1);
                }
            }
        }
    }

    private void runOnce() throws Exception {
        String command = endpoint.getCommand();
        LOG.info("execute process command: " + command);
        Process process = Runtime.getRuntime().exec(endpoint.getCommand());
        try (BufferedReader br = bufferedReader(process.getInputStream())) {
            while (isRunAllowed()) {
                String line = br.readLine();
                if (line == null) {
                    LOG.info("Stream is end");
                    break;
                }
                Exchange exchange = endpoint.createExchange();
                exchange.getIn().setBody(line);
                getProcessor().process(exchange);
            }
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            String errMsg = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
            throw new Exception(errMsg);
        }
    }
}