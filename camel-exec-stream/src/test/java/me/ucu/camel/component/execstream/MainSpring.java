package me.ucu.camel.component.execstream;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Created by Rui Zhou on 2019/2/17.
 */
@SpringBootApplication
public class MainSpring implements CommandLineRunner {

    @Autowired
    private ExecStreamEndpoint execStreamEndpoint;

    @Bean
    @ConfigurationProperties("endpoints.exec-stream")
    public ExecStreamEndpoint execStreamEndpoint() {
        return new ExecStreamEndpoint();
    }

    @Override
    public void run(String... args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        execStreamEndpoint.setCamelContext(context);
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from(execStreamEndpoint()).to("stream:out");
            }
        });

        context.start();

        synchronized (MainSpring.class) {
            MainSpring.class.wait();
        }
    }


    public static void main(String[] args) {
        SpringApplication.run(MainSpring.class, args);
    }
}
